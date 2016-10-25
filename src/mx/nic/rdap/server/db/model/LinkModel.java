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

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.server.db.LinkDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * The model for the Link object
 * 
 * @author dalpuche
 * @author dhfelix
 *
 */
public class LinkModel {

	private final static Logger logger = Logger.getLogger(LinkModel.class.getName());

	private final static String QUERY_GROUP = "Link";

	protected static QueryGroup queryGroup = null;

	private static final String NS_GET_QUERY = "getByNameServerId";
	private static final String EVENT_GET_QUERY = "getByEventId";
	private static final String DS_DATA_GET_QUERY = "getByDsDataId";
	private static final String DOMAIN_GET_QUERY = "getByDomainId";
	private static final String REMARK_GET_QUERY = "getByRemarkId";
	private static final String ENTITY_GET_QUERY = "getByEntityId";

	private static final String NS_STORE_QUERY = "storeNameserverLinksToDatabase";
	private static final String EVENT_STORE_QUERY = "storeEventLinksToDatabase";
	private static final String REMARK_STORE_QUERY = "storeRemarkLinksToDatabase";
	private static final String DS_DATA_STORE_QUERY = "storeDsDataLinksToDatabase";
	private static final String DOMAIN_STORE_QUERY = "storeDomainLinksToDatabase";
	private static final String ENTITY_STORE_QUERY = "storeEntityLinksToDatabase";

	static {
		try {
			LinkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Validate the required attributes for the link
	 * 
	 * @param link
	 * @throws RequiredValueNotFoundException
	 */
	private static void isValidForStore(Link link) throws RequiredValueNotFoundException {
		if (link.getValue() == null || link.getValue().isEmpty())
			throw new RequiredValueNotFoundException("value", "Link");
		if (link.getHref() == null || link.getHref().isEmpty())
			throw new RequiredValueNotFoundException("href", "Link");
	}

	/**
	 * Store a Link in the Database
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static Long storeToDatabase(Link link, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		isValidForStore(link);
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS)) {
			((LinkDAO) link).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long linkId = result.getLong(1);// The id of the link inserted
			link.setId(linkId);
			return linkId;
		}
	}

	/**
	 * Store the nameserver links
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeNameserverLinksToDatabase(List<Link> links, Long nameserverId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeLinkRelationToDatabase(links, nameserverId, connection, NS_STORE_QUERY);
	}

	/**
	 * Stores the Domain links
	 * 
	 * @param links
	 * @param domainId
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeDomainLinksToDatabase(List<Link> links, Long domainId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeLinkRelationToDatabase(links, domainId, connection, DOMAIN_STORE_QUERY);
	}

	/**
	 * Stores the DsData links
	 * 
	 * @param links
	 * @param dsDataId
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeDsDataLinksToDatabase(List<Link> links, Long dsDataId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeLinkRelationToDatabase(links, dsDataId, connection, DS_DATA_STORE_QUERY);
	}

	/**
	 * Store the event links
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeEventLinksToDatabase(List<Link> links, Long eventId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeLinkRelationToDatabase(links, eventId, connection, EVENT_STORE_QUERY);
	}

	/**
	 * Store the remark links
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeRemarkLinksToDatabase(List<Link> links, Long remarkId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeLinkRelationToDatabase(links, remarkId, connection, REMARK_STORE_QUERY);
	}

	/**
	 * Store the entity links
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeEntityLinksToDatabase(List<Link> links, Long entityId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeLinkRelationToDatabase(links, entityId, connection, ENTITY_STORE_QUERY);
	}

	/**
	 * @param links
	 *            The links to be stored in the relation.
	 * @param id
	 *            Id of the owner of the links.
	 * @param connection
	 *            Connection to a database.
	 * @param storeQueryId
	 *            SQL query to use to store the relation of the links.
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	private static void storeLinkRelationToDatabase(List<Link> links, Long id, Connection connection,
			String storeQueryId) throws SQLException, IOException, RequiredValueNotFoundException {
		if (links.isEmpty())
			return;

		String query = queryGroup.getQuery(storeQueryId);
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			for (Link link : links) {
				Long linkId = LinkModel.storeToDatabase(link, connection);
				statement.setLong(1, id);
				statement.setLong(2, linkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();
			}
		}
	}

	/**
	 * Get all links for a Nameserver
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Link> getByNameServerId(Long nameserverId, Connection connection)
			throws IOException, SQLException {
		return getByRelationId(nameserverId, connection, NS_GET_QUERY);
	}

	/**
	 * Gets all links from a domain
	 * 
	 * @param domainId
	 * @param connection
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Link> getByDomainId(Long domainId, Connection connection) throws IOException, SQLException {
		return getByRelationId(domainId, connection, DOMAIN_GET_QUERY);
	}

	/**
	 * Get all links for a event
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Link> getByEventId(Long eventId, Connection connection) throws IOException, SQLException {
		return getByRelationId(eventId, connection, EVENT_GET_QUERY);
	}

	/**
	 * Get all links for a Remark
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Link> getByRemarkId(Long remarkId, Connection connection) throws IOException, SQLException {
		return getByRelationId(remarkId, connection, REMARK_GET_QUERY);
	}

	/**
	 * Get all links for a DsData
	 * 
	 * @param dsDataId
	 * @param connection
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Link> getByDsDataId(Long dsDataId, Connection connection) throws IOException, SQLException {
		return getByRelationId(dsDataId, connection, DS_DATA_GET_QUERY);
	}

	/**
	 * Get all links for an entity
	 * 
	 */
	public static List<Link> getByEntityId(Long entityId, Connection connection) throws IOException, SQLException {
		return getByRelationId(entityId, connection, ENTITY_GET_QUERY);
	}

	/**
	 * @param id
	 *            Id of the owner of the links
	 * @param connection
	 *            connection to a database.
	 * @param queryGetId
	 *            SQL query to get the links of the id.
	 * @return
	 * @throws SQLException
	 */
	private static List<Link> getByRelationId(Long id, Connection connection, String queryGetId) throws SQLException {
		String query = queryGroup.getQuery(queryGetId);
		List<Link> result = null;

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, id);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return Collections.emptyList(); // A Data can have no links
				}
				List<Link> links = new ArrayList<Link>();
				do {
					LinkDAO link = new LinkDAO(resultSet);
					links.add(link);
				} while (resultSet.next());
				result = links;
			}
		}

		return result;
	}

	/**
	 * Unused. Get all the ipAddress from DB
	 * 
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Link> getAll(Connection connection) throws SQLException {
		String query = queryGroup.getQuery("getAll");
		List<Link> result = null;

		try (PreparedStatement statement = connection.prepareStatement(query)) {
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return Collections.emptyList(); // A Data can have no links
				}
				List<Link> links = new ArrayList<Link>();
				do {
					LinkDAO link = new LinkDAO(resultSet);
					links.add(link);
				} while (resultSet.next());
				result = links;
			}
		}

		return result;
	}
}
