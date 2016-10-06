package mx.nic.rdap.server.migration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mx.nic.rdap.server.db.NameserverDAO;
import mx.nic.rdap.server.db.model.NameserverModel;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Class that process the nameservers from the client's database and store them
 * in the RDAP database
 * 
 * @author dalpuche
 *
 */
public class NameserverMigrator {

	/**
	 * Process the resultSet of the select statement and return a list of
	 * Nameservers
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	public static List<NameserverDAO> getNameserversFromResultSet(ResultSet result) throws SQLException {
		List<NameserverDAO> nameservers = new ArrayList<NameserverDAO>();
		while (result.next()) {
			NameserverDAO nameserver = new NameserverDAO();
			if (MigrationUtil.isResultSetValueValid(result.getString("handle")))
				nameserver.setHandle(result.getString("handle").trim());
			else {
				throw new RuntimeException("Nameserver's handle can't be null");
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("ldh_name")))
				nameserver.setPunycodeName(result.getString("ldh_name").trim());
			else {
				throw new RuntimeException("Nameserver's ldh_name can't be null");
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("port43")))
				nameserver.setPort43(result.getString("port43").trim());
			else {
				throw new RuntimeException("Nameserver's port43 can't be null");
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("rdap_status"))) {
				nameserver.setStatus(MigrationUtil.getRDAPStatusFromResultSet(result.getString("rdap_status")));
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("epp_status"))) {
				nameserver.getStatus().addAll(MigrationUtil.getRDAPStatusFromResultSet(result.getString("epp_status")));
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("events"))) {
				nameserver.setEvents(MigrationUtil.getEventsFromResultSet(result.getString("events")));
			}

			nameservers.add(nameserver);
		}
		return nameservers;
	}

	/**
	 * Store the nameservers in the RDAP database
	 * 
	 * @param nameservers
	 * @param con
	 */
	public static void storeNameserversInRDAPDatabase(List<NameserverDAO> nameservers, Connection con) {
		for (NameserverDAO nameserver : nameservers) {
			try {
				NameserverModel.storeToDatabase(nameserver, con);
			} catch (IOException | SQLException | RequiredValueNotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}

}
