package mx.nic.rdap.server.notices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mx.nic.rdap.core.db.Remark;

/**
 * Holder for the Help, Terms of services and notices remarks.
 */
public class UserNotices {

	// XML file names
	private static final String HELP_FILE_NAME = "help.xml";
	private static final String TOS_FILE_NAME = "tos.xml";
	private static final String NOTICES_FILE_NAME = "notices.xml";

	// Holder for remarks
	private static List<Remark> help;
	private static List<Remark> tos;
	private static List<Remark> notices;

	/**
	 * Reads the XML files and stores the information from the XML.
	 * 
	 * @param userPath
	 *            User path that contains the xml files.
	 * @throws SAXException
	 *             When the XML file content has an invalid format.
	 * @throws IOException
	 *             Problems reading the XML file.
	 */
	public static void init(String userPath) throws SAXException, IOException, ParserConfigurationException {
		help = NoticesReader.parseHelpXML(Paths.get(userPath, HELP_FILE_NAME).toString());

		// The terms of service are optional.
		try {
			tos = NoticesReader.parseTOSXML(Paths.get(userPath, TOS_FILE_NAME).toString());
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
		}

		// The notices are optional.
		try {
			notices = NoticesReader.parseNoticesXML(Paths.get(userPath, NOTICES_FILE_NAME).toString());
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
		}
	}

	public static List<Remark> getHelp() {
		return help;
	}

	public static List<Remark> getTos() {
		return tos;
	}

	public static List<Remark> getNotices() {
		return notices;
	}

}
