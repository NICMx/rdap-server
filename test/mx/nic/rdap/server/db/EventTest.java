package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.EventModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;

/**
 * Test for the class Event
 * 
 * @author dalpuche
 *
 */
public class EventTest {

	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database";

	@Test
	/**
	 * Store am event in the database
	 */
	public void insert() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			Event event = new EventDAO();
			event.setEventAction(EventAction.DELETION);
			event.setEventDate(new Date());
			event.setEventActor("dalpuche");
			Link link = new LinkDAO();
			link.setValue("linkofevent.com");
			event.getLinks().add(link);
			try (Connection connection = DatabaseSession.getConnection()) {
				EventModel.storeToDatabase(event, connection);
			}
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
	/**
	 * Store am event in the database
	 */
	public void insertNameserverEvent() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			Event event = new EventDAO();
			event.setEventAction(EventAction.EXPIRATION);
			event.setEventDate(new Date());
			event.setEventActor("dalpuche");
			List<Event> events = new ArrayList<Event>();
			events.add(event);
			try (Connection connection = DatabaseSession.getConnection()) {
				EventModel.storeNameserverEventsToDatabase(events, 5L, connection);
			}
			assert true;
		} catch (RequiredValueNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				e.printStackTrace();
				fail();
			}
		}

	}

	@Test
	/**
	 * Test that retrieve an array of events from a Nameserver id
	 */
	public void getByNameserverId() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			try (Connection connection = DatabaseSession.getConnection()) {
				List<Event> events = EventModel.getByNameServerId(5L, connection);
				for (Event event : events) {
					System.out.println(event);
				}
			}
			assert true;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				e.printStackTrace();
				fail();
			}
		}
	}
}
