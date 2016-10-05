package mx.nic.rdap.server.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mx.nic.rdap.server.db.NameserverDAO;

/**
 * @author L00000185
 *
 */
public class NameserverMigrator {

	public NameserverMigrator() {

	}

	public void processResultSet(ResultSet result) throws SQLException {
		List<NameserverDAO> nameservers = new ArrayList<NameserverDAO>();
		while (result.next()) {
			NameserverDAO nameserver = new NameserverDAO();
			if (MigrationUtil.isResultSetValueValid(result.getString("handle")))
				nameserver.setHandle(result.getString("handle").trim());
			else {
				throw new RuntimeException("Nameserver's handle can't be null");
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("ldh_name")))
				nameserver.setHandle(result.getString("ldh_name").trim());
			else {
				throw new RuntimeException("Nameserver's ldh_name can't be null");
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("port43")))
				nameserver.setHandle(result.getString("port43").trim());
			else {
				throw new RuntimeException("Nameserver's port43 can't be null");
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("rdap_status"))) {
				nameserver.setStatus(MigrationUtil.getRDAPStatusFromResultSet(result.getString("rdap_status")));
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("epp_status"))) {
				nameserver.getStatus().addAll(MigrationUtil.getRDAPStatusFromResultSet(result.getString("epp_status")));
			}
		}
	}

}
