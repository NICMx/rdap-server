package mx.nic.rdap.server.migration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.db.RdapUserDAO;
import mx.nic.rdap.server.db.RdapUserRoleDAO;
import mx.nic.rdap.server.db.model.RdapUserModel;
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.InvalidadDataStructure;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Class used to process the users from the client´s database and stores them
 * into the RDAP's database
 * 
 * @author dalpuche
 *
 */
public class UserMigrator {

	private static Logger logger = Logger.getLogger(DomainMigrator.class.getName());

	/**
	 * Process the resultSet of the select statement and returns a list of users
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 * @throws InvalidValueException
	 * @throws InvalidadDataStructure
	 * @throws RequiredValueNotFoundException
	 */
	public static List<RdapUserDAO> getUsersFromResultSet(ResultSet resultSet)
			throws SQLException, InvalidValueException, RequiredValueNotFoundException, InvalidadDataStructure {
		List<RdapUserDAO> users = new ArrayList<RdapUserDAO>();
		while (resultSet.next()) {
			RdapUserDAO user = new RdapUserDAO();

			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("rus_name"))) {
					user.setName(resultSet.getString("rus_name").trim());
				} else {
					throw new RequiredValueNotFoundException("Name", "RdapUser");
				}
			} catch (SQLException e) {
				throw new RequiredValueNotFoundException("Name", "RdapUser");
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("pass"))) {
					user.setPass(resultSet.getString("pass").trim());
				} else {
					throw new RequiredValueNotFoundException("Password", "RdapUser");
				}
			} catch (SQLException e) {
				throw new RequiredValueNotFoundException("Password", "RdapUser");
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("rus_max_search_results"))) {
					user.setMaxSearchResults(Integer.parseInt(resultSet.getString("rus_max_search_results")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "rus_max_search_results column not found");// Not
				// a
				// required
				// value.
			} catch (NumberFormatException e) {
				logger.log(Level.WARNING, "rus_max_search_results column invalid value.Set null");
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("rur_name"))) {
					RdapUserRoleDAO role = new RdapUserRoleDAO();
					role.setRoleName(resultSet.getString("rur_name").trim());
					user.setUserRole(role);
				} else {
					throw new RequiredValueNotFoundException("Role", "RdapUser");
				}
			} catch (SQLException e) {
				throw new RequiredValueNotFoundException("Role", "RdapUser");
			}
			users.add(user);
		}
		return users;

	}

	/**
	 * Store the users in the RDAP database
	 * 
	 * @param users
	 * @param con
	 * @throws RequiredValueNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 * @throws InvalidValueException
	 */
	public static void storeDomainsInRDAPDatabase(List<RdapUserDAO> users, Connection con)
			throws IOException, SQLException, RequiredValueNotFoundException, InvalidValueException {
		for (RdapUserDAO user : users) {
			RdapUserModel.storeToDatabase(user, con);
		}
	}
}
