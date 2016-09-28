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

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.RemarkDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Model for the Remark Object
 * 
 * @author dalpuche
 * @author dhfelix
 *
 */
public class RemarkModel {

	private final static Logger logger = Logger.getLogger(RemarkModel.class.getName());

	private final static String QUERY_GROUP = "Remark";

	private final static String NAMESERVER_STORE_QUERY = "storeNameserverRemarksToDatabase";
	private final static String DOMAIN_STORE_QUERY = "storeDomainRemarksToDatabase";
	private final static String ENTITY_STORE_QUERY = "storeEntityRemarksToDatabase";
	private final static String REGISTRAR_STORE_QUERY = "storeRegistrarRemarksToDatabase";

	private static final String NAMESERVER_GET_QUERY = "getByNameserverId";
	private static final String DOMAIN_GET_QUERY = "getByDomainId";
	private static final String ENTITY_GET_QUERY = "getByEntityId";
	private static final String REGISTRAR_GET_QUERY = "getByRegistrarId";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			RemarkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Store a Remark in the database
	 * 
	 * @param remark
	 * @return true if the insert was correct
	 * @throws IOException
	 * @throws SQLException
	 * @throws RequiredValueNotFoundException
	 */
	public static long storeToDatabase(Remark remark, Connection connection)
			throws IOException, SQLException, RequiredValueNotFoundException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS)) {// The Remark's id is
													// autoincremental,
													// Statement.RETURN_GENERATED_KEYS
													// give us the id
													// generated for the
													// object stored
			((RemarkDAO) remark).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long remarkInsertedId = result.getLong(1);// The id of the remark
														// inserted

			remark.setId(remarkInsertedId);
			RemarkDescriptionModel.storeAllToDatabase(remark.getDescriptions(), remarkInsertedId, connection);
			LinkModel.storeRemarkLinksToDatabase(remark.getLinks(), remarkInsertedId, connection);
			return remarkInsertedId;
		}
	}

	private static void storeRelationRemarksToDatabase(List<Remark> remarks, Long id, Connection connection,
			String queryId) throws SQLException, IOException, RequiredValueNotFoundException {
		String query = queryGroup.getQuery(queryId);
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			for (Remark remark : remarks) {
				Long remarkId = RemarkModel.storeToDatabase(remark, connection);
				statement.setLong(1, id);
				statement.setLong(2, remarkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
				// insert was correct
			}
		}
	}

	/**
	 * Store the nameserver remarks
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeNameserverRemarksToDatabase(List<Remark> remarks, Long nameserverId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeRelationRemarksToDatabase(remarks, nameserverId, connection, NAMESERVER_STORE_QUERY);
	}

	/**
	 * Stores the domain's remarks
	 * 
	 * @param remarks
	 * @param domainId
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeDomainRemarksToDatabase(List<Remark> remarks, Long domainId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeRelationRemarksToDatabase(remarks, domainId, connection, DOMAIN_STORE_QUERY);
	}

	/**
	 * 
	 * Stores the Entity's remarks
	 * 
	 */
	public static void storeEntityRemarksToDatabase(List<Remark> remarks, Long entityId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeRelationRemarksToDatabase(remarks, entityId, connection, ENTITY_STORE_QUERY);
	}

	/**
	 * 
	 * Stores the Registrar's remarks
	 * 
	 */
	public static void storeRegistrarRemarksToDatabase(List<Remark> remarks, Long registrarId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		storeRelationRemarksToDatabase(remarks, registrarId, connection, REGISTRAR_STORE_QUERY);
	}

	private static List<Remark> getByRelationId(Long id, Connection connection, String queryId)
			throws IOException, SQLException {
		String query = queryGroup.getQuery(queryId);
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, id);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery();) {
				return processResultSet(resultSet, connection);
			}
		}
	}

	/**
	 * Get all remarks for the namemeserver
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Remark> getByNameserverId(Long nameserverId, Connection connection)
			throws IOException, SQLException {
		return getByRelationId(nameserverId, connection, NAMESERVER_GET_QUERY);
	}

	/**
	 * Get all domain's remarks
	 * 
	 */
	public static List<Remark> getByDomainId(Long domainId, Connection connection) throws SQLException, IOException {
		return getByRelationId(domainId, connection, DOMAIN_GET_QUERY);
	}

	/**
	 * Get all entity's remarks
	 * 
	 */
	public static List<Remark> getByEntityId(Long entityId, Connection connection) throws SQLException, IOException {
		return getByRelationId(entityId, connection, ENTITY_GET_QUERY);
	}

	/**
	 * Get all Registrar's remarks
	 * 
	 */
	public static List<Remark> getByRegistrarId(Long registrarId, Connection connection)
			throws SQLException, IOException {
		return getByRelationId(registrarId, connection, REGISTRAR_GET_QUERY);
	}

	/**
	 * Unused. Get all Remarks from DB
	 * 
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Remark> getAll(Connection connection) throws IOException, SQLException {
		RemarkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getAll"));
				ResultSet resultSet = statement.executeQuery();) {
			return processResultSet(resultSet, connection);
		}
	}

	/**
	 * Process the resulset of the query
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 * @throws ObjectNotFoundException
	 * @throws IOException
	 */
	private static List<Remark> processResultSet(ResultSet resultSet, Connection connection)
			throws SQLException, ObjectNotFoundException, IOException {
		if (!resultSet.next()) {
			return Collections.emptyList();
		}
		List<Remark> remarks = new ArrayList<Remark>();
		do {
			RemarkDAO remark = new RemarkDAO(resultSet);
			remark.setDescriptions(RemarkDescriptionModel.findByRemarkId(remark.getId(), connection));// load
																										// the
																										// remark
																										// descriptions
																										// of
																										// the
																										// remark
			remark.setLinks(LinkModel.getByRemarkId(remark.getId(), connection));// load
																					// the
																					// remark's
																					// links
			remarks.add(remark);
		} while (resultSet.next());
		return remarks;
	}

}
