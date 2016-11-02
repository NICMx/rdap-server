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
import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.DomainModel;
import mx.nic.rdap.server.db.model.EntityModel;
import mx.nic.rdap.server.db.model.NameserverModel;
import mx.nic.rdap.server.db.model.ZoneModel;
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;
import mx.nix.rdap.core.catalog.Rol;

public class DomainSearchTest extends DatabaseTest {

	/**
	 * Connection for this tests
	 */
	private static Connection connection = null;

	@Before
	public void before() throws SQLException, IOException {
		RdapConfiguration.loadSystemProperties(Util.loadProperties("configuration"));
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

		createRandomDomains(5, 0);
		try {
			List<Domain> domains = DomainModel.searchByName("mydomaintest*", connection);
			for (Domain domain : domains) {
				System.out.println(domain.getLdhName());
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
		createRandomDomains(5, 0);
		try {
			List<Domain> domains = DomainModel.searchByName("mydomaintest*", "lat", connection);
			for (Domain domain : domains) {
				System.out.println(domain.getLdhName());
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
		createRandomDomains(3, 5);
		try {
			List<Domain> domains = DomainModel.searchByNsLdhName("ns1.xn--fo-5ja*", connection);
			for (Domain domain : domains) {
				System.out.println(domain.getLdhName());
				List<Nameserver> nameservers = domain.getNameServers();
				for (Nameserver ns : nameservers) {
					System.out.println("\t" + ns.getLdhName());
					System.out.println(ns.getIpAddresses().getIpv4Adresses().get(0).getAddress().getHostAddress());

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
	public void searchByNsIp() {

		try {
			createRandomDomains(1, 5);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		try {
			List<Domain> domains = DomainModel.searchByNsIp("192.0.2.13", connection);

			for (Domain domain : domains) {
				System.out.println(domain.getLdhName());
				for (Nameserver nameserver : domain.getNameServers()) {
					System.out.println(nameserver.getLdhName());
					System.out.println(
							nameserver.getIpAddresses().getIpv4Adresses().get(0).getAddress().getHostAddress());
				}
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public static List<Domain> createRandomDomains(int numDom, int numNs) throws UnknownHostException {
		Random random = new Random();
		List<Domain> domains = new ArrayList<Domain>();

		// Creat entity and registrar for domain
		Entity registrar = new EntityDAO();
		int randomInt = random.nextInt();
		registrar.setHandle("whois" + randomInt);
		registrar.getRoles().add(Rol.SPONSOR);

		Entity ent = new EntityDAO();
		ent.setHandle("usr_evaldez" + randomInt);

		// Create event with links for entity
		Event event = new EventDAO();
		event.setEventAction(EventAction.DELETION);
		event.setEventDate(new Date());
		event.setEventActor("dalpuche");

		Link link = new LinkDAO();
		link.setValue("linkofevent.com");
		link.setHref("lele");
		event.getLinks().add(link);

		ent.getEvents().add(event);

		try {
			EntityModel.storeToDatabase(registrar, connection);
			EntityModel.storeToDatabase(ent, connection);
		} catch (SQLException | IOException | RequiredValueNotFoundException e1) {
			e1.printStackTrace();
			fail();
		}

		// Create nameservers
		List<Nameserver> nameservers = createRandomNameservers(numNs, ent);

		// Create each domain
		for (int i = 0; i < numDom; i++) {

			Domain dom = new DomainDAO();
			int rnd = random.nextInt();
			dom.getEntities().add(ent);
			dom.getEntities().add(registrar);
			dom.setHandle("domcommxewq" + rnd);
			dom.setLdhName("mydomaintest" + rnd + ".lat");

			// Create zone
			Integer zoneId = null;
			try {
				zoneId = ZoneModel.storeToDatabase("lat", connection);
			} catch (SQLException e1) {
				e1.printStackTrace();
				fail(e1.toString());
			}
			dom.setZoneId(zoneId);

			SecureDNSDAO secureDNS = SecureDnsTest.getSecureDns(null, null, false, false, null);
			dom.setSecureDNS(secureDNS);
			List<Nameserver> sub = new ArrayList<Nameserver>();

			for (Nameserver nameserver : nameservers) {
				if (random.nextInt(10) < 5) {
					sub.add(nameserver);
				}
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

	public static List<Nameserver> createRandomNameservers(int numNs, Entity ent) throws UnknownHostException {

		List<Nameserver> nameservers = new ArrayList<Nameserver>();

		Random r = new Random();
		for (int i = 0; i < numNs; i++) {
			int rnd = r.nextInt();
			Nameserver nameserver = new NameserverDAO();
			nameserver.setHandle("XXXX" + rnd);
			nameserver.setPunycodeName("ns1.xn--fo-5ja" + rnd + ".example");
			nameserver.setPort43("whois.example.net");
			nameserver.getEntities().add(ent);

			// IpAddressStruct data
			NameserverIpAddressesStruct ipAddresses = new NameserverIpAddressesStruct();

			IpAddress ipv41 = new IpAddressDAO();
			try {
				ipv41.setAddress(InetAddress.getByName("192.0.2.13"));
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			}

			ipv41.setType(4);
			ipAddresses.getIpv4Adresses().add(ipv41);

			nameserver.setIpAddresses(ipAddresses);

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
