package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.core.db.IpAddress;
import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
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

	private final static Logger logger = Logger.getLogger(IpAddressModel.class.getName());

	private final static String QUERY_GROUP = "IpAddress";

	protected static QueryGroup queryGroup = null;

	/**
	 * Store an array of IpAddress in the database
	 * 
	 * @param remark
	 * @return true if the insert was correct
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean storeToDatabase(NameserverIpAddressesStruct struct, long nameserverId, Connection connection)
			throws IOException, SQLException {
		IpAddressModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"))) {
			for (IpAddress addressV4 : struct.getIpv4Adresses()) {
				addressV4.setNameserverId(nameserverId);
				((IpAddressDAO) addressV4).storeToDatabase(statement);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
											// insert was correct
			}
			for (IpAddress addressV6 : struct.getIpv6Adresses()) {
				addressV6.setNameserverId(nameserverId);
				((IpAddressDAO) addressV6).storeToDatabase(statement);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
											// insert was correct
			}
			return true;
		} catch (Exception e) {
			return false;// TODO: manage the exception
		}
	}

	/**
	 * Get a NameserverIpAddressesStruct from a nameserverid
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static NameserverIpAddressesStruct getIpAddressStructByNameserverId(Long nameserverId, Connection connection)
			throws IOException, SQLException {
		IpAddressModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByNameserverId"))) {
			statement.setLong(1, nameserverId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
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
					IpAddressDAO ipAddressDAO = new IpAddressDAO(resultSet,connection);
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
