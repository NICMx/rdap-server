
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
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;

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
	 * Validate the required attributes for the event
	 * 
	 * @param event
	 * @throws RequiredValueNotFoundException
	 */
	private static void isValidForStore(Event event) throws RequiredValueNotFoundException {
		if (event.getEventAction() == null || event.getEventAction().compareTo(EventAction.UNKNOWN) == 0)
			throw new RequiredValueNotFoundException("eventAction", "Event");
		if (event.getEventDate() == null)
			throw new RequiredValueNotFoundException("eventDate", "Event");
	}

	/**
	 * Store a Event in the Database
	 * 
	 * @param event
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static long storeToDatabase(Event event, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		isValidForStore(event);
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS)) {
			((EventDAO) event).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();// TODO Validate if the
										// insert was correct
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long eventId = result.getLong(1);// The id of the link inserted
			LinkModel.storeEventLinksToDatabase(event.getLinks(), eventId, connection);
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
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeNameserverEventsToDatabase(List<Event> events, Long nameserverId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {

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
	
	public static void storeDomainEventsToDatabase(List<Event> events, Long domainId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeDomainEventsToDatabase"))) {
			for (Event event : events) {
				Long eventId = EventModel.storeToDatabase(event, connection);
				statement.setLong(1, domainId);
				statement.setLong(2, eventId);
				logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the insert was
											// correct
			}
		}
	}

	/**
	 * Store the DsData events
	 * 
	 * @param events
	 * @param nameserverId
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException 
	 */
	public static void storeDsDataEventsToDatabase(List<Event> events, Long dsDataId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {

		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeDsDataEventsToDatabase"))) {
			for (Event event : events) {
				Long eventId = EventModel.storeToDatabase(event, connection);
				statement.setLong(1, dsDataId);
				statement.setLong(2, eventId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the insert was
											// correct
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
	
	/**
	 * Get all events for a DsData
	 * 
	 * @param domainId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Event> getByDsDataId(Long dsDataId, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDsDataId"))) {
			statement.setLong(1, dsDataId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO
																			// Manage
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
	
	/**
	 * Get all events for a Domain
	 * 
	 * @param domainId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Event> getByDomainId(Long domainId, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDomainId"))) {
			statement.setLong(1, domainId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");
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
