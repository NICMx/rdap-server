package mx.nic.rdap.server.notices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.configuration.RdapConfiguration;
import mx.nic.rdap.server.listener.RdapInitializer;

/**
 * Holder for the Help, Terms of services and notices remarks.
 */
public class UserNotices {

	private static Logger logger = Logger.getLogger(UserNotices.class.getName());
	
	// XML file names
	private static final String HELP_FILE_NAME = "help.xml";
	private static final String TOS_FILE_NAME = "tos.xml";
	private static final String NOTICES_FILE_NAME = "notices.xml";

	// Holder for remarks
	private static List<Remark> help;
	private static List<Remark> tos;
	private static List<Remark> notices;

	private static boolean isTimerActive = false;
	private static boolean isRdapDefaultPath;

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
	public static void init(String userPath, boolean isDefaultPath) throws SAXException, IOException, ParserConfigurationException {
		isRdapDefaultPath = isDefaultPath;
		
		
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

		RequestNotices.init(userPath);
		UserEvents.init(userPath);

		createTimerSchema(userPath);
	}

	/**
	 * Creates a timer .
	 */
	public static synchronized void createTimerSchema(String userPath) {
		long noticesTimerUpdate = RdapConfiguration.getNoticesUpdateTime();
		long userEventsTimer = RdapConfiguration.getEventsUpdateTime();

		// if both tasks are set to 0, that means the tasks are disabled.
		if (noticesTimerUpdate == 0 || userEventsTimer == 0) {
			return;
		}

		//Check if the files can be update.
		String realPath = RdapInitializer.getServletContext().getRealPath("/");
		if (realPath == null) {
			logger.log(Level.WARNING, "Can't read the path of WEB-INF, notices and event timers will not work");
			return;
		} 
		
		// If for some reason this function is called two or more times, we
		// don't want to create lot of timers.
		if (isTimerActive) {
			return;
		}

		Timer timer = new Timer("RdapUpdaterThread", true);

		if (noticesTimerUpdate > 0) {
			NoticesUpdaterTask task = new NoticesUpdaterTask(userPath);
			long millis = TimeUnit.SECONDS.toMillis(noticesTimerUpdate);
			timer.schedule(task, millis, millis);
		}
		
		if (userEventsTimer > 0) {
			EventsUpdaterTask task = new EventsUpdaterTask(userPath);
			long millis = TimeUnit.SECONDS.toMillis(noticesTimerUpdate);
			timer.schedule(task, millis, millis);
		}

		isTimerActive = true;
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
	
	static boolean isRdapDefaultPath() {
		return isRdapDefaultPath;
	}
	

}
