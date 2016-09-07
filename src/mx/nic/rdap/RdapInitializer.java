package mx.nic.rdap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import mx.nic.rdap.db.DatabaseSession;

@WebListener
public class RdapInitializer implements ServletContextListener {

	/** File from which we will load the renderer. */
	private static final String RENDERERS_FILE = "renderers";
	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			RendererPool.loadRenderers(Util.loadProperties(RENDERERS_FILE));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// Nothing needed here.
	}

}
