package mx.nic.rdap.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class RdapInitializer implements ServletContextListener {

	/** File from which we will load the renderer. */
	private static final String RENDERERS_FILE = "renderers";
	/** File from which we will load the rdap server configuration. */
	private static final String CONFIGURATION_FILE = "configuration";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			RendererPool.loadRenderers(Util.loadProperties(RENDERERS_FILE));
			RdapConfiguration.loadSystemProperties(Util.loadProperties(CONFIGURATION_FILE));
			//Validate if the configurated zones are in the database
			RdapConfiguration.validateConfiguratedZones();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Nothing needed.
	}

}
