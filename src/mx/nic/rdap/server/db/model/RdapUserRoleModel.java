package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.RdapUserRoleDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * 
 * @author dalpuche
 *
 */
public class RdapUserRoleModel {

	private final static Logger logger = Logger.getLogger(RdapUserRoleModel.class.getName());

	private final static String QUERY_GROUP = "RdapUserRole";
	private static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Validate the required attributes for the rdapUserRole
	 * 
	 * @param nameserver
	 * @throws RequiredValueNotFoundException
	 */
	private static void isValidForStore(RdapUserRoleDAO userRole) throws RequiredValueNotFoundException {
		if (userRole.getUserName() == null || userRole.getUserName().isEmpty())
			throw new RequiredValueNotFoundException("userName", "RdapUserRole");
		if (userRole.getRoleName() == null || userRole.getRoleName().isEmpty())
			throw new RequiredValueNotFoundException("roleName", "RdapUserRole");
	}

	/**
	 * Store a rdapUserRole in the database
	 * 
	 * @param userRole
	 * @param connection
	 * @throws RequiredValueNotFoundException
	 * @throws SQLException
	 */
	public static void storeRdapUserRoleToDatabase(RdapUserRoleDAO userRole, Connection connection)
			throws RequiredValueNotFoundException, SQLException {
		isValidForStore(userRole);
		String query = queryGroup.getQuery("storeToDatabase");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			userRole.storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();
		}
	}

	/**
	 * Get a rdapUserRole by the username
	 * 
	 * @param userName
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static RdapUserRoleDAO getByUserName(String userName, Connection connection) throws SQLException {

		String query = queryGroup.getQuery("getByUserName");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, userName);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");
				}
				RdapUserRoleDAO userRole = new RdapUserRoleDAO(resultSet);
				return userRole;
			}
		}
	}

}
