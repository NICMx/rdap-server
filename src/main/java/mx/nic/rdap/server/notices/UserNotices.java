package mx.nic.rdap.server.notices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.db.exception.InitializationException;
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

	public static final int MIN_TIMER_TIME = 10;
	// Holder for remarks
	private static List<Remark> help;
	private static List<Remark> tos;
	private static List<Remark> notices;

	private static List<Remark> helpWithHost;
	private static List<Remark> tosWithHost;
	private static List<Remark> noticesWithHost;

	private static boolean isTimerActive = false;
	private static boolean isRdapDefaultPath;

	static String hostLinkPattern = "${host}";

	static void splitHostLinks(List<Remark> original, List<Remark> normalList, List<Remark> hostList) {
		boolean found = false;
		for (Remark r : original) {
			for (Link l : r.getLinks()) {
				String value = l.getHref();
				int indexOf = value.indexOf(hostLinkPattern);
				if (indexOf >= 0) {
					hostList.add(r);
					found = true;
					break;
				}
				indexOf = l.getValue().indexOf(hostLinkPattern);
				if (indexOf >= 0) {
					hostList.add(r);
					found = true;
					break;
				}
			}

			if (!found) {
				normalList.add(r);
			}

			found = false;
		}

		return;
	}

	/**
	 * Reads the XML files and stores the information from the XML.
	 * 
	 * @param userPath User path that contains the xml files.
	 * @throws SAXException            When the XML file content has an invalid
	 *                                 format.
	 * @throws IOException             Problems reading the XML file.
	 * @throws InitializationException
	 */
	public static void init(String userPath, boolean isDefaultPath)
			throws SAXException, IOException, ParserConfigurationException, InitializationException {
		isRdapDefaultPath = isDefaultPath;

		try {
			help = NoticesReader.parseHelpXML(Paths.get(userPath, HELP_FILE_NAME).toString());

			List<Remark> tmp = new ArrayList<Remark>();
			List<Remark> tmpWithHost = new ArrayList<Remark>();

			splitHostLinks(help, tmp, tmpWithHost);

			if (tmpWithHost.isEmpty()) {
				tmpWithHost = null;
			}

			help = tmp;
			helpWithHost = tmpWithHost;

		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.WARNING, "Optional File '" + HELP_FILE_NAME + "' not found, it is recommended to provide "
					+ "a help file , continue. \n\t" + e);

			help = new ArrayList<Remark>();
			Remark r = new Remark();
			r.setTitle("Example Help");
			List<RemarkDescription> lrd = new ArrayList<RemarkDescription>();
			r.setDescriptions(lrd);
			RemarkDescription rd = new RemarkDescription();
			rd.setDescription("Sample Help Description.");
			RemarkDescription rd1 = new RemarkDescription();
			rd1.setDescription("Contact the administrator to provide a help file.");
			lrd.add(rd);
			lrd.add(rd1);

			help.add(r);
		}

		// The terms of service are optional.
		try {
			tos = NoticesReader.parseTOSXML(Paths.get(userPath, TOS_FILE_NAME).toString());

			List<Remark> tmp = new ArrayList<Remark>();
			List<Remark> tmpWithHost = new ArrayList<Remark>();

			splitHostLinks(tos, tmp, tmpWithHost);

			if (tmpWithHost.isEmpty()) {
				tmpWithHost = null;
			}

			tos = tmp;
			tosWithHost = tmpWithHost;
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + TOS_FILE_NAME + "' not found, continue. \n\t" + e);
		}

		// The notices are optional.
		try {
			notices = NoticesReader.parseNoticesXML(Paths.get(userPath, NOTICES_FILE_NAME).toString());

			List<Remark> tmp = new ArrayList<Remark>();
			List<Remark> tmpWithHost = new ArrayList<Remark>();

			splitHostLinks(notices, tmp, tmpWithHost);

			if (tmpWithHost.isEmpty()) {
				tmpWithHost = null;
			}

			notices = tmp;
			noticesWithHost = tmpWithHost;

		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + NOTICES_FILE_NAME + "' not found, continue. \n\t" + e);
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
		if (noticesTimerUpdate < MIN_TIMER_TIME && userEventsTimer < MIN_TIMER_TIME) {
			return;
		}

		// Check if the files can be update.
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

		if (noticesTimerUpdate >= MIN_TIMER_TIME) {
			NoticesUpdaterTask task = new NoticesUpdaterTask(userPath);
			long millis = TimeUnit.SECONDS.toMillis(noticesTimerUpdate);
			timer.schedule(task, millis, millis);
			logger.log(Level.INFO, "Notices updater is active");
		}

		if (userEventsTimer >= MIN_TIMER_TIME) {
			EventsUpdaterTask task = new EventsUpdaterTask(userPath);
			long millis = TimeUnit.SECONDS.toMillis(noticesTimerUpdate);
			timer.schedule(task, millis, millis);

			logger.log(Level.INFO, "Events updater is active");
		}

		isTimerActive = true;
	}

	static String replaceHostPattern(String header, String toReplace) {
		if (toReplace == null || toReplace.isEmpty()) {
			return toReplace;
		}

		String valueResult = toReplace;
		int indexOf = toReplace.indexOf(hostLinkPattern);
		if (indexOf >= 0) {
			StringBuilder sb = new StringBuilder(toReplace);
			sb.delete(indexOf, indexOf + hostLinkPattern.length());
			sb.insert(indexOf, header);
			valueResult = sb.toString();
//			valueResult = header + toReplace.substring(indexOf + hostLinkPattern.length());
		}
		return valueResult;
	}

	static void appendRemarkWithPatternHost(List<Remark> result, List<Remark> listWithHost, String header) {
		for (Remark r : listWithHost) {
			Remark clone = new Remark();
			clone.setDescriptions(r.getDescriptions());
			clone.setLanguage(r.getLanguage());
			clone.setTitle(r.getTitle());
			clone.setType(r.getType());

			for (Link l : r.getLinks()) {
				Link cl = new Link();
				cl.setHreflang(l.getHreflang());
				cl.setMedia(l.getMedia());
				cl.setRel(l.getRel());
				cl.setTitle(l.getTitle());
				cl.setType(l.getType());

				cl.setHref(replaceHostPattern(header, l.getHref()));
				cl.setValue(replaceHostPattern(header, l.getValue()));

				clone.getLinks().add(cl);
			}

			result.add(clone);
		}
	}

	public static List<Remark> getHelp(String header) {
		if (helpWithHost == null)
			return help;

		List<Remark> result = new ArrayList<Remark>(help);

		appendRemarkWithPatternHost(result, helpWithHost, header);

		return result;
	}

	public static List<Remark> getTos(String header) {
		if (tosWithHost == null)
			return tos;

		List<Remark> result = new ArrayList<Remark>(tos);

		appendRemarkWithPatternHost(result, tosWithHost, header);

		return result;
	}

	public static List<Remark> getNotices(String header) {
		if (noticesWithHost == null)
			return notices;

		List<Remark> result = new ArrayList<Remark>(notices);

		appendRemarkWithPatternHost(result, noticesWithHost, header);

		return result;
	}

	static boolean isRdapDefaultPath() {
		return isRdapDefaultPath;
	}

}
