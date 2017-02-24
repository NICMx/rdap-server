package mx.nic.rdap.server;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mx.nic.rdap.db.exception.ObjectNotFoundException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.server.util.PrivacyUtil;
import mx.nic.rdap.server.util.Util;

@WebListener
public class RdapInitializer implements ServletContextListener {

	/** File from which we will load the renderer. */
	private static final String RENDERERS_FILE = "renderers";
	/** File from which we will load the rdap server configuration. */
	private static final String CONFIGURATION_FILE = "configuration";

	private static ServletContext servletContext;

	private static final String DEFAULT_USER_RENDERER_FILE_PATH = "WEB-INF/" + RENDERERS_FILE + ".properties";
	private static final String DEFAULT_USER_CONFIGURATION_FILE_PATH = "WEB-INF/" + CONFIGURATION_FILE + ".properties";
	private static final String DEFAULT_NOTICES_FOLDER_PATH = "WEB-INF/notices";

	private static final String RENDERER_CONTEXT_PARAM_NAME = "renderersUserPath";
	private static final String RDAP_CONFIGURATION_PARAM_NAME = "rdapConfigurationUserPath";
	public static final String PRIVACY_SETTINGS_PARAM_NAME = "privacySettingsUserPath";
	private static final String NOTICES_FOLDER_PATH_PARAM_NAME = "noticesUserPath";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		servletContext = event.getServletContext();
		try {
			loadRenderers();
			loadRdapConfiguration();
			// Validate if the configurated zones are in the database
			RdapConfiguration.validateRdapConfiguration();
			RdapConfiguration.validateConfiguratedZones();
			RdapConfiguration.validateConfiguratedRoles();
			PrivacyUtil.loadAllPrivacySettings();
			loadUserNotices();

			DataAccessService.getImplementation().init(RdapConfiguration.getServerProperties());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Nothing needed.
		servletContext = null;
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	private void loadRenderers() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Properties p = Util.loadProperties(RENDERERS_FILE);
		String userFilePath = servletContext.getInitParameter(RENDERER_CONTEXT_PARAM_NAME);
		if (userFilePath == null) {
			userFilePath = DEFAULT_USER_RENDERER_FILE_PATH;
		} else {
			Path path = Paths.get(userFilePath, RENDERERS_FILE + ".properties");
			userFilePath = path.toString();
		}

		try (InputStream inStream = servletContext.getResourceAsStream(userFilePath);) {
			if (inStream != null) {
				p.load(inStream);
			}
		}

		RendererPool.loadRenderers(p);
	}

	private void loadRdapConfiguration() throws IOException, ObjectNotFoundException {
		Properties p = Util.loadProperties(CONFIGURATION_FILE);
		String userFilePath = servletContext.getInitParameter(RDAP_CONFIGURATION_PARAM_NAME);
		if (userFilePath == null) {
			userFilePath = DEFAULT_USER_CONFIGURATION_FILE_PATH;
		} else {
			Path path = Paths.get(userFilePath, CONFIGURATION_FILE + ".properties");
			userFilePath = path.toString();
		}

		try (InputStream inStream = servletContext.getResourceAsStream(userFilePath);) {
			if (inStream != null) {
				p.load(inStream);
			}
		}

		RdapConfiguration.loadSystemProperties(p);
	}

	private void loadUserNotices() throws SAXException, IOException, ParserConfigurationException {
		String userPath = servletContext.getInitParameter(NOTICES_FOLDER_PATH_PARAM_NAME);
		if (userPath == null || userPath.trim().isEmpty()) {
			userPath = DEFAULT_NOTICES_FOLDER_PATH;
		}

		UserNotices.init(userPath);
	}

}
