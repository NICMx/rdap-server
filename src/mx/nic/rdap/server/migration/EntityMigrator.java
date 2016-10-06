package mx.nic.rdap.server.migration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import mx.nic.rdap.server.db.EntityDAO;

/**
 * @author L00000185
 *
 */
public class EntityMigrator {

	/**
	 * @param entitiesResultSet
	 * @return
	 */
	public static List<EntityDAO> getEntitiesFromResultSet(ResultSet entitiesResultSet) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param entities
	 * @param rdapConnection
	 */
	public static void storeEntitiesInRDAPDatabase(List<EntityDAO> entities, Connection rdapConnection) {
		// TODO Auto-generated method stub

	}

}
