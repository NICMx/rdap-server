package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.DsData;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.IpAddress;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.DomainModel;
import mx.nic.rdap.server.db.model.EntityModel;
import mx.nic.rdap.server.db.model.ZoneModel;
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;
import mx.nix.rdap.core.catalog.Rol;
import mx.nix.rdap.core.catalog.Status;
import mx.nix.rdap.core.catalog.VariantRelation;

/**
 * Test for {@link DomainModel}
 * 
 * @author dhfelix
 *
 */
public class DomainTest {

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
	public void insertAndGetSimpleDomain() {
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
		dom.setLdhName("mydomaintest.com.mx");

		Integer zoneId = null;
		try {
			zoneId = ZoneModel.storeToDatabase("mx", connection);
		} catch (SQLException e1) {
			e1.printStackTrace();
			fail(e1.toString());
		}
		dom.setZoneId(zoneId);

		SecureDNSDAO secureDNS = SecureDnsTest.getSecureDns(null, null, false, false, null);
		dom.setSecureDNS(secureDNS);

		Long domId = null;
		try {
			domId = DomainModel.storeToDatabase(dom, connection);
		} catch (SQLException | IOException | RequiredValueNotFoundException e) {
			e.printStackTrace();
			fail();
		}

		Domain domainById = null;
		Domain findByLdhName = null;
		try {
			domainById = DomainModel.getDomainById(domId, connection);
			findByLdhName = DomainModel.findByLdhName(dom.getLdhName(), connection);
		} catch (SQLException | IOException | InvalidValueException e) {
			e.printStackTrace();
			fail();
		}

		// Compares the results
		Assert.assertTrue("getById fails", dom.equals(domainById));
		Assert.assertTrue("findByLdhName fails", dom.equals(findByLdhName));
	}

	@Test
	/**
	 * Inserts a domain and retrieves it
	 */
	public void insertDomainAndGet() {
		Random random = new Random();
		int randomInt = random.nextInt();

		String domainName = "foo" + randomInt;
		DomainDAO domain = new DomainDAO();

		Entity registrar = new EntityDAO();
		registrar.setHandle("rar_dhfelix");
		registrar.setPort43("whois.dhfelixrar.mx");
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

		domain.getEntities().add(ent);
		domain.getEntities().add(registrar);
		List<Nameserver> nameservers = new ArrayList<Nameserver>();
		try {
			nameservers = createDefaultNameservers(randomInt);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		domain.setNameServers(nameservers);

		// Creates and inserts a zone
		Integer zoneId = null;
		try {
			zoneId = ZoneModel.storeToDatabase("mx", connection);
		} catch (SQLException e1) {
			e1.printStackTrace();
			fail(e1.toString());
		}

		domain.setZoneId(zoneId);
		domain.setLdhName(domainName);
		domain.setSecureDNS(SecureDnsTest.createDefaultSDNS());

		// Creates and inserts a list of variants into the domain
		List<Variant> variants = new ArrayList<Variant>();

		List<VariantRelation> relations1 = new ArrayList<VariantRelation>();
		relations1.add(VariantRelation.REGISTERED);
		relations1.add(VariantRelation.CONJOINED);
		List<VariantName> variantNames1 = new ArrayList<VariantName>();
		variantNames1.add(VariantTest.createVariantName("xn--fo-cka" + randomInt + ".mx"));
		variantNames1.add(VariantTest.createVariantName("xn--fo-fka" + randomInt + ".mx"));

		List<VariantRelation> relations2 = new ArrayList<VariantRelation>();
		relations2.add(VariantRelation.UNREGISTERED);
		relations2.add(VariantRelation.REGISTRATION_RESTRICTED);
		List<VariantName> variantNames2 = new ArrayList<VariantName>();
		variantNames2.add(VariantTest.createVariantName("xn--fo-8ja" + randomInt + ".mx"));

		variants.add(VariantTest.createVariant(null, relations1, variantNames1, null, null));
		variants.add(VariantTest.createVariant(null, relations2, variantNames2, null, ".EXAMPLE Spanish"));

		domain.getVariants().addAll(variants);

		domain.getStatus().add(Status.ACTIVE);
		domain.getStatus().add(Status.TRANSFER_PROHIBITED);

		// Creates and inserts default public id
		List<PublicId> listPids = new ArrayList<>();
		PublicId pid = new PublicIdDAO();
		pid.setPublicId("dumy pid 1");
		pid.setType("dummy iana");
		PublicId pid2 = new PublicIdDAO();
		pid.setPublicId("dumy pid 2");
		pid.setType("dummy IETF");
		listPids.add(pid);
		listPids.add(pid2);

		domain.getPublicIds().addAll(listPids);

		// Creates and inserts Remark data
		List<Remark> remarks = new ArrayList<Remark>();
		Remark remark = new RemarkDAO();
		remark.setLanguage("ES");
		remark.setTitle("Prueba");
		remark.setType("PruebaType");

		List<RemarkDescription> descriptions = new ArrayList<RemarkDescription>();
		RemarkDescription description1 = new RemarkDescriptionDAO();
		description1.setOrder(1);
		description1.setDescription("She sells sea shells down by the sea shore.");

		RemarkDescription description2 = new RemarkDescriptionDAO();
		description2.setOrder(2);
		description2.setDescription("Originally written by Terry Sullivan.");

		descriptions.add(description1);
		descriptions.add(description2);
		remark.setDescriptions(descriptions);
		remarks.add(remark);
		domain.getRemarks().addAll(remarks);

		// Links data
		List<Link> links = new ArrayList<Link>();
		Link link = new LinkDAO();
		link.setValue("http://example.net/domain/xxxx");
		link.setRel("other");
		link.setHref("http://example.net/domain/xxxx");
		link.setType("application/rdap+json");
		links.add(link);
		domain.getLinks().addAll(links);

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
		domain.getEvents().addAll(events);

		domain.setHandle(domainName + "." + ZoneModel.getZoneNameById(domain.getZoneId()));

		List<DsData> dsDataList = new ArrayList<>();
		DsData dsData = SecureDnsTest.getDsData(null, null, 66612, 1, "ABCDEF1234", 1, null, null);
		DsData dsData2 = SecureDnsTest.getDsData(null, null, 1234, 1, "abcd5432", 1, null, null);
		dsDataList.add(dsData);
		dsDataList.add(dsData2);

		SecureDNS secureDns = SecureDnsTest.getSecureDns(null, null, true, true, dsDataList);
		domain.setSecureDNS(secureDns);

		Long domainId = null;
		try {
			domainId = DomainModel.storeToDatabase(domain, connection);
		} catch (SQLException | IOException | RequiredValueNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery("SELECT * FROM rdap.domain_entity_roles");
			resultSet.next();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// Get domain By its id
		Domain domainById = null;
		Domain findByLdhName = null;
		try {
			domainById = DomainModel.getDomainById(domainId, connection);
			findByLdhName = DomainModel.findByLdhName(domain.getLdhName(), connection);
		} catch (SQLException | IOException | InvalidValueException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Compares the results
		Assert.assertTrue("getById fails", domain.equals(domainById));
		Assert.assertTrue("findByLdhName fails", domain.equals(findByLdhName));

	}

	public static List<Nameserver> createDefaultNameservers(int randomInt) throws UnknownHostException {
		List<Nameserver> nameservers = new ArrayList<Nameserver>();
		Nameserver nameserver = new NameserverDAO();
		nameserver.setHandle("XXXX73532" + randomInt);
		nameserver.setPunycodeName("ns1.xn--fo-5ja" + randomInt + ".example");
		nameserver.setPort43("whois.example.net");

		// IpAddressStruct data
		NameserverIpAddressesStruct ipAddresses = new NameserverIpAddressesStruct();

		IpAddress ipv41 = new IpAddressDAO();
		ipv41.setAddress(InetAddress.getByName("192.0.2.1"));
		ipv41.setType(4);
		ipAddresses.getIpv4Adresses().add(ipv41);

		IpAddress ipv42 = new IpAddressDAO();
		ipv42.setAddress(InetAddress.getByName("192.0.2.2"));
		ipv42.setType(4);
		ipAddresses.getIpv4Adresses().add(ipv42);

		IpAddress ipv6 = new IpAddressDAO();
		ipv6.setAddress(InetAddress.getByName("2001:db8::123"));
		ipv6.setType(6);
		ipAddresses.getIpv6Adresses().add(ipv6);
		nameserver.setIpAddresses(ipAddresses);

		// Status data
		List<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.ACTIVE);
		statusList.add(Status.ASSOCIATED);
		nameserver.setStatus(statusList);

		// Remarks data
		List<Remark> remarks = new ArrayList<Remark>();
		Remark remark = new RemarkDAO();
		remark.setLanguage("ES");
		remark.setTitle("Prueba");
		remark.setType("PruebaType");

		List<RemarkDescription> descriptions = new ArrayList<RemarkDescription>();
		RemarkDescription description1 = new RemarkDescriptionDAO();
		description1.setOrder(1);
		description1.setDescription("She sells sea shells down by the sea shore.");

		RemarkDescription description2 = new RemarkDescriptionDAO();
		description2.setOrder(2);
		description2.setDescription("Originally written by Terry Sullivan.");

		descriptions.add(description1);
		descriptions.add(description2);
		remark.setDescriptions(descriptions);
		remarks.add(remark);
		nameserver.setRemarks(remarks);

		// Links data
		List<Link> links = new ArrayList<Link>();
		Link link = new LinkDAO();
		link.setValue("http://example.net/nameserver/xxxx");
		link.setRel("self");
		link.setHref("http://example.net/nameserver/xxxx");
		link.setType("application/rdap+json");
		links.add(link);
		nameserver.setLinks(links);

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
		nameserver.setEvents(events);
		nameservers.add(nameserver);
		return nameservers;
	}

	public static Entity createDefaultEntity(Connection connection) {
		// Entity base data

		// Create local instances
		Entity entity = createEntity(null, "ent_dhfelix", null, null, null);

		try {
			Entity byHandle = EntityModel.getByHandle(entity.getHandle(), connection);
			return byHandle;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail();
		}
		// Status data
		List<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.ACTIVE);
		statusList.add(Status.ASSOCIATED);
		entity.setStatus(statusList);

		// Remarks data
		List<Remark> remarks = new ArrayList<Remark>();
		Remark remark = new RemarkDAO();
		remark.setLanguage("ES");
		remark.setTitle("Prueba");
		remark.setType("PruebaType");

		List<RemarkDescription> descriptions = new ArrayList<RemarkDescription>();
		RemarkDescription description1 = new RemarkDescriptionDAO();
		description1.setOrder(1);
		description1.setDescription("She sells sea shells down by the sea shore.");

		RemarkDescription description2 = new RemarkDescriptionDAO();
		description2.setOrder(2);
		description2.setDescription("Originally written by Terry Sullivan.");

		descriptions.add(description1);
		descriptions.add(description2);
		remark.setDescriptions(descriptions);
		remarks.add(remark);
		entity.getRemarks().addAll(remarks);

		// Links data
		List<Link> links = new ArrayList<Link>();
		Link link = new LinkDAO();
		link.setValue("http://example.net/nameserver/xxxx");
		link.setRel("self");
		link.setHref("http://example.net/nameserver/xxxx");
		link.setType("application/rdap+json");
		links.add(link);
		entity.getLinks().addAll(links);

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
		entity.getEvents().addAll(events);

		// PublicId data
		List<PublicId> listPids = new ArrayList<>();
		PublicId pid = new PublicIdDAO();
		pid.setPublicId("dumy pid 1");
		pid.setType("dummy iana");
		PublicId pid2 = new PublicIdDAO();
		pid.setPublicId("dumy pid 2");
		pid.setType("dummy IETF");
		listPids.add(pid);
		listPids.add(pid2);

		entity.getPublicIds().addAll(listPids);

		List<Rol> listRoles = new ArrayList<>();
		Rol rol = Rol.REGISTRAR;
		listRoles.add(rol);
		return entity;
	}

	public static EntityDAO createEntity(Long id, String handle, String port43, Long rarId, Long vCardId) {
		EntityDAO e = new EntityDAO();
		e.setId(id);
		e.setHandle(handle);
		e.setPort43(port43);
		return e;
	}
}
