/**
 * 
 */
package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.core.db.Zone;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.ZoneDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the Zone object
 * 
 * @author evaldes
 *
 */
public class ZoneModel {

	private final static Logger logger = Logger.getLogger(ZoneModel.class.getName());

	private final static String QUERY_GROUP = "Zone";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Stores a zone into the database
	 * 
	 * @param zone
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Integer storeToDatabase(Zone zone, Connection connection) throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS)) {
			((ZoneDAO) zone).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			Integer zoneId = resultSet.getInt(1);// Inserted Zone's Id
			return zoneId;
		}
	}

	/**
	 * Get zone from a domain
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Zone getByZoneId(Integer id, Connection connection) throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByZoneId"))) {
			statement.setInt(1, id);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}
			Zone zone = new ZoneDAO(resultSet);
			return zone;
		}
	}

	public static List<Zone> getAll(Connection connection) throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getAll"))) {
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}
			List<Zone> zones = new ArrayList<Zone>();
			do {
				ZoneDAO zone = new ZoneDAO(resultSet);
				zones.add(zone);
			} while (resultSet.next());
			return zones;
		}

	}
}
