
package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Statement;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.server.db.EventDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.EventAction;

/**
 * The model for the Event object
 * 
 * @author dalpuche
 * @author dhfelix
 *
 */
public class EventModel {

	private final static Logger logger = Logger.getLogger(RemarkModel.class.getName());

	private final static String QUERY_GROUP = "Event";

	protected static QueryGroup queryGroup = null;

	private static final String NS_GET_QUERY = "getByNameServerId";
	private static final String DS_DATA_GET_QUERY = "getByDsDataId";
	private static final String DOMAIN_GET_QUERY = "getByDomainId";
	private static final String ENTITY_GET_QUERY = "getByEntityId";

	private static final String NS_STORE_QUERY = "storeNameserverEventsToDatabase";
	private static final String DS_DATA_STORE_QUERY = "storeDsDataEventsToDatabase";
	private static final String DOMAIN_STORE_QUERY = "storeDomainEventsToDatabase";
	private static final String ENTITY_STORE_QUERY = "storeEntityEventsToDatabase";

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
			statement.executeUpdate();
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long eventId = result.getLong(1);// The id of the link inserted
			event.setId(eventId);
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

		storeRelationEventsToDatabase(events, nameserverId, connection, NS_STORE_QUERY);
	}

	/**
	 *
	 * Store the entity events
	 * 
	 */
	public static void storeEntityEventsToDatabase(List<Event> events, Long entityId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeRelationEventsToDatabase(events, entityId, connection, ENTITY_STORE_QUERY);
	}

	/**
	 *
	 * Store the Domain events
	 * 
	 */
	public static void storeDomainEventsToDatabase(List<Event> events, Long domainId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeRelationEventsToDatabase(events, domainId, connection, DOMAIN_STORE_QUERY);
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
		storeRelationEventsToDatabase(events, dsDataId, connection, DS_DATA_STORE_QUERY);
	}

	private static void storeRelationEventsToDatabase(List<Event> events, Long id, Connection connection,
			String storeQueryId) throws SQLException, IOException, RequiredValueNotFoundException {
		if (events.isEmpty())
			return;

		String query = queryGroup.getQuery(storeQueryId);
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			for (Event event : events) {
				Long eventId = EventModel.storeToDatabase(event, connection);
				statement.setLong(1, id);
				statement.setLong(2, eventId);
				logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
				statement.executeUpdate();
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
		return getByRelationId(nameserverId, connection, NS_GET_QUERY);
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
		return getByRelationId(dsDataId, connection, DS_DATA_GET_QUERY);
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
		return getByRelationId(domainId, connection, DOMAIN_GET_QUERY);
	}

	/**
	 * Get all events for an Entity
	 */
	public static List<Event> getByEntityId(Long entityId, Connection connection) throws SQLException, IOException {
		return getByRelationId(entityId, connection, ENTITY_GET_QUERY);
	}

	private static List<Event> getByRelationId(Long id, Connection connection, String getQueryId)
			throws SQLException, IOException {
		String query = queryGroup.getQuery(getQueryId);
		List<Event> result = null;

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, id);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return Collections.emptyList();
				}
				List<Event> events = new ArrayList<Event>();
				do {
					EventDAO event = new EventDAO(resultSet);
					event.setLinks(LinkModel.getByEventId(event.getId(), connection));
					events.add(event);
				} while (resultSet.next());
				result = events;
			}
		}

		return result;
	}

}
