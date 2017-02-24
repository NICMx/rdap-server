package mx.nic.rdap.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import mx.nic.rdap.core.db.Remark;

/**
 * Class that helps to read and validate HELP and TERMS OF SERVICE notices from
 * the user.
 */
public class NoticesReader {

	// Paths to xsd files
	private final static String HELP_XSD = "META-INF/xsd/help.xsd";
	private final static String TOS_XSD = "META-INF/xsd/tos.xsd";
	private final static String NOTICES_XSD = "META-INF/xsd/notices.xsd";

	/**
	 * Parse an XML file, validate the XML against the help.xsd and obtains the
	 * help remarks from the XML file.
	 */
	public static List<Remark> parseHelpXML(String filePath)
			throws SAXException, IOException, ParserConfigurationException {
		validateXMLWithSchema(filePath, HELP_XSD);
		return getRemarksFromFilePath(filePath);
	}

	/**
	 * Parse an XML file, validate the XML against the tos.xsd and obtains the
	 * terms of services remarks from the XML file.
	 */
	public static List<Remark> parseTOSXML(String filePath)
			throws SAXException, IOException, ParserConfigurationException {
		validateXMLWithSchema(filePath, TOS_XSD);
		return getRemarksFromFilePath(filePath);
	}

	/**
	 * Parse an XML file, validate the XML against the notices.xsd and obtains
	 * the notices remarks from the XML file.
	 */
	public static List<Remark> parseNoticesXML(String filePath)
			throws SAXException, IOException, ParserConfigurationException {
		validateXMLWithSchema(filePath, NOTICES_XSD);
		return getRemarksFromFilePath(filePath);
	}

	/**
	 * Parse an XML file and obtains the remarks objects
	 * 
	 * @return A {@link List} of {@link Remark} objects
	 */
	private static List<Remark> getRemarksFromFilePath(String filePath)
			throws IOException, ParserConfigurationException, SAXException {
		NoticesHandler handler = new NoticesHandler();
		parseXML(handler, filePath);
		return handler.getNoticesList();
	}

	/**
	 * Parses an XML with the incoming handler
	 * 
	 * @param handler
	 *            Handler that will parse the file
	 * @param xmlPath
	 *            File that will be parsed
	 * @throws IOException
	 *             Problems reading an XML file
	 * @throws SAXException
	 *             When the XML has an invalid format
	 */
	private static void parseXML(DefaultHandler handler, String xmlPath)
			throws IOException, ParserConfigurationException, SAXException {
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		SAXParser saxParser = saxFactory.newSAXParser();
		InputStream xmlStream = null;
		try {
			xmlStream = RdapInitializer.getServletContext().getResourceAsStream(xmlPath);
			if (xmlStream == null) {
				/*
				 * For some reason the ServletContext#getResourceAsStream
				 * doesn't recognize the file if an absolute path is provided.
				 */
				xmlStream = Files.newInputStream(Paths.get(xmlPath));
				if (xmlStream == null) {
					throw new FileNotFoundException(xmlPath);
				}
			}
			saxParser.parse(xmlStream, handler);
		} finally {
			if (xmlStream != null) {
				xmlStream.close();
			}
		}
	}

	/**
	 * Validates an XML against its XSD schema
	 * 
	 * @throws SAXException
	 *             when the XML has an invalid format.
	 * @throws IOException
	 *             Problems reading the XML or XSD file.
	 */
	private static void validateXMLWithSchema(String xmlPath, String xsdSchemaPath) throws SAXException, IOException {
		ClassLoader classLoader = NoticesReader.class.getClassLoader();
		URL schemaResource = classLoader.getResource(xsdSchemaPath);
		InputStream xmlStream = null;
		try {
			xmlStream = RdapInitializer.getServletContext().getResourceAsStream(xmlPath);
			if (xmlStream == null) {
				/*
				 * For some reason the ServletContext#getResourceAsStream
				 * doesn't recognize the file if an absolute path is provided.
				 */
				xmlStream = Files.newInputStream(Paths.get(xmlPath));
				if (xmlStream == null) {
					throw new FileNotFoundException(xmlPath);
				}
			}
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = schemaFactory.newSchema(schemaResource);
			Validator validator = schema.newValidator();
			StreamSource streamSource = new StreamSource(xmlStream);
			validator.validate(streamSource);
		} finally {
			if (xmlStream != null) {
				xmlStream.close();
			}
		}
	}
}
