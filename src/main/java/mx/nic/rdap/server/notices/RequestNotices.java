package mx.nic.rdap.server.notices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.configuration.RdapConfiguration;
import mx.nic.rdap.server.result.ResultType;

public class RequestNotices {

	private static final Logger logger = Logger.getLogger(RequestNotices.class.getName());
	static final String DOMAIN_FILE_NAME = "domain.xml";
	static final String ENTITY_FILE_NAME = "entity.xml";
	static final String NS_FILE_NAME = "ns.xml";
	static final String AUTNUM_FILE_NAME = "autnum.xml";
	static final String IP_NETWORK_FILE_NAME = "ip.xml";
	static final String UPDATED_EXTENSION = ".updated";

	private static List<Remark> domainNotices;
	private static List<Remark> entityNotices;
	private static List<Remark> nsNotices;
	private static List<Remark> autnumNotices;
	private static List<Remark> ipNotices;

	private static ReadWriteLock lock;

	public static List<Remark> getDomainNotices() {
		return getNotices(ResultType.DOMAIN);
	}

	public static List<Remark> getEntityNotices() {
		return getNotices(ResultType.ENTITY);
	}

	public static List<Remark> getAutnumNotices() {
		return getNotices(ResultType.AUTNUM);
	}

	public static List<Remark> getNsNotices() {
		return getNotices(ResultType.NAMESERVER);
	}

	public static List<Remark> getIpNotices() {
		return getNotices(ResultType.IP);
	}

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
			domainNotices = NoticesReader.parseNoticesXML(Paths.get(userPath, DOMAIN_FILE_NAME).toString());
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + DOMAIN_FILE_NAME + "' not found, continue.", e);
		}

		try {
			entityNotices = NoticesReader.parseNoticesXML(Paths.get(userPath, ENTITY_FILE_NAME).toString());
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + ENTITY_FILE_NAME + "' not found, continue.", e);
		}

		try {
			nsNotices = NoticesReader.parseNoticesXML(Paths.get(userPath, NS_FILE_NAME).toString());
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + NS_FILE_NAME + "' not found, continue.", e);
		}

		try {
			autnumNotices = NoticesReader.parseNoticesXML(Paths.get(userPath, AUTNUM_FILE_NAME).toString());
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + AUTNUM_FILE_NAME + "' not found, continue.", e);
		}

		try {
			ipNotices = NoticesReader.parseNoticesXML(Paths.get(userPath, IP_NETWORK_FILE_NAME).toString());
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + IP_NETWORK_FILE_NAME + "' not found, continue.", e);
		}

		if (RdapConfiguration.getNoticesUpdateTime() > 0) {
			lock = new ReentrantReadWriteLock();
		}
	}

	private static List<Remark> getNotices(ResultType type) {

		List<Remark> notices = null;
		List<Remark> result = null;
		if (lock != null) {
			lock.readLock().lock();
		}

		try {
			switch (type) {
				case DOMAIN :
					notices = domainNotices;
					break;
				case ENTITY :
					notices = entityNotices;
					break;
				case AUTNUM :
					notices = autnumNotices;
					break;
				case IP :
					notices = ipNotices;
					break;
				case NAMESERVER :
					notices = nsNotices;
					break;

				default :
					notices = null;
			}

			if (notices != null) {
				result = new ArrayList<>(notices);
			}
			notices = null;
		} finally {
			if (lock != null) {
				lock.readLock().unlock();
			}
		}

		return result;
	}

	public static void updateNotices(ResultType type, List<Remark> updatedNotices) {
		if (lock != null) {
			lock.writeLock().lock();
		}
		try {
			switch (type) {
				case DOMAIN :
					domainNotices = updatedNotices;
					break;
				case ENTITY :
					entityNotices = updatedNotices;
					break;
				case AUTNUM :
					autnumNotices = updatedNotices;
					break;
				case IP :
					ipNotices = updatedNotices;
					break;
				case NAMESERVER :
					nsNotices = updatedNotices;
					break;
				default :
					break;
			}

		} finally {
			if (lock != null) {
				lock.writeLock().unlock();
			}
		}
	}

}
