/**
 * 
 */
package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.Zone;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.ZoneDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the Zone object
 * @author evaldes
 *
 */
public class ZoneModel {
	
	private final static String QUERY_GROUP = "Zone";
	
	protected static QueryGroup queryGroup = null;
	
	public static boolean storeToDatabase(ZoneDAO zone) throws IOException, SQLException{
		ZoneModel.queryGroup = new QueryGroup(QUERY_GROUP);
		Connection connection = DatabaseSession.getConnection();
		try(PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"))) {
			zone.storeToDatabase(statement);
			return (statement.executeUpdate() == 1);
			//TODO Validate if the insert was correct
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
	public static Zone findByZoneId(int id) throws IOException, SQLException {
		ZoneModel.queryGroup = new QueryGroup(QUERY_GROUP);
		Connection connection = DatabaseSession.getConnection();
		try(PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDomainId"))) {
			statement.setLong(1, id);
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}
			Zone zone = new ZoneDAO(resultSet);
			return zone;
		}
		
	}
}
