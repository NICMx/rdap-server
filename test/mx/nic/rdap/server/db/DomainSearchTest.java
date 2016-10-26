package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.IpAddress;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nic.rdap.server.db.model.DomainModel;
import mx.nic.rdap.server.db.model.EntityModel;
import mx.nic.rdap.server.db.model.NameserverModel;
import mx.nic.rdap.server.db.model.ZoneModel;
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;
import mx.nix.rdap.core.catalog.Rol;
import mx.nix.rdap.core.catalog.Status;

public class DomainSearchTest extends DatabaseTest {
	
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

	/**
	 * Searches Domains by it´s name without a zone
	 * 
	 * @throws UnknownHostException
	 */
	@Test
	public void searchByName() throws UnknownHostException {
		Random random = new Random();
		createRandomDomains(random.nextInt());
		try {
			List<Domain> domains = DomainModel.searchByName("mydomaintest*", connection);
			for (Domain domain : domains) {
				System.out.println(domain.getHandle() + "\n" + domain.getNameServers().get(0).getHandle());
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Searches domains by it´s name with zone
	 * 
	 * @throws UnknownHostException
	 * @throws InvalidValueException
	 */
	@Test
	public void searchByNameWZone() throws UnknownHostException, InvalidValueException {
		Random random = new Random();
		createRandomDomains(random.nextInt());
		try {
			List<Domain> domains = DomainModel.searchByName("mydomaintest*", "lat", connection);
			for (Domain domain : domains) {
				System.out.println(domain.getHandle() + "\n" + domain.getNameServers().get(0).getHandle());
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Searches domains by it´s name server with random generated nameservers
	 * 
	 * @throws UnknownHostException
	 */
	@Test
	public void searchByNameserverName() throws UnknownHostException {
		Random random = new Random();
		createRandomDomains(random.nextInt());
		try {
			List<Domain> domains = DomainModel.searchByNsLdhName("ns1.xn--fo-5ja1*", connection);
			for (Domain domain : domains) {
				System.out.println(domain.getHandle());
				List<Nameserver> nameservers = domain.getNameServers();
				for (Nameserver ns : nameservers) {
					System.out.println("\t" + ns.getLdhName());
				}
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Searches domain´s by its nameserver exactIp
	 * 
	 * @throws UnknownHostException
	 */
	@Test
	public void searchByNsIp() throws UnknownHostException {
		try {
			List<Domain> domains = DomainModel.searchByNsIp("192.0.2.1", connection);
			for (Domain domain : domains) {
				System.out.println(domain.getHandle());
				List<Nameserver> nameservers = domain.getNameServers();
				for (Nameserver ns : nameservers) {
					System.out.println("\t" + ns.getLdhName());
				}
			}
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public static List<Domain> createRandomDomains(int randomInt) throws UnknownHostException {
		List<Domain> domains = new ArrayList<Domain>();
		Entity registrar = new EntityDAO();
		registrar.setHandle("whois" + randomInt);
		registrar.setPort43("whois.mx");
		registrar.getRoles().add(Rol.SPONSOR);

		Entity ent = new EntityDAO();
		ent.setHandle("usr_evaldez" + randomInt);
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
		List<Nameserver> nameservers = createRandomNameservers(3);
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			Domain dom = new DomainDAO();
			int rnd = random.nextInt();
			dom.getEntities().add(ent);
			dom.getEntities().add(registrar);
			dom.setHandle("domcommxewq" + rnd);
			dom.setLdhName("mydomaintest" + rnd + ".mx");

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

			List<Nameserver> sub;
			if (i < 5) {
				sub = nameservers.subList(0, random.nextInt(5));
			} else {
				sub = nameservers.subList(5, random.nextInt(4) + 5);

			}
			dom.setNameServers(sub);
			Long domId = null;
			try {
				domId = DomainModel.storeToDatabase(dom, connection);
			} catch (SQLException | IOException | RequiredValueNotFoundException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			dom.setId(domId);
			domains.add(dom);
		}
		return domains;
	}

	public static List<Nameserver> createRandomNameservers(int randomInt) throws UnknownHostException {
		List<Nameserver> nameservers = new ArrayList<Nameserver>();
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			randomInt++;
			int rnd = r.nextInt();
			Nameserver nameserver = new NameserverDAO();
			nameserver.setHandle("XXXX" + rnd);
			nameserver.setPunycodeName("ns1.xn--fo-5ja" + rnd + ".example");
			nameserver.setPort43("whois.example.net");

			// IpAddressStruct data
			NameserverIpAddressesStruct ipAddresses = new NameserverIpAddressesStruct();

			IpAddress ipv41 = new IpAddressDAO();
			ipv41.setAddress(
					InetAddress.getByName("192." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256)));
			ipv41.setType(4);
			ipAddresses.getIpv4Adresses().add(ipv41);

			IpAddress ipv42 = new IpAddressDAO();
			ipv42.setAddress(
					InetAddress.getByName("192." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256)));
			ipv42.setType(4);
			ipAddresses.getIpv4Adresses().add(ipv42);

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

			try {
				NameserverModel.storeToDatabase(nameserver, connection);
			} catch (SQLException | IOException | RequiredValueNotFoundException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			nameservers.add(nameserver);
		}
		return nameservers;

	}

	public static List<Nameserver> createNameserverWithIp(int randomInt) throws UnknownHostException {
		List<Nameserver> nameservers = new ArrayList<Nameserver>();
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			randomInt++;
			int rnd = r.nextInt();
			Nameserver nameserver = new NameserverDAO();
			nameserver.setHandle("XXXX" + rnd);
			nameserver.setPunycodeName("ns1.xn--fo-5ja" + rnd + ".example");
			nameserver.setPort43("whois.example.net");

			// IpAddressStruct data
			NameserverIpAddressesStruct ipAddresses = new NameserverIpAddressesStruct();

			IpAddress ipv41 = new IpAddressDAO();
			ipv41.setAddress(
					InetAddress.getByName("192." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256)));
			ipv41.setType(4);
			ipAddresses.getIpv4Adresses().add(ipv41);

			IpAddress ipv42 = new IpAddressDAO();
			ipv42.setAddress(
					InetAddress.getByName("192." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256)));
			ipv42.setType(4);
			ipAddresses.getIpv4Adresses().add(ipv42);

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

			try {
				NameserverModel.storeToDatabase(nameserver, connection);
			} catch (SQLException | IOException | RequiredValueNotFoundException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			nameservers.add(nameserver);
		}
		return nameservers;

	}
}
