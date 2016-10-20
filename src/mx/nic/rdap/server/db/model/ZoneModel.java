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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the Zone table, read all zones in the zone_table and keeps it in
 * memory for quickly access.
 * 
 * @author evaldes
 * @author dhfelix
 *
 */
public class ZoneModel {

	private final static Logger logger = Logger.getLogger(ZoneModel.class.getName());

	private final static String QUERY_GROUP = "Zone";

	private static QueryGroup queryGroup = null;

	private static Map<Integer, String> zoneById;
	private static Map<String, Integer> idByZone;
	public static String REVERSE_IP_V4 = "in-addr.arpa";
	public static String REVERSE_IP_V6 = "ip6.arpa";

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group", e);
		}

		try (Connection con = DatabaseSession.getConnection();) {
			loadAllFromDatabase(con);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Load all the zones stored in the database.
	 * 
	 * @param con
	 *            Connection use to query a database.
	 * @throws SQLException
	 */
	private static void loadAllFromDatabase(Connection con) throws SQLException {
		zoneById = new HashMap<Integer, String>();
		idByZone = new HashMap<String, Integer>();

		String query = queryGroup.getQuery("getAll");

		PreparedStatement statement = con.prepareStatement(query);
		ResultSet rs = statement.executeQuery();
		if (!rs.next()) {
			return;
		}

		do {
			Integer zoneId = rs.getInt("zone_id");
			String zoneName = rs.getString("zone_name");
			zoneById.put(zoneId, zoneName);
			idByZone.put(zoneName, zoneId);
		} while (rs.next());

	}

	/**
	 * Validate if the configurated zones are in the database
	 * 
	 * @throws ObjectNotFoundException
	 */
	public static void validateConfiguratedZones() throws ObjectNotFoundException {
		List<String> configuratedZones = RdapConfiguration.getServerZones();
		Map<Integer, String> zoneByIdForServer = new HashMap<Integer, String>();
		Map<String, Integer> idByZoneForServer = new HashMap<String, Integer>();
		for (String zone : configuratedZones) {
			if (idByZone.get(zone) == null) {
				logger.log(Level.SEVERE, "Configurated zone not found in database:" + zone);
				throw new ObjectNotFoundException("Configurated zone not found in database:" + zone);
			}
			zoneByIdForServer.put(idByZone.get(zone), zone);
			idByZoneForServer.put(zone, getIdByZoneName(zone));
		}
		// Ovewrite the hashmaps to only use the configurated zones
		zoneById = zoneByIdForServer;
		idByZone = idByZoneForServer;
	}

	/**
	 * Stores a zone into the database
	 * 
	 * @param zoneName
	 *            Zone name to be stored.
	 * @return The zoneId for the <code>zoneName</code>.
	 * @throws SQLException
	 */
	public static Integer storeToDatabase(String zoneName, Connection connection) throws SQLException {
		Integer idByZoneName = getIdByZoneName(zoneName);

		if (idByZoneName != null) {
			return idByZoneName;
		}

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, zoneName);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			Integer zoneId = resultSet.getInt(1);// Inserted Zone's Id
			zoneById.put(zoneId, zoneName);
			idByZone.put(zoneName, zoneId);

			return zoneId;
		}
	}

	/**
	 * Get zoneName from an id
	 * 
	 * @param id
	 *            identifier related to a zone.
	 * @return The ZoneName if the id is related to a zone, otherwise null.
	 */
	public static String getZoneNameById(Integer id) {
		return zoneById.get(id);
	}

	/**
	 * @param zoneName
	 *            Name of the zone
	 * @return The Id if the zone exists in the database, otherwise null.
	 */
	public static Integer getIdByZoneName(String zoneName) {
		return idByZone.get(zoneName);
	}

	/**
	 * @param zoneName
	 * @return Checks if the zoneName exists in the database.
	 */
	public static boolean existsZone(String zoneName) {
		return idByZone.containsKey(zoneName);
	}

	/**
	 * @param zoneId
	 * @return Checks if the id is related to a zone.
	 */
	public static boolean existsZoneById(Integer zoneId) {
		return zoneById.containsKey(zoneId);
	}

	/**
	 * validate if a address is in reverse lookup
	 * 
	 * @param address
	 * @return
	 */
	public static boolean isReverseAddress(String address) {
		return address.trim().endsWith(REVERSE_IP_V4) || address.trim().endsWith(REVERSE_IP_V6);

	}
}
