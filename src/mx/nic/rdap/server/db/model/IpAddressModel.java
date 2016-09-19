package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.IpAddressDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the IpAddress Object
 * 
 * @author dalpuche
 *
 */
public class IpAddressModel {
	private final static String QUERY_GROUP = "IpAddress";

	protected static QueryGroup queryGroup = null;

	/**
	 * Get a NameserverIpAddressesStruct from a nameserverid
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static NameserverIpAddressesStruct getIpAddressStructByNameserverId(Long nameserverId)
			throws IOException, SQLException {
		IpAddressModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByNameserverId"))) {
			statement.setLong(1, nameserverId);
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO:
																			// Managae
																			// the
																			// exception
				}
				// Process the resulset to construct the struct
				NameserverIpAddressesStruct struct = new NameserverIpAddressesStruct();
				do {
					IpAddressDAO ipAddressDAO = new IpAddressDAO(resultSet);
					if (ipAddressDAO.getType() == 4) {
						struct.getIpv4Adresses().add(ipAddressDAO);
					} else {
						struct.getIpv6Adresses().add(ipAddressDAO);
					}
				} while (resultSet.next());
				return struct;
			}
		}
	}
}
