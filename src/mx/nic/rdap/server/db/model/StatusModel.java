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

import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nix.rdap.core.catalog.Status;

/**
 * @author dalpuche
 *
 */
public class StatusModel {

	private final static Logger logger = Logger.getLogger(StatusModel.class.getName());

	private final static String QUERY_GROUP = "Status";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			StatusModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Store a array of statement in the relational table nameserver_status
	 * 
	 * @param status
	 * @param nameserverId
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void storeNameserverStatusToDatabase(List<Status> statusList, Long nameserverId,
			Connection connection) throws IOException, SQLException {
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeNameserverStatusToDatabase"))) {
			for (Status status : statusList) {
				statement.setLong(1, nameserverId);
				statement.setLong(2, status.getId());
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();
			}
		}
	}
	
	/**
	 * Stores an array of statements in the relational table domain_status
	 * 
	 * @param statusList
	 * @param domainId
	 * @param connection
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void storeDomainStatusToDatabase(List<Status> statusList, Long domainId, Connection connection)
			throws IOException, SQLException {
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeDomainStatusToDatabase"))) {
			for (Status status : statusList) {
				statement.setLong(1, domainId);
				statement.setLong(2, status.getId());
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();
			}
		}
	}

	/**
	 * Get all Status for a Nameserver
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Status> getByNameServerId(Long nameserverId, Connection connection)
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
				List<Status> status = new ArrayList<Status>();
				do {
					status.add(Status.getById(resultSet.getInt("sta_id")));
				} while (resultSet.next());
				return status;
			}
		}
	}
	
	/**
	 * Get all status from a domain
	 * 
	 * @param domainId
	 * @param connection
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Status> getByDomainId(Long domainId, Connection connection) throws IOException, SQLException {
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
				List<Status> status = new ArrayList<Status>();
				do {
					status.add(Status.getById(resultSet.getInt("sta_id")));
				} while (resultSet.next());
				return status;
			}
		}
	}
	
}
