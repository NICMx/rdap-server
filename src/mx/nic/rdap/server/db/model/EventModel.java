package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Statement;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.server.db.EventDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * The model for the Event object
 * 
 * @author dalpuche
 *
 */
public class EventModel {

	private final static Logger logger = Logger.getLogger(RemarkModel.class.getName());

	private final static String QUERY_GROUP = "Event";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			EventModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Store a Event in the Database
	 * 
	 * @param event
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static long storeToDatabase(Event event, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS)) {
			((EventDAO) event).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();// TODO Validate if the
										// insert was correct
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long eventId = result.getLong(1);// The id of the link inserted
			LinkModel.storeEventLinksToDatabase(event.getLinks(), eventId,connection);
			return eventId;
		}
	}

	/**
	 * Store the nameserver events
	 * 
	 * @param events
	 * @param nameserverId
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void storeNameserverEventsToDatabase(List<Event> events, Long nameserverId, Connection connection)
			throws SQLException, IOException {

		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeNameserverEventsToDatabase"))) {
			for (Event event : events) {
				Long eventId = EventModel.storeToDatabase(event, connection);
				statement.setLong(1, nameserverId);
				statement.setLong(2, eventId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
				// insert was correct
			}
		}
	}

	/**
	 * Get all events for a Nameserver
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Event> getByNameServerId(Long nameserverId, Connection connection)
			throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByNameServerId"))) {
			statement.setLong(1, nameserverId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO:
																			// Managae
																			// the
																			// exception
				}
				List<Event> events = new ArrayList<Event>();
				do {
					EventDAO event = new EventDAO(resultSet);
					event.setLinks(LinkModel.getByEventId(event.getId(), connection));
					events.add(event);
				} while (resultSet.next());
				return events;
			}
		}
	}
}
