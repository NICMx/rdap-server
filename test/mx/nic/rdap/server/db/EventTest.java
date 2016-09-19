package mx.nic.rdap.server.db;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.EventModel;

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
			// event.setEventAction("Registro");
			String formatDate = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date());
			event.setEventDate(formatDate);
			event.setEventActor("dalpuche");
			EventModel.storeToDatabase(event);
			assert true;
		} catch (SQLException | IOException e) {
			assert false;
			e.printStackTrace();
		}

	}

	@Test
	/**
	 * Test that retrieve an array of events from a Nameserver id
	 */
	public void getByNameserverId() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			List<Event> events = EventModel.getByNameServerId(1l);
			assert true;
		} catch (SQLException | IOException e) {
			assert false;
			e.printStackTrace();
		}
	}
}
