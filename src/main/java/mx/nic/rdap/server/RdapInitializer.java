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

import mx.nic.rdap.db.exception.ObjectNotFoundException;

@WebListener
public class RdapInitializer implements ServletContextListener {

	/** File from which we will load the renderer. */
	private static final String RENDERERS_FILE = "renderers";
	/** File from which we will load the rdap server configuration. */
	private static final String CONFIGURATION_FILE = "configuration";

	private static ServletContext servletContext;

	private static final String DEFAULT_RENDERER_FILE_PATH = "WEB-INF/" + RENDERERS_FILE + ".properties";
	private static final String DEFAULT_CONFIGURATION_FILE_PATH = "WEB-INF/" + CONFIGURATION_FILE + ".properties";

	private static final String RENDERER_CONTEXT_PARAM_NAME = "renderersUserPath";
	private static final String RDAP_CONFIGURATION_PARAM_NAME = "rdapConfigurationUserPath";
	public static final String PRIVACY_SETTINGS_PARAM_NAME = "privacySettingsUserPath";

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
			userFilePath = DEFAULT_RENDERER_FILE_PATH;
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
			userFilePath = DEFAULT_CONFIGURATION_FILE_PATH;
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

}
