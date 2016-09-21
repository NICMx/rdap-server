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

import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.server.db.PublicIdDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the PublicId Object
 * 
 * @author evaldes
 *
 */
public class PublicIdModel {

	private final static Logger logger = Logger.getLogger(PublicIdModel.class.getName());

	private final static String QUERY_GROUP = "PublicId";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			SecureDNSModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Stores all PublicId Objects to the database
	 * 
	 * @param publicIds
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void storeAllToDatabase(List<PublicId> publicIds, Connection connection)
			throws SQLException, IOException {
		for (PublicId publicId : publicIds) {
			PublicIdModel.storeToDatabase(publicId, connection);
		}
	}

	/**
	 * Stores a publicId to the database
	 * 
	 * @param publicId
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void storeToDatabase(PublicId publicId, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"));) {
			((PublicIdDAO) publicId).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();// TODO Validate if the insert was correct
		}
	}

	/**
	 * Get all publicIds from an specific type of Object
	 * 
	 * @param domainId
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<PublicId> getBy(Long entityId, String name, Connection connection)
			throws SQLException, IOException {
		PublicIdModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getBy" + name))) {
			statement.setLong(1, entityId);
			try (ResultSet resultSet = statement.executeQuery()) {
				return processResultSet(resultSet);
			}
		}
	}

	public static List<PublicId> getAll(Connection connection) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("getAll")) {
			ResultSet resultSet = statement.executeQuery();
			return processResultSet(resultSet);
		}
	}

	/**
	 * Get all domain's public identifiers
	 * 
	 * @param domainId
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<PublicId> getByDomain(Long domainId, Connection connection) throws SQLException, IOException {
		return getBy(domainId, "Domain", connection);
	}

	/**
	 * Get all entitiy's public identifiers
	 * 
	 * @param domainId
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<PublicId> getByEntity(Long entityId, Connection connection) throws SQLException, IOException {
		return getBy(entityId, "Entity", connection);
	}

	/**
	 * Get all registrar's public identifiers
	 * 
	 * @param domainId
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<PublicId> getByRegistrar(Long registrarId, Connection connection)
			throws SQLException, IOException {
		return getBy(registrarId, "Registrar", connection);
	}

	/**
	 * Process the ResultSet of the query
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private static List<PublicId> processResultSet(ResultSet resultSet) throws SQLException {
		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Object not found");
		}
		List<PublicId> publicIds = new ArrayList<PublicId>();
		do {
			PublicIdDAO publicId = new PublicIdDAO(resultSet);
			publicIds.add(publicId);
		} while (resultSet.next());
		return publicIds;
	}
}
