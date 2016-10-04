/**
 * 
 */
package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.Zone;

/**
 * Data access for the zone object
 * 
 * @author evaldes
 *
 */
public class ZoneDAO extends Zone implements DatabaseObject {

	/**
	 * Default Constructor
	 */
	public ZoneDAO() {
		super();
	}

	public ZoneDAO(ResultSet resultSet) throws SQLException {
		loadFromDatabase(resultSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.server.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getInt("zone_id"));
		this.setZoneName(resultSet.getString("zone_name"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getZoneName());
	}

}
