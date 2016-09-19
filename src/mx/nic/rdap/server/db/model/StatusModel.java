package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nix.rdap.core.catalog.Status;

/**
 * @author dalpuche
 *
 */
public class StatusModel {

	private final static String QUERY_GROUP = "Status";

	protected static QueryGroup queryGroup = null;

	/**
	 * Get all Status for a Nameserver
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Status> getByNameServerId(Long nameserverId) throws IOException, SQLException {
		StatusModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByNameServerId"))) {
			statement.setLong(1, nameserverId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO:
																			// Managae
																			// the
																			// exception
				}
				List<Status> status = new ArrayList<Status>();
				do {
					status.add(Status.getById(resultSet.getInt("sta_id")));
				} while (resultSet.next());
				return status;
			}
		}
	}
}
