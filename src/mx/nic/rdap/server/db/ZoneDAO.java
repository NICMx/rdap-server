/**
 * 
 */
package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access for the zone object
 * @author evaldes
 *
 */
public class ZoneDAO extends mx.nic.rdap.core.db.Zone implements DatabaseObject {
	
	/**
	 * Default Constructor
	 */
	public ZoneDAO(){
		super();
	}
	
	public ZoneDAO(ResultSet resultSet) {
		try {
			loadFromDatabase(resultSet);
		}
		catch (SQLException e){
			//TODO 
		}
	}
	
	/* (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		// TODO Auto-generated method stub
		if(resultSet.wasNull())
			return;
		this.setId(resultSet.getInt("zone_id"));
		this.setZoneName(resultSet.getString("zone_name"));
	}

	/* (non-Javadoc)
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		// TODO Auto-generated method stub
		preparedStatement.setInt(1, this.getId());
		preparedStatement.setString(2, this.getZoneName());
	}

}
