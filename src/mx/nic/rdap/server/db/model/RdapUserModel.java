package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.db.QueryGroup;

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

}
