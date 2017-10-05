package mx.nic.rdap.server.listener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.server.configuration.RdapConfiguration;
import mx.nic.rdap.server.notices.UserNotices;
import mx.nic.rdap.server.renderer.RendererPool;
import mx.nic.rdap.server.util.PrivacyUtil;
import mx.nic.rdap.server.util.Util;

@WebListener
public class RdapInitializer implements ServletContextListener {

	/** File from which we will load the renderer. */
	private static final String RENDERERS_FILE = "renderers";
	/** File from which we will load the rdap server configuration. */
	private static final String CONFIGURATION_FILE = "configuration";
	/**
	 * File from which we will load the configuration of the data access
	 * implementation.
	 */
	private static final String DATA_ACCESS_FILE = "data-access";

	private static ServletContext servletContext;

	private static final String DEFAULT_NOTICES_FOLDER_PATH = "/WEB-INF/notices";

	private static final String RENDERER_CONTEXT_PARAM_NAME = "renderersUserPath";
	private static final String RDAP_CONFIGURATION_PARAM_NAME = "rdapConfigurationUserPath";
	public static final String PRIVACY_SETTINGS_PARAM_NAME = "privacySettingsUserPath";
	private static final String DATA_ACCESS_PARAM_NAME = "dataAccessUserPath";
	private static final String NOTICES_FOLDER_PATH_PARAM_NAME = "noticesUserPath";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		servletContext = event.getServletContext();
		try {
			RendererPool.loadRenderers(loadConfig(RENDERERS_FILE, RENDERER_CONTEXT_PARAM_NAME));
			Properties serverConfig = loadConfig(CONFIGURATION_FILE, RDAP_CONFIGURATION_PARAM_NAME);
			RdapConfiguration.loadSystemProperties(serverConfig);

			RdapConfiguration.loadRdapConfiguration();
			RdapConfiguration.loadConfiguredOwnerRoles();
			PrivacyUtil.loadAllPrivacySettings();
			loadUserNotices();

			Properties dataAccessConfig = loadConfig(DATA_ACCESS_FILE, DATA_ACCESS_PARAM_NAME);
			for (Entry<Object, Object> entry : dataAccessConfig.entrySet()) {
				serverConfig.put(entry.getKey(), entry.getValue());
			}
			DataAccessService.initialize(serverConfig);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		servletContext = null;
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	private Properties loadConfig(String baseFileName, String pathParamName) throws IOException {
		// First, load the default values (from META-INF).
		Properties p = Util.loadProperties(baseFileName);

		// Then, override with whatever the user set up.
		String userFilePath = servletContext.getInitParameter(pathParamName);
		if (userFilePath == null) {
			userFilePath = "WEB-INF/" + baseFileName + ".properties";
		} else {
			Path path = Paths.get(userFilePath, baseFileName + ".properties");
			userFilePath = path.toString();
		}

		try (InputStream inStream = servletContext.getResourceAsStream(userFilePath);) {
			if (inStream != null) {
				p.load(inStream);
			}
		}

		return p;
	}

	private void loadUserNotices() throws SAXException, IOException, ParserConfigurationException {
		String userPath = servletContext.getInitParameter(NOTICES_FOLDER_PATH_PARAM_NAME);
		if (userPath == null || userPath.trim().isEmpty()) {
			userPath = DEFAULT_NOTICES_FOLDER_PATH;
		}

		UserNotices.init(userPath);
	}

}
