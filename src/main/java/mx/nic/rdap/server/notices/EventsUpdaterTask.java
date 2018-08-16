package mx.nic.rdap.server.notices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mx.nic.rdap.core.db.Event;

public class EventsUpdaterTask extends TimerTask {

	Logger logger = Logger.getLogger(EventsUpdaterTask.class.getName());

	private String userPath;

	public EventsUpdaterTask(String userPath) {
		this.userPath = userPath;
	}

	@Override
	public void run() {
		checkForUpdate(UserEvents.EVENT_FILE_NAME);
	}

	private void checkForUpdate(String fileName) {
		Path updated = Paths.get(userPath, fileName + RequestNotices.UPDATED_EXTENSION);
		List<Event> updatedEvents;
		try {
			updatedEvents = NoticesReader.parseEventsXML(updated.toString());
		} catch (NoSuchFileException | FileNotFoundException e) {
			return;
		} catch (SAXException | ParserConfigurationException | IOException e) {
			// Nothing happens, continue
			logger.log(Level.WARNING, "Can't read and update the File '" + fileName + "'.", e);
			return;
		}

		Path target = Paths.get(userPath, fileName);
		if (UserNotices.isRdapDefaultPath()) {
			target = Paths.get(NoticesReader.getRealPath(target.toString()));
			updated = Paths.get(NoticesReader.getRealPath(updated.toString()));
		}

		try {
			Files.move(updated, target, StandardCopyOption.REPLACE_EXISTING);
			Files.deleteIfExists(updated);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Can't move the File '" + updated.toString() + "'.", e);
			return;
		}

		UserEvents.updateEvents(updatedEvents);
	}

}
