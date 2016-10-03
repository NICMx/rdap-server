package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

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

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.EntityModel;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;
import mx.nix.rdap.core.catalog.Rol;
import mx.nix.rdap.core.catalog.Status;

/**
 * Tests for the {@link EntityModel}
 * 
 * @author dhfelix
 *
 */
public class EntityTest {

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
	 * Creates a simple entity object and store it in the database, then get the
	 * same entity from the database by its ID and Handle, finally compares the
	 * first objects with the objects in the database
	 */
	@Test
	public void insertAndGetSimpleEntity() {
		Random random = new Random();
		int randomInt = random.nextInt();

		// create local instances;
		Entity entity = createEntity(null, "rar_dhfelix" + randomInt, "www.rardhfelix" + randomInt + ".mx");

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

		// Vcard data
		VCard vCard = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		List<VCardPostalInfo> postalInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoList.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}
		vCard.setPostalInfo(postalInfoList);
		entity.getVCardList().add(vCard);

		// Store it in the database
		Long entId = null;
		try {
			entId = EntityModel.storeToDatabase(entity, connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Query the database
		Entity byId = null;
		try {
			byId = EntityModel.getById(entId, connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		Entity byHandle = null;
		try {
			byHandle = EntityModel.getByHandle(entity.getHandle(), connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Compares the results
		Assert.assertTrue("getById fails", entity.equals(byId));
		Assert.assertTrue("getByHandle fails", entity.equals(byHandle));
	}

	@Test
	public void createAndInsertComplexObject() {
		Random random = new Random();
		int randomInt = random.nextInt();

		// ----- START OF REGISTRAR -----
		// first we create a RAR
		Entity ent = new EntityDAO();

		ent.setHandle("rar_dhfelix");
		ent.setPort43("whois.dhfelixrar.mx");

		ent.getStatus().add(Status.ACTIVE);
		ent.getStatus().add(Status.VALIDATED);

		ent.getRoles().add(Rol.SPONSOR);

		PublicId pid = new PublicIdDAO();
		pid.setPublicId("Dhfelix_rar_from_mx");
		pid.setType("DUMMY REGISTRARS PUBLIC IDS");
		ent.getPublicIds().add(pid);

		// Vcard data
		VCard vCard = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);
		List<VCardPostalInfo> postalInfoList = new ArrayList<>();
		for (int i = 0; i < 2; i++) {
			postalInfoList.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + randomInt, "MX", "monterrey",
					"Luis Elizondo", null, null, "NL", "66666"));
		}

		ent.getVCardList().add(vCard);
		// ----- END OF REGISTRAR -----
		// ----- START OF ENT 1 ------
		// create local instances;
		Entity entity = createEntity(null, "rar_dhfelix" + randomInt, "www.rardhfelix" + randomInt + ".mx");

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

		randomInt = random.nextInt();
		// Vcard data
		VCard vCardEnt = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		List<VCardPostalInfo> postalInfoListEnt = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoListEnt.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}
		vCard.setPostalInfo(postalInfoListEnt);
		entity.getVCardList().add(vCardEnt);
		// ----- END OF ENT 1 ------
		// ----- START OF ENT 2 ------
		// create local instances;
		Entity entity2 = createEntity(null, "rar_dhfelix" + randomInt, "www.rardhfelix" + randomInt + ".mx");

		// Status data
		List<Status> statusList2 = new ArrayList<Status>();
		statusList2.add(Status.ACTIVE);
		statusList2.add(Status.ASSOCIATED);
		entity2.setStatus(statusList2);

		// Remarks data
		List<Remark> remList = new ArrayList<Remark>();
		Remark rem = new RemarkDAO();
		rem.setLanguage("ES");
		rem.setTitle("Prueba");
		rem.setType("PruebaType");

		List<RemarkDescription> descList2 = new ArrayList<RemarkDescription>();
		RemarkDescription desc1 = new RemarkDescriptionDAO();
		desc1.setOrder(1);
		desc1.setDescription("She sells sea shells down by the sea shore.");

		RemarkDescription desc2 = new RemarkDescriptionDAO();
		desc2.setOrder(2);
		desc2.setDescription("Originally written by Terry Sullivan.");

		descList2.add(desc1);
		descList2.add(desc2);
		rem.setDescriptions(descList2);
		remList.add(rem);
		entity2.getRemarks().addAll(remList);

		// Links data
		List<Link> linksList = new ArrayList<Link>();
		Link link2 = new LinkDAO();
		link2.setValue("http://example.net/nameserver/xxxx");
		link2.setRel("self");
		link2.setHref("http://example.net/nameserver/xxxx");
		link2.setType("application/rdap+json");
		linksList.add(link2);
		entity2.getLinks().addAll(linksList);

		// Events Data
		List<Event> eventList = new ArrayList<Event>();
		Event eve1 = new EventDAO();
		eve1.setEventAction(EventAction.REGISTRATION);
		eve1.setEventDate(new Timestamp(((new Date()).getTime())));

		Event eve2 = new EventDAO();
		eve2.setEventAction(EventAction.LAST_CHANGED);
		eve2.setEventDate(new Timestamp(((new Date()).getTime())));
		eve2.setEventActor("joe@example.com");

		// event links data
		List<Link> eventLinksList = new ArrayList<Link>();
		Link eventLink2 = new LinkDAO();
		eventLink2.setValue("eventLink1");
		eventLink2.setRel("eventlink");
		eventLink2.setHref("http://example.net/eventlink/xxxx");
		eventLink2.setType("application/rdap+json");
		eventLinksList.add(eventLink2);
		eve2.setLinks(eventLinksList);

		eventList.add(eve1);
		eventList.add(eve2);
		entity2.getEvents().addAll(eventList);

		randomInt = random.nextInt();
		// Vcard data
		VCard vCardEnt2 = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		List<VCardPostalInfo> postalInfoListEnt2 = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoListEnt2.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}
		vCardEnt2.getPostalInfo().addAll(postalInfoListEnt2);
		entity2.getVCardList().add(vCardEnt2);
		// ----- END OF ENT 2 ------

		// Store it in the database
		Long entId = null;
		Long entId2 = null;
		try {
			entId = EntityModel.storeToDatabase(entity, connection);
			entId2 = EntityModel.storeToDatabase(entity2, connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Query the database
		Entity byId = null;
		Entity byId2 = null;
		try {
			byId = EntityModel.getById(entId, connection);
			byId2 = EntityModel.getById(entId2, connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		Entity byHandle = null;
		Entity byHandle2 = null;
		try {
			byHandle = EntityModel.getByHandle(entity.getHandle(), connection);
			byHandle2 = EntityModel.getByHandle(entity2.getHandle(), connection);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		// Compares the results
		Assert.assertTrue("getById fails", entity.equals(byId));
		Assert.assertTrue("getById2 fails", entity2.equals(byId2));
		Assert.assertTrue("getByHandle fails", entity.equals(byHandle));
		Assert.assertTrue("getByHandle2 fails", entity2.equals(byHandle2));

	}

	/**
	 * Create a new instance, and set the incoming parameters. (Does not store
	 * the instance in the Database).
	 * 
	 * @param id
	 *            The id of the entity
	 * @param handle
	 *            roid of the entity
	 * @param port43
	 * @param rarId
	 * @param vCardId
	 * @return
	 */
	public static EntityDAO createEntity(Long id, String handle, String port43) {
		EntityDAO e = new EntityDAO();
		e.setId(id);
		e.setHandle(handle);
		e.setPort43(port43);
		return e;
	}

	public static Entity createDefaultEntity(Connection connection) {
		// Entity base data
		Random random = new Random();
		int randomInt = random.nextInt();

		// Create local instances
		Entity entity = createEntity(null, "ent_dhfelix", null);

		try {
			Entity byHandle = EntityModel.getByHandle(entity.getHandle(), connection);
			return byHandle;
		} catch (ObjectNotFoundException e) {
			// if not found, continue;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail();
		}

		VCard vCard = VCardTest.createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		List<VCardPostalInfo> postalInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoList.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}
		vCard.setPostalInfo(postalInfoList);

		entity.getVCardList().add(vCard);

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
		return entity;
	}

}
