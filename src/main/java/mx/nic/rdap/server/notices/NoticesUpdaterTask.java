package mx.nic.rdap.server.notices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.result.ResultType;

public class NoticesUpdaterTask extends TimerTask {

	Logger logger = Logger.getLogger(NoticesUpdaterTask.class.getName());

	private String userPath;

	public NoticesUpdaterTask(String userPath) {
		this.userPath = userPath;
	}

	@Override
	public void run() {
		logger.log(Level.INFO, "Running " + NoticesUpdaterTask.class.getName());
		checkForUpdate(RequestNotices.DOMAIN_FILE_NAME, ResultType.DOMAIN);
		checkForUpdate(RequestNotices.ENTITY_FILE_NAME, ResultType.ENTITY);
		checkForUpdate(RequestNotices.NS_FILE_NAME, ResultType.NAMESERVER);
		checkForUpdate(RequestNotices.AUTNUM_FILE_NAME, ResultType.AUTNUM);
		checkForUpdate(RequestNotices.IP_NETWORK_FILE_NAME, ResultType.IP);
	}

	private void checkForUpdate(String fileName, ResultType type) {
		Path updated = Paths.get(userPath, fileName + RequestNotices.UPDATED_EXTENSION);

		List<Remark> parsedNotices;
		List<Remark> updatedNotices = new ArrayList<Remark>();
		List<Remark> updatedHostNotices = new ArrayList<Remark>();
		try {
			parsedNotices = NoticesReader.parseNoticesXML(updated.toString());
			
			UserNotices.splitHostLinks(parsedNotices, updatedNotices, updatedHostNotices);
			
			if (updatedHostNotices.isEmpty()) {
				updatedHostNotices = null;
			}
			
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

		RequestNotices.updateNotices(type, updatedNotices, updatedHostNotices);
	}

}
