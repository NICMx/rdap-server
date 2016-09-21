package mx.nic.rdap.server.db;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.IpAddress;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.NameserverModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;
import mx.nix.rdap.core.catalog.Status;

/**
 * Test for the Nameserver object
 * 
 * @author dalpuche
 *
 */
public class NameserverTest {

	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database";

	@Test
	public void insertMinimunNameServer() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			// Nameserver base data
			Nameserver nameserver = new NameserverDAO();
			nameserver.setPunycodeName("ns.xn--test-minumun.example");
			nameserver.setRarId(1L);
			NameserverModel.storeToDatabase(nameserver);
			System.out.println(nameserver);
			assert true;
		} catch (RequiredValueNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void insert() {

		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			// Nameserver base data
			Nameserver nameserver = new NameserverDAO();
			nameserver.setHandle("XXXX3");
			nameserver.setPunycodeName("ns1.xn--fo-5ja.example");
			nameserver.setPort43("whois.example.net");
			nameserver.setRarId(1L);

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
			String formatDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
			event1.setEventDate(formatDate);

			Event event2 = new EventDAO();
			event2.setEventAction(EventAction.LAST_CHANGED);
			event2.setEventDate(formatDate);
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
			NameserverModel.storeToDatabase(nameserver);
			System.out.println(nameserver);
			assert true;
		} catch (RequiredValueNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void findByName() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			Nameserver nameserver = new NameserverDAO();
			nameserver = NameserverModel.findByName("ns1.xn--fo-5ja.example");
			System.out.println(nameserver.toString());
			assert true;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
