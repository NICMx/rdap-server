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
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.Registrar;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.RegistrarModel;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;
import mx.nix.rdap.core.catalog.Status;

/**
 * Tests for the {@link RegistrarModel}
 * 
 * @author dhfelix
 *
 */
public class RegistrarTest {

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

	/**
	 * Creates a new instance with no objects nested to it and stores it in the
	 * database, then get an instance with the id generated and compares it to
	 * see if they match.
	 */
	@Test
	public void insertAndGetSimpleRegistrar() {
		// Creates the instances to be stored
		Random random = new Random();
		int nextInt = random.nextInt();
		Registrar registrar = createRegistrar(null, "rar_dhrar" + nextInt, "www.dhrar" + nextInt + ".com.mx");

		Long registrarId = null;
		try {
			registrarId = RegistrarModel.storeToDatabase(registrar, connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Query instance from the database
		Registrar byId = null;
		try {
			byId = RegistrarModel.getById(registrarId, connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		Registrar byHandle = null;
		try {
			byHandle = RegistrarModel.getByHandle(registrar.getHandle(), connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		Long idByHandle = null;
		try {
			idByHandle = RegistrarModel.getIdByHandle(registrar.getHandle(), connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Compare the instances
		Assert.assertTrue("getById Fails", registrar.equals(byId));
		Assert.assertTrue("getByHandle Fails", registrar.equals(byHandle));
		Assert.assertTrue("getIdByHandle Fails", registrar.getId().equals(idByHandle));

	}

	/**
	 * Creates a new instance with objects nested to it and stores it in the
	 * database, then get an instance with the id generated and compares it to
	 * see if they match.
	 */
	@Test
	public void insertAndGetComplexRegistrar() {
		// Creates the instances to be stored
		Random random = new Random();
		int randomInt = random.nextInt();
		Registrar registrar = createRegistrar(null, "rar_dhrar" + randomInt, "www.dhrar" + randomInt + ".com.mx");

		VCard vCard = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		List<VCardPostalInfo> postalInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoList.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}
		vCard.setPostalInfo(postalInfoList);
		registrar.setVCardToList(vCard);

		randomInt = random.nextInt();
		vCard = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		postalInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoList.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}
		vCard.setPostalInfo(postalInfoList);
		registrar.setVCardToList(vCard);

		// Status data
		List<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.ACTIVE);
		statusList.add(Status.ASSOCIATED);
		registrar.getStatus().addAll(statusList);

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
		registrar.getRemarks().addAll(remarks);

		// Links data
		List<Link> links = new ArrayList<Link>();
		Link link = new LinkDAO();
		link.setValue("http://example.net/nameserver/xxxx");
		link.setRel("self");
		link.setHref("http://example.net/nameserver/xxxx");
		link.setType("application/rdap+json");
		links.add(link);
		registrar.getLinks().addAll(links);

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
		registrar.getEvents().addAll(events);

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

		registrar.getPublicIds().addAll(listPids);

		Long registrarId = null;
		try {
			registrarId = RegistrarModel.storeToDatabase(registrar, connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Query the instances from the database
		Registrar byId = null;
		try {
			byId = RegistrarModel.getById(registrarId, connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		Registrar byHandle = null;
		try {
			byHandle = RegistrarModel.getByHandle(registrar.getHandle(), connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		Long idByHandle = null;
		try {
			idByHandle = RegistrarModel.getIdByHandle(registrar.getHandle(), connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Compare the instances
		Assert.assertTrue("getById Fails", registrar.equals(byId));
		Assert.assertTrue("getByHandle Fails", registrar.equals(byHandle));
		Assert.assertTrue("getIdByHandle Fails", registrar.getId().equals(idByHandle));
	}

	/**
	 * Creates an instance of {@link RegistrarDAO} object with the incoming
	 * attributes.
	 * 
	 * @param id
	 * @param handle
	 * @param port43
	 * @return
	 */
	public static RegistrarDAO createRegistrar(Long id, String handle, String port43) {
		RegistrarDAO registrar = new RegistrarDAO();

		registrar.setId(id);
		registrar.setHandle(handle);
		registrar.setPort43(port43);

		return registrar;
	}

	public static RegistrarDAO getDefaultRegistrar(Connection con) {
		RegistrarDAO registrar = createRegistrar(null, "rar_rdap_test", "whois.rdaptest.com");

		RegistrarDAO result = null;
		try {
			result = (RegistrarDAO) RegistrarModel.getByHandle(registrar.getHandle(), con);
		} catch (ObjectNotFoundException e) {
			// store a new registrar if not exist.
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail();
		}

		if (result != null) {
			return result;
		}

		Random random = new Random();
		int randomInt = random.nextInt();

		VCard vCard = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		List<VCardPostalInfo> postalInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoList.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}
		vCard.setPostalInfo(postalInfoList);
		registrar.setVCardToList(vCard);

		randomInt = random.nextInt();
		vCard = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		postalInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoList.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}
		vCard.setPostalInfo(postalInfoList);
		registrar.setVCardToList(vCard);

		// Status data
		List<Status> statusList = new ArrayList<Status>();
		statusList.add(Status.ACTIVE);
		statusList.add(Status.ASSOCIATED);
		registrar.getStatus().addAll(statusList);

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
		registrar.getRemarks().addAll(remarks);

		// Links data
		List<Link> links = new ArrayList<Link>();
		Link link = new LinkDAO();
		link.setValue("http://example.net/nameserver/xxxx");
		link.setRel("self");
		link.setHref("http://example.net/nameserver/xxxx");
		link.setType("application/rdap+json");
		links.add(link);
		registrar.getLinks().addAll(links);

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
		registrar.getEvents().addAll(events);

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

		registrar.getPublicIds().addAll(listPids);

		try {
			RegistrarModel.storeToDatabase(registrar, con);
		} catch (SQLException | IOException | RequiredValueNotFoundException e) {
			e.printStackTrace();
			fail("Can't store a registrar object.");
		}

		return registrar;
	}
}
