package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.DsData;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.server.db.model.DomainModel;
import mx.nic.rdap.server.db.model.EntityModel;
import mx.nic.rdap.server.db.model.SecureDNSModel;
import mx.nic.rdap.server.db.model.ZoneModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;
import mx.nix.rdap.core.catalog.Rol;

public class SecureDnsTest extends DatabaseTest {

	/**
	 * Connection for this tests
	 */
	private static Connection connection = null;

	@Before
	public void before() throws SQLException {
		connection = DatabaseSession.getRdapConnection();
	}

	@After
	public void after() throws SQLException {
		connection.rollback();
		connection.close();
	}

	@Test
	public void insertAndGetMinimum() {
		Domain dom = createSimpleDomain();
		Long domainId = dom.getId();

		SecureDNS secureDns = getSecureDns(null, domainId, true, false, null);

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
		Domain dom = createSimpleDomain();
		Long domainId = dom.getId();

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
		event1.setEventDate(new Date());

		Event event2 = new EventDAO();
		event2.setEventAction(EventAction.LAST_CHANGED);
		event2.setEventDate(new Date());
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

		SecureDNS secureDns = getSecureDns(null, domainId, true, true, dsDataList);

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

	public static SecureDNSDAO createDefaultSDNS() {
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
		event1.setEventDate(new Date());

		Event event2 = new EventDAO();
		event2.setEventAction(EventAction.LAST_CHANGED);
		event2.setEventDate(new Date());
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

		SecureDNS secureDns = getSecureDns(null, null, true, true, dsDataList);
		return (SecureDNSDAO) secureDns;
	}

	private static Domain createSimpleDomain() {

		Entity registrar = new EntityDAO();
		registrar.setHandle("whois");
		registrar.setPort43("whois.mx");
		registrar.getRoles().add(Rol.SPONSOR);

		Entity ent = new EntityDAO();
		ent.setHandle("usr_evaldez");
		ent.getRoles().add(Rol.REGISTRANT);
		ent.getRoles().add(Rol.ADMINISTRATIVE);
		ent.getRoles().add(Rol.TECHNICAL);

		try {
			EntityModel.storeToDatabase(registrar, connection);
			EntityModel.storeToDatabase(ent, connection);
		} catch (SQLException | IOException | RequiredValueNotFoundException e1) {
			e1.printStackTrace();
			fail();
		}

		try {
			EntityModel.storeToDatabase(registrar, connection);
			EntityModel.storeToDatabase(ent, connection);
		} catch (SQLException | IOException | RequiredValueNotFoundException e1) {
			e1.printStackTrace();
			fail();
		}

		Domain dom = new DomainDAO();
		dom.getEntities().add(ent);
		dom.getEntities().add(registrar);
		dom.setHandle("domcommx");
		dom.setLdhName("mydomaintest.mx");

		Integer zoneId = null;
		try {
			zoneId = ZoneModel.storeToDatabase("mx", connection);
		} catch (SQLException e1) {
			e1.printStackTrace();
			fail(e1.toString());
		}
		dom.setZoneId(zoneId);

		// SecureDNSDAO secureDNS = SecureDnsTest.getSecureDns(null, null,
		// false, false, null);
		// dom.setSecureDNS(secureDNS);

		try {
			DomainModel.storeToDatabase(dom, connection);
		} catch (SQLException | IOException | RequiredValueNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		return dom;
	}
}
