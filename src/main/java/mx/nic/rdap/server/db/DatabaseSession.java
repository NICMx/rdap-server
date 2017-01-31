package mx.nic.rdap.server.db;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import mx.nic.rdap.server.demo.ContextXmlHandler;

public class DatabaseSession {

	public static final String RDAP_DB = "rdap";

	private static DataSource getEnvironmentDataSource(String name) throws SQLException, ParserConfigurationException, SAXException {
		try {
			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			ContextXmlHandler handler = new ContextXmlHandler();
			DataSource rdapDataSource=null;
			try {
				parser.parse(new File("src/main/resources/META-INF/context.xml"), handler);
				 rdapDataSource = handler.getRdapDataSource();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			
			
			return rdapDataSource;
		} catch (NamingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static Connection getRdapConnection() throws SQLException {
		try {
			return getEnvironmentDataSource(RDAP_DB).getConnection();
		} catch (ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static DataSource getDataSource() {
		try {
			return getEnvironmentDataSource(RDAP_DB);
		} catch (SQLException | ParserConfigurationException | SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
