package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.RdapUserDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Model for RdapUserData
 * 
 * @author dalpuche
 *
 */
public class RdapUserModel {

	private final static Logger logger = Logger.getLogger(RdapUserModel.class.getName());

	private final static String QUERY_GROUP = "RdapUser";
	private static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Find the max search results for the autheticatedUser
	 * 
	 * @param username
	 * @param connection
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Integer getMaxSearchResultsForAuthenticatedUser(String username, Connection connection)
			throws IOException, SQLException {
		String query = queryGroup.getQuery("getMaxSearchResults");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, username);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return null;
				}
				return resultSet.getInt(1);
			}
		}
	}

	/**
	 * Validate the required attributes for the rdapuser
	 * 
	 * @param nameserver
	 * @throws RequiredValueNotFoundException
	 */
	private static void isValidForStore(RdapUserDAO user) throws RequiredValueNotFoundException {
		if (user.getName() == null || user.getName().isEmpty())
			throw new RequiredValueNotFoundException("name", "RdapUser");
		if (user.getPass() == null || user.getPass().isEmpty())
			throw new RequiredValueNotFoundException("password", "RdapUser");
		if (user.getUserRole().getRoleName() == null || user.getUserRole().getRoleName().isEmpty())
			throw new RequiredValueNotFoundException("role", "RdapUser");
	}

	/**
	 * Store a rdapuser in the database
	 * 
	 * @param user
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */

	public static void storeToDatabase(RdapUserDAO user, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		isValidForStore(user);
		String query = queryGroup.getQuery("storeToDatabase");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			user.storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();
		}
		RdapUserRoleModel.storeRdapUserRoleToDatabase(user.getUserRole(), connection);
	}

	/**
	 * Get a rdapuser object by it's name
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static RdapUserDAO getByName(String name, Connection connection) throws IOException, SQLException {
		String query = queryGroup.getQuery("getByName");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, name);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");
				}
				RdapUserDAO user = new RdapUserDAO(resultSet);
				user.setUserRole(RdapUserRoleModel.getByUserName(user.getName(), connection));
				return user;
			}
		}
	}

	/**
	 * Clean the rdapuser and rdapuserrole tables in the migration
	 * 
	 * @param connection
	 * @throws SQLException
	 */
	public static void cleanRdapUserDatabase(Connection connection) throws SQLException {
		String query = queryGroup.getQuery("deleteAllRdapUserRoles");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();

		}
		query = queryGroup.getQuery("deleteAllRdapUsers");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();

		}
	}
}
