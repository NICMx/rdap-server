package mx.nic.rdap.server.db;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mx.nic.rdap.core.db.DsData;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.SecureDNSModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;

public class SecureDnsTest {

	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database";

	/**
	 * Connection for this tests
	 */
	private static Connection connection = null;

	/**
	 * To see if autoCommit is set in the connection.
	 */
	private static boolean autoCommit = false;

	@BeforeClass
	public static void init() {
		try {
			Properties properties = Util.loadProperties(DATABASE_FILE);
			autoCommit = Boolean.parseBoolean(properties.getProperty("autoCommit"));
			DatabaseSession.init(properties);
		} catch (SQLException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Before
	public void before() {
		try {
			connection = DatabaseSession.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@After
	public void after() {
		try {
			if (!autoCommit)
				connection.rollback();
			connection.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterClass
	public static void end() {
		try {
			DatabaseSession.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void insertAndGetMinimum() {
		Long domainId = 3L;

		SecureDNS secureDns = getSecureDns(null, 3L, true, false, null);

		try {
			SecureDNSModel.storeToDatabase(secureDns, connection);
		} catch (SQLException | IOException | RequiredValueNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		SecureDNS byDomain = null;
		try {
			byDomain = SecureDNSModel.getByDomain(domainId, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail();
		}

		Assert.assertTrue("getByName fails", secureDns.equals(byDomain));
	}

	@Test
	public void insertAndGetComplex() {
		Long domainId = 3L;

		List<DsData> dsDataList = new ArrayList<>();

		// Links data
		List<Link> links = new ArrayList<Link>();
		Link link = new LinkDAO();
		link.setValue("http://example.net/nameserver/xxxx");
		link.setRel("self");
		link.setHref("http://example.net/nameserver/xxxx");
		link.setType("application/rdap+json");
		links.add(link);

		// Events Data
		List<Event> events = new ArrayList<Event>();
		Event event1 = new EventDAO();
		event1.setEventAction(EventAction.REGISTRATION);
		event1.setEventDate(new Timestamp(((new Date()).getTime())));

		Event event2 = new EventDAO();
		event2.setEventAction(EventAction.LAST_CHANGED);
		event2.setEventDate(new Timestamp(((new Date()).getTime())));
		event2.setEventActor("joe@example.com");

		// event links data
		List<Link> eventLinks = new ArrayList<Link>();
		Link eventLink = new LinkDAO();
		eventLink.setValue("eventLink1");
		eventLink.setRel("eventlink");
		eventLink.setHref("http://example.net/eventlink/xxxx");
		eventLink.setType("application/rdap+json");
		eventLinks.add(eventLink);
		event2.setLinks(eventLinks);

		events.add(event1);
		events.add(event2);

		DsData dsData = getDsData(null, null, 66612, 1, "ABCDEF1234", 1, links, events);
		DsData dsData2 = getDsData(null, null, 1234, 1, "abcd5432", 1, null, null);
		dsDataList.add(dsData);
		dsDataList.add(dsData2);

		SecureDNS secureDns = getSecureDns(null, 3L, true, true, dsDataList);

		try {
			SecureDNSModel.storeToDatabase(secureDns, connection);
		} catch (SQLException | IOException | RequiredValueNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		SecureDNS byDomain = null;
		try {
			byDomain = SecureDNSModel.getByDomain(domainId, connection);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail();
		}

		Assert.assertTrue("getByDomainId fails", secureDns.equals(byDomain));

	}

	public static SecureDNSDAO getSecureDns(Long id, Long domainId, boolean zoneSigned, boolean delegationSigned,
			List<DsData> dsData) {
		SecureDNSDAO sDns = new SecureDNSDAO();
		sDns.setId(id);
		sDns.setZoneSigned(zoneSigned);
		sDns.setDelegationSigned(delegationSigned);
		sDns.setDomainId(domainId);
		if (dsData != null)
			sDns.getDsData().addAll(dsData);

		return sDns;
	}

	public static DsDataDAO getDsData(Long id, Long secureDNSId, Integer keytag, Integer algorithm, String digest,
			Integer digestType, List<Link> links, List<Event> events) {
		DsDataDAO ds = new DsDataDAO();

		ds.setId(id);
		ds.setSecureDNSId(secureDNSId);
		ds.setKeytag(keytag);
		ds.setAlgorithm(algorithm);
		ds.setDigest(digest);
		ds.setDigestType(digestType);
		if (events != null)
			ds.getEvents().addAll(events);
		if (links != null)
			ds.getLinks().addAll(links);

		return ds;
	}

}
