package mx.nic.rdap.server.migration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.nic.rdap.core.db.Event;
import mx.nix.rdap.core.catalog.Status;

/**
 * @author L00000185
 *
 */
public class MigrationUtil {

	public static List<Status> getRDAPStatusFromResultSet(String resultSet) {
		List<Status> statusList = new ArrayList<Status>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<String> statusString = Arrays.asList(resultSet.split(","));
			for (String status : statusString) {
				statusList.add(Status.getByName(status.trim()));
			}
		}
		return statusList;
	}

	public static List<Status> getRDAPStatusFromEPPStatusResultSet(String resultSet) {
		List<Status> statusList = new ArrayList<Status>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<String> statusString = Arrays.asList(resultSet.split(","));
			for (String status : statusString) {
				statusList.add(Status.getByEPPName(status.trim()));
			}
		}
		return statusList;
	}

	public static boolean isResultSetValueValid(String value) {
		if (value == null | value.isEmpty() | value.trim().isEmpty())
			return false;
		return true;
	}

	public static List<Event> getEventsFromResultSet(String resultSet) {
		return null;
	}
}
