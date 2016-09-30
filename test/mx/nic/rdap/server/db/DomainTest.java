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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.core.db.Zone;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.DomainModel;
import mx.nic.rdap.server.db.model.ZoneModel;
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

	// @Test
	// public void insertAndGetComplex() {
	// RegistrarDAO registrar = RegistrarTest.getDefaultRegistrar(connection);
	// Entity entity = EntityTest.createDefaultEntity(connection);
	//
	// entity.setRegistrar(null);
	//
	// entity.getRoles().add(Rol.TECHNICAL);
	// entity.getRoles().add(Rol.REGISTRANT);
	// entity.getRoles().add(Rol.ADMINISTRATIVE);
	// entity.getRoles().add(Rol.BILLING);
	//
	// Zone zone = new Zone();
	// zone.setZoneName("mx");
	//
	// try {
	// ZoneModel.storeToDatabase(zone, connection);
	// } catch (IOException | SQLException e) {
	// e.printStackTrace();
	// fail();
	// }
	//
	// DomainDAO domain = new DomainDAO();
	//
	// domain.getEntities().add(entity);
	// domain.setRegistrar(registrar);
	// domain.setRegistrarId(registrar.getId());
	// domain.setLdhName("foo");
	// domain.setZone(zone);
	// domain.setZoneId(zone.getId());
	// domain.setSecureDNS(SecureDnsTest.createDefaultSDNS());
	//
	// List<Variant> variants = new ArrayList<Variant>();
	//
	// List<VariantRelation> relations1 = new ArrayList<VariantRelation>();
	// relations1.add(VariantRelation.REGISTERED);
	// relations1.add(VariantRelation.CONJOINED);
	// List<VariantName> variantNames1 = new ArrayList<VariantName>();
	// variantNames1.add(VariantTest.createVariantName("xn--fo-cka.mx"));
	// variantNames1.add(VariantTest.createVariantName("xn--fo-fka.mx"));
	//
	// List<VariantRelation> relations2 = new ArrayList<VariantRelation>();
	// relations2.add(VariantRelation.UNREGISTERED);
	// relations2.add(VariantRelation.REGISTRATION_RESTRICTED);
	// List<VariantName> variantNames2 = new ArrayList<VariantName>();
	// variantNames2.add(VariantTest.createVariantName("xn--fo-8ja.mx"));
	//
	// variants.add(VariantTest.createVariant(null, relations1, variantNames1,
	// null, null));
	// variants.add(VariantTest.createVariant(null, relations2, variantNames2,
	// null, ".EXAMPLE Spanish"));
	//
	// domain.getVariants().addAll(variants);
	//
	// domain.getStatus().add(Status.ACTIVE);
	// domain.getStatus().add(Status.TRANSFER_PROHIBITED);
	//
	//
	// // PublicId data
	// List<PublicId> listPids = new ArrayList<>();
	// PublicId pid = new PublicIdDAO();
	// pid.setPublicId("dumy pid 1");
	// pid.setType("dummy iana");
	// PublicId pid2 = new PublicIdDAO();
	// pid.setPublicId("dumy pid 2");
	// pid.setType("dummy IETF");
	// listPids.add(pid);
	// listPids.add(pid2);
	//
	// domain.getPublicIds().addAll(listPids);
	//
	// // Remarks data
	// List<Remark> remarks = new ArrayList<Remark>();
	// Remark remark = new RemarkDAO();
	// remark.setLanguage("ES");
	// remark.setTitle("Prueba");
	// remark.setType("PruebaType");
	//
	// List<RemarkDescription> descriptions = new
	// ArrayList<RemarkDescription>();
	// RemarkDescription description1 = new RemarkDescriptionDAO();
	// description1.setOrder(1);
	// description1.setDescription("She sells sea shells down by the sea
	// shore.");
	//
	// RemarkDescription description2 = new RemarkDescriptionDAO();
	// description2.setOrder(2);
	// description2.setDescription("Originally written by Terry Sullivan.");
	//
	// descriptions.add(description1);
	// descriptions.add(description2);
	// remark.setDescriptions(descriptions);
	// remarks.add(remark);
	// entity.getRemarks().addAll(remarks);
	//
	// // Links data
	// List<Link> links = new ArrayList<Link>();
	// Link link = new LinkDAO();
	// link.setValue("http://example.net/domain/xxxx");
	// link.setRel("other");
	// link.setHref("http://example.net/domain/xxxx");
	// link.setType("application/rdap+json");
	// links.add(link);
	// domain.getLinks().addAll(links);
	//
	// // Events Data
	// List<Event> events = new ArrayList<Event>();
	// Event event1 = new EventDAO();
	// event1.setEventAction(EventAction.REGISTRATION);
	// event1.setEventDate(new Timestamp(((new Date()).getTime())));
	//
	// Event event2 = new EventDAO();
	// event2.setEventAction(EventAction.LAST_CHANGED);
	// event2.setEventDate(new Timestamp(((new Date()).getTime())));
	// event2.setEventActor("joe@example.com");
	//
	// // event links data
	// List<Link> eventLinks = new ArrayList<Link>();
	// Link eventLink = new LinkDAO();
	// eventLink.setValue("eventLink1");
	// eventLink.setRel("eventlink");
	// eventLink.setHref("http://example.net/eventlink/xxxx");
	// eventLink.setType("application/rdap+json");
	// eventLinks.add(eventLink);
	// event2.setLinks(eventLinks);
	//
	// events.add(event1);
	// events.add(event2);
	// domain.getEvents().addAll(events);
	//
	//
	// domain.setHandle("foo." + zone.getZoneName());
	//
	// DomainModel.storeToDatabase(domain, connection);
	//
	//
	//
	// }

}
