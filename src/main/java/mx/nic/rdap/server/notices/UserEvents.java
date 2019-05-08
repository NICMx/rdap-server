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
import mx.nic.rdap.db.exception.InitializationException;
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
	 * @throws InitializationException 
	 */
	static void init(String userPath) throws SAXException, IOException, ParserConfigurationException, InitializationException {

		try {
			userEvents = NoticesReader.parseEventsXML(Paths.get(userPath, EVENT_FILE_NAME).toString());
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + EVENT_FILE_NAME + "' not found, continue. \n\t" + e);
		}

		if (RdapConfiguration.getEventsUpdateTime() >= UserNotices.MIN_TIMER_TIME) {
			lock = new ReentrantReadWriteLock();
		}
		
		if (userEvents == null || userEvents.isEmpty()) {
			return;
		}
		
		/* Duplicate events are not allowed. */
		checkForDuplicateEvents(userEvents);
	}
	
	private static void checkForDuplicateEvents(List<Event> events) throws InitializationException {
		int start = 1;
		for (Event e : events) {
			for (int i = start; i < events.size() ; i++) {
				if (e.getEventAction().equals(events.get(i).getEventAction())) {
					throw new InitializationException("Duplicate events found in events.xml");
				}
			}
			start++;
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
		} finally {
			if (lock != null) {
				lock.readLock().unlock();
			}
		}
		
		if (events != null) {
			result = new ArrayList<>(events);
		} else {
			result = new ArrayList<>();
		}

		return result;
	}

	public static void setCurrentTimestamp(List<Event> events) {
		boolean lastUpdateFound = false; 
		for (Event e : events) {
			if (e.getEventAction().equals(EventAction.LAST_UPDATE_OF_RDAP_DATABASE)) {
				Date newDate = new Date();
				e.setEventDate(newDate);
				lastUpdateFound = true;
			}
		}
		
		if (!lastUpdateFound) {
			Event lastUpdateRdapDatabase = new Event();
			lastUpdateRdapDatabase.setEventAction(EventAction.LAST_UPDATE_OF_RDAP_DATABASE);
			lastUpdateRdapDatabase.setEventDate(new Date());
			events.add(lastUpdateRdapDatabase);
		}
	}

	public static void updateEvents(List<Event> updatedEvents) {
		try {
			checkForDuplicateEvents(updatedEvents);
		} catch (InitializationException e) {
			logger.log(Level.WARNING, "The new events.xml file can not be updated.", e);
			return;
		}
		
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
