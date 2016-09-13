package mx.nic.rdap.server.db;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.IpAddress;

/**
 * DAO for the IpAddress Object.Object representig an IpAddress, different to
 * {@link IpNetwork}
 * 
 * @author dalpuche
 *
 */
public class IpAddressDAO extends IpAddress implements DatabaseObject {

	/**
	 * Constructor
	 */
	public IpAddressDAO() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Constructor that create a IpAddressDao from a resulset
	 */
	public IpAddressDAO(ResultSet resultSet) {
		super();
		try {
			loadFromDatabase(resultSet);
		} catch (SQLException e) {
			// TODO Manage the exception
		}
	}

	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		// validate if resulset is null
		if (resultSet.wasNull()) {
			this.setId(0L);
			this.setType(0);
			this.setAddress(null);
			return;
		}

		this.setId(resultSet.getLong("iad_id"));
		this.setType(resultSet.getInt("iad_type"));
		try {
			this.setAddress(InetAddress.getByName(resultSet.getString("iad_value")));
		} catch (UnknownHostException e) {
			// TODO manage the exception
		}

	}

	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		// Unimplement
	}

}
