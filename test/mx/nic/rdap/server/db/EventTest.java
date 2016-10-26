package mx.nic.rdap.server.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.server.db.model.EventModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;

/**
 * Test for the class Event
 * 
 * @author dalpuche
 *
 */
public class EventTest extends DatabaseTest {

	@Test
	/**
	 * Store am event in the database
	 */
	public void insert() {
		try {
			Event event = new EventDAO();
			event.setEventAction(EventAction.DELETION);
			event.setEventDate(new Date());
			event.setEventActor("dalpuche");
			Link link = new LinkDAO();
			link.setValue("linkofevent.com");
			link.setHref("lele");
			event.getLinks().add(link);
			try (Connection connection = DatabaseSession.getRdapConnection()) {
				EventModel.storeToDatabase(event, connection);
			}
			assert true;
		} catch (RequiredValueNotFoundException | SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		}

	}

	@Test
	/**
	 * Test that retrieve an array of events from the DB
	 */
	public void getAll() {
		try {
			try (Connection connection = DatabaseSession.getRdapConnection()) {
				List<Event> events = EventModel.getAll(connection);
				for (Event event : events) {
					System.out.println(((EventDAO) event).toJson());
				}
			}
			assert true;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		}
	}
}
