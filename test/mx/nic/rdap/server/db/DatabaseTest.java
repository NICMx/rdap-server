package mx.nic.rdap.server.db;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

/**
 * Readies the {@link DatabaseSession} module for unit test usage. If your unit
 * test does not inherit from this, your environment will not feature the data
 * sources the servlet container provides via {@link DatabaseSession}.
 */
public abstract class DatabaseTest {

	private static BasicDataSource rdapDataSource;
	private static BasicDataSource migratorDataSource;

	/**
	 * Note: As is, during unit tests, autocommit starts as false regardless of
	 * config.
	 */
	@BeforeClass
	public static void init()
			throws ParserConfigurationException, SAXException, IOException, NamingException, SQLException {
		// Parse the XML
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		ContextXmlHandler handler = new ContextXmlHandler();

		try {
			parser.parse(new File("WebContent/META-INF/context.xml"), handler);
			rdapDataSource = handler.getRdapDataSource();
			migratorDataSource = handler.getMigratorDataSource();
		} finally {
			handler.closeDataSources();
		}

		// Note: "All @AfterClass methods are guaranteed to run even if a
		// BeforeClass method throws an exception".
		// This method no longer needs to worry about closing the data sources.

		// Store the data sources in the context.
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
		Context context = new InitialContext();
		context.createSubcontext("java:");
		context.createSubcontext("java:/comp");
		context.createSubcontext("java:/comp/env");
		context.createSubcontext("java:/comp/env/jdbc");
		context.bind("java:/comp/env/jdbc/" + DatabaseSession.RDAP_DB, rdapDataSource);
		context.bind("java:/comp/env/jdbc/" + DatabaseSession.MIGRATION_DB, migratorDataSource);
	}

	@AfterClass
	public static void end() throws SQLException, NamingException {
		if (rdapDataSource != null) {
			rdapDataSource.close();
			rdapDataSource = null;
		}
		if (migratorDataSource != null) {
			migratorDataSource.close();
			migratorDataSource = null;
		}

		new InitialContext().destroySubcontext("java:");
	}

}
