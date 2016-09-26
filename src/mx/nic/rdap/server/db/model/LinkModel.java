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

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.server.db.LinkDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * The model for the Link object
 * 
 * @author dalpuche
 *
 */
public class LinkModel {

	private final static Logger logger = Logger.getLogger(LinkModel.class.getName());

	private final static String QUERY_GROUP = "Link";

	protected static QueryGroup queryGroup = null;

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
			statement.executeUpdate();// TODO Validate if the
										// insert was correct
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long linkId = result.getLong(1);// The id of the link inserted
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
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeNameserverLinksToDatabase"))) {
			for (Link link : links) {
				Long linkId = LinkModel.storeToDatabase(link, connection);
				statement.setLong(1, nameserverId);
				statement.setLong(2, linkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
				// insert was correct
			}
		}
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
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeDomainLinksToDatabase"))) {
			for (Link link : links) {
				Long linkId = LinkModel.storeToDatabase(link, connection);
				statement.setLong(1, domainId);
				statement.setLong(2, linkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the insert was
											// correct
			}
		}
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
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeDsDataLinksToDatabase"))) {
			for (Link link : links) {
				Long linkId = LinkModel.storeToDatabase(link, connection);
				statement.setLong(1, dsDataId);
				statement.setLong(2, linkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the insert was
											// correct
			}
		}
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
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeEventLinksToDatabase"))) {
			for (Link link : links) {
				Long linkId = LinkModel.storeToDatabase(link, connection);
				statement.setLong(1, eventId);
				statement.setLong(2, linkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
				// insert was correct
			}
		}
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
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeRemarkLinksToDatabase"))) {
			for (Link link : links) {
				Long linkId = LinkModel.storeToDatabase(link, connection);
				statement.setLong(1, remarkId);
				statement.setLong(2, linkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
				// insert was correct
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
				List<Link> links = new ArrayList<Link>();
				do {
					LinkDAO link = new LinkDAO(resultSet);
					links.add(link);
				} while (resultSet.next());
				return links;
			}
		}
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
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDomainId"))) {
			statement.setLong(1, domainId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO:
																			// Managae
																			// the
																			// exception
				}
				List<Link> links = new ArrayList<Link>();
				do {
					LinkDAO link = new LinkDAO(resultSet);
					links.add(link);
				} while (resultSet.next());
				return links;
			}
		}
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
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByEventId"))) {
			statement.setLong(1, eventId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null; // An event can have no links
				}
				List<Link> links = new ArrayList<Link>();
				do {
					LinkDAO link = new LinkDAO(resultSet);
					links.add(link);
				} while (resultSet.next());
				return links;
			}
		}
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
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByRemarkId"))) {
			statement.setLong(1, remarkId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null; // An event can have no links
				}
				List<Link> links = new ArrayList<Link>();
				do {
					LinkDAO link = new LinkDAO(resultSet);
					links.add(link);
				} while (resultSet.next());
				return links;
			}
		}
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
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDsDataId"))) {
			statement.setLong(1, dsDataId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null; // A dsData can have no links
				}
				List<Link> links = new ArrayList<Link>();
				do {
					LinkDAO link = new LinkDAO(resultSet);
					links.add(link);
				} while (resultSet.next());
				return links;
			}
		}
	}
}
