package mx.nic.rdap.server.notices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mx.nic.rdap.core.catalog.EventAction;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.server.configuration.RdapConfiguration;

public class UserEvents {

	private static final Logger logger = Logger.getLogger(UserEvents.class.getName());
	static final String EVENT_FILE_NAME = "events.xml";

	private static List<Event> userEvents;

	private static ReadWriteLock lock;

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
	static void init(String userPath) throws SAXException, IOException, ParserConfigurationException {

		try {
			userEvents = NoticesReader.parseEventsXML(Paths.get(userPath, EVENT_FILE_NAME).toString());
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + EVENT_FILE_NAME + "' not found, continue.", e);
		}

		if (RdapConfiguration.getEventsUpdateTime() > 0) {
			lock = new ReentrantReadWriteLock();
		}

	}

	public static List<Event> getEvents() {

		List<Event> events = null;
		List<Event> result = null;
		if (lock != null) {
			lock.readLock().lock();
		}

		try {
			events = userEvents;

			if (events != null) {
				result = new ArrayList<>(events);
			}
			events = null;
		} finally {
			if (lock != null) {
				lock.readLock().unlock();
			}
		}

		if (RdapConfiguration.isDbDataLive()) {
			setCurrentTimestamp(result);
		}

		return result;
	}

	private static void setCurrentTimestamp(List<Event> events) {
		for (Event e : events) {
			if (e.getEventAction().equals(EventAction.LAST_UPDATE_OF_RDAP_DATABASE)) {
				Date newDate = new Date();
				e.setEventDate(newDate);
			}
		}
	}

	public static void updateEvents(List<Event> updatedEvents) {
		if (lock != null) {
			lock.writeLock().lock();
		}
		try {
			userEvents = updatedEvents;
		} finally {
			if (lock != null) {
				lock.writeLock().unlock();
			}
		}
	}

}
