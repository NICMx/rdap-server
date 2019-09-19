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

	private static List<Remark> domainHostNotices;
	private static List<Remark> entityHostNotices;
	private static List<Remark> nsHostNotices;
	private static List<Remark> autnumHostNotices;
	private static List<Remark> ipHostNotices;
	
	private static ReadWriteLock lock;

	public static List<Remark> getDomainNotices(String header) {
		return getNotices(ResultType.DOMAIN, header);
	}

	public static List<Remark> getEntityNotices(String header) {
		return getNotices(ResultType.ENTITY, header);
	}

	public static List<Remark> getAutnumNotices(String header) {
		return getNotices(ResultType.AUTNUM, header);
	}

	public static List<Remark> getNsNotices(String header) {
		return getNotices(ResultType.NAMESERVER, header);
	}

	public static List<Remark> getIpNotices(String header) {
		return getNotices(ResultType.IP, header);
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
			
			List<Remark> normalList = new ArrayList<Remark>();
			List<Remark> hostList = new ArrayList<Remark>();
			
			UserNotices.splitHostLinks(domainNotices, normalList, hostList);
			
			if (hostList.isEmpty()) {
				hostList = null;
			}
			
			domainNotices = normalList;
			domainHostNotices = hostList;
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO,
					"Optional File '" + DOMAIN_FILE_NAME + "' not found, continue. \n\t" + e);
		}

		try {
			entityNotices = NoticesReader.parseNoticesXML(Paths.get(userPath, ENTITY_FILE_NAME).toString());
			
			List<Remark> normalList = new ArrayList<Remark>();
			List<Remark> hostList = new ArrayList<Remark>();
			
			UserNotices.splitHostLinks(entityNotices, normalList, hostList);
			
			if (hostList.isEmpty()) {
				hostList = null;
			}
			
			entityNotices = normalList;
			entityHostNotices = hostList;
			
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + ENTITY_FILE_NAME + "' not found, continue. \n\t" + e);
		}

		try {
			nsNotices = NoticesReader.parseNoticesXML(Paths.get(userPath, NS_FILE_NAME).toString());
			
			List<Remark> normalList = new ArrayList<Remark>();
			List<Remark> hostList = new ArrayList<Remark>();
			
			UserNotices.splitHostLinks(nsNotices, normalList, hostList);
			
			if (hostList.isEmpty()) {
				hostList = null;
			}
			
			nsNotices = normalList;
			nsHostNotices = hostList;
			
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + NS_FILE_NAME + "' not found, continue. \n\t" + e);
		}

		try {
			autnumNotices = NoticesReader.parseNoticesXML(Paths.get(userPath, AUTNUM_FILE_NAME).toString());
			
			List<Remark> normalList = new ArrayList<Remark>();
			List<Remark> hostList = new ArrayList<Remark>();
			
			UserNotices.splitHostLinks(autnumNotices, normalList, hostList);
			
			if (hostList.isEmpty()) {
				hostList = null;
			}
			
			autnumNotices = normalList;
			autnumHostNotices = hostList;
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO, "Optional File '" + AUTNUM_FILE_NAME + "' not found, continue. \n\t" + e);
		}

		try {
			ipNotices = NoticesReader.parseNoticesXML(Paths.get(userPath, IP_NETWORK_FILE_NAME).toString());
			
			List<Remark> normalList = new ArrayList<Remark>();
			List<Remark> hostList = new ArrayList<Remark>();
			
			UserNotices.splitHostLinks(ipNotices, normalList, hostList);
			
			if (hostList.isEmpty()) {
				hostList = null;
			}
			
			ipNotices = normalList;
			ipHostNotices = hostList;
		} catch (FileNotFoundException | NoSuchFileException e) {
			// Nothing happens, continue
			logger.log(Level.INFO,
					"Optional File '" + IP_NETWORK_FILE_NAME + "' not found, continue. \n\t" + e);
		}

		if (RdapConfiguration.getNoticesUpdateTime() >= UserNotices.MIN_TIMER_TIME) {
			lock = new ReentrantReadWriteLock();
		}
	}

	private static List<Remark> getNotices(ResultType type, String header) {

		List<Remark> notices = null;
		List<Remark> hostNotices = null;
		List<Remark> result = null;
		if (lock != null) {
			lock.readLock().lock();
		}

		try {
			switch (type) {
				case DOMAIN :
					notices = domainNotices;
					hostNotices = domainHostNotices;
					break;
				case ENTITY :
					notices = entityNotices;
					hostNotices = entityHostNotices;
					break;
				case AUTNUM :
					notices = autnumNotices;
					hostNotices = autnumHostNotices;
					break;
				case IP :
					notices = ipNotices;
					hostNotices = ipHostNotices;
					break;
				case NAMESERVER :
					notices = nsNotices;
					hostNotices = nsHostNotices;
					break;

				default :
					notices = null;
			}

		} finally {
			if (lock != null) {
				lock.readLock().unlock();
			}
		}

		if (notices != null) {
			result = new ArrayList<>(notices);
			notices = null;
		}
		
		if (hostNotices != null) {
			if (result == null)
				result = new ArrayList<Remark>();
			
			UserNotices.appendRemarkWithPatternHost(result, hostNotices, header);
		}

		return result;
	}

	public static void updateNotices(ResultType type, List<Remark> updatedNotices, List<Remark> updatedHostNotices) {
		if (lock != null) {
			lock.writeLock().lock();
		}
		try {
			switch (type) {
				case DOMAIN :
					domainNotices = updatedNotices;
					domainHostNotices = updatedHostNotices;
					break;
				case ENTITY :
					entityNotices = updatedNotices;
					entityHostNotices = updatedHostNotices;
					break;
				case AUTNUM :
					autnumNotices = updatedNotices;
					autnumHostNotices = updatedHostNotices;
					break;
				case IP :
					ipNotices = updatedNotices;
					ipHostNotices = updatedHostNotices;
					break;
				case NAMESERVER :
					nsNotices = updatedNotices;
					nsHostNotices = updatedHostNotices;
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
