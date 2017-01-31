package mx.nic.rdap.server.demo;

import java.sql.SQLException;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX parser handler for reading the context.xml file during testing.
 */
public class ContextXmlHandler extends DefaultHandler {

	private BasicDataSource rdapDataSource;

	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (!"Resource".equals(qName)) {
			return;
		}

		rdapDataSource = loadDataSource(attributes);
	}

	private BasicDataSource loadDataSource(Attributes attributes) {
		BasicDataSource result = new BasicDataSource();
		result.setDriverClassName(attributes.getValue("driverClassName"));
		result.setUrl(attributes.getValue("url"));
		result.setUsername(attributes.getValue("username"));
		result.setPassword(attributes.getValue("password"));
		result.setDefaultAutoCommit(false);
		return result;
	}

	public BasicDataSource getRdapDataSource() {
		BasicDataSource result = rdapDataSource;
		rdapDataSource = null;
		return result;
	}

	public void closeDataSources() throws SQLException {
		if (rdapDataSource != null) {
			rdapDataSource.close();
		}
	}
}