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

	private static final String REGISTRAR_GET_QUERY = "getByRegistrar";
	private static final String ENTITY_GET_QUERY = "getByEntity";
	private static final String DOMAIN_GET_QUERY = "getByDomain";
	private static final String REGISTRAR_STORE_QUERY = "storeRegistrarPublicIdsToDatabase";
	private static final String ENTITY_STORE_QUERY = "storeEntityPublicIdsToDatabase";
	private static final String DOMAIN_STORE_QUERY = "storeDomainPublicIdsToDatabase";

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
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
	public static Long storeToDatabase(PublicId publicId, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"));) {
			((PublicIdDAO) publicId).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();// TODO Validate if the insert was correct
			return publicId.getId();
		}
	}

	/**
	 * Stores domain-public id relation
	 * 
	 * @param publicIds
	 * @param domainId
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void storeBy(List<PublicId> publicIds, Long domainId, Connection connection, String query)
			throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery(query))) {
			for (PublicId publicId : publicIds) {
				Long id = PublicIdModel.storeToDatabase(publicId, connection);
				statement.setLong(1, domainId);
				statement.setLong(2, id);
				logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
				statement.executeUpdate(); // TODO Validate if insert was
											// correct
			}
		}
	}

	public static void storePublicIdByDomain(List<PublicId> publicIds, Long domainId, Connection connection)
			throws SQLException, IOException {
		storeBy(publicIds, domainId, connection, DOMAIN_STORE_QUERY);
	}

	public static void storePublicIdByEntity(List<PublicId> publicIds, Long domainId, Connection connection)
			throws SQLException, IOException {
		storeBy(publicIds, domainId, connection, ENTITY_STORE_QUERY);
	}

	public static void storePublicIdByRegistrar(List<PublicId> publicIds, Long domainId, Connection connection)
			throws SQLException, IOException {
		storeBy(publicIds, domainId, connection, REGISTRAR_STORE_QUERY);
	}

	/**
	 * Get all publicIds from an specific type of Object
	 * 
	 * @param domainId
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private static List<PublicId> getBy(Long entityId, Connection connection, String query)
			throws SQLException, IOException {
		PublicIdModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery(query))) {
			statement.setLong(1, entityId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) { // TODO
																	// Validate
																	// if insert
																	// was
																	// correct
				return processResultSet(resultSet);
			}
		}
	}

	public static List<PublicId> getAll(Connection connection) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement("getAll")) {
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet resultSet = statement.executeQuery(); // TODO Validate if
															// insert was
															// correct
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
		return getBy(domainId, connection, DOMAIN_GET_QUERY);
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
		return getBy(entityId, connection, ENTITY_GET_QUERY);
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
		return getBy(registrarId, connection, REGISTRAR_GET_QUERY);
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
