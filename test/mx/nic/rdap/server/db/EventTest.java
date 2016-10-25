package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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
			link.setHref("lele");
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
	 * Test that retrieve an array of events from the DB
	 */
	public void getAll() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			try (Connection connection = DatabaseSession.getConnection()) {
				List<Event> events = EventModel.getAll(connection);
				for (Event event : events) {
					System.out.println(((EventDAO) event).toJson());
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
