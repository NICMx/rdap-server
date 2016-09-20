package mx.nic.rdap.server.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.EventModel;
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
			String formatDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
			event.setEventDate(formatDate);
			event.setEventActor("dalpuche");
			try (Connection connection = DatabaseSession.getConnection()) {
				EventModel.storeToDatabase(event, connection);
			}
			assert true;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
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
			String formatDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
			event.setEventDate(formatDate);
			event.setEventActor("dalpuche");
			List<Event> events = new ArrayList<Event>();
			events.add(event);
			try (Connection connection = DatabaseSession.getConnection()) {
				EventModel.storeNameserverEventsToDatabase(events, 1L, connection);
			}
			assert true;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				EventModel.getByNameServerId(1L, connection);
			}
			assert true;
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			assert false;
		} finally {
			try {
				DatabaseSession.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
