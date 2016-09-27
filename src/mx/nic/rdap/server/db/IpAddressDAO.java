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
		super();
	}

	/**
	 * Constructor that create a IpAddressDao from a resulset
	 * 
	 * @throws SQLException
	 */
	public IpAddressDAO(ResultSet resultSet) throws SQLException {
		super();
		loadFromDatabase(resultSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getLong("iad_id"));
		this.setNameserverId(resultSet.getLong("nse_id"));
		try {
			this.setAddress(InetAddress.getByName(resultSet.getString("iad_value")));
		} catch (UnknownHostException e) {
			// TODO manage the exception
			// throw new InvalidValueException("iad_value",
			// this.getClass().getName());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setLong(1, this.getNameserverId());
		preparedStatement.setInt(2, this.getType());
		preparedStatement.setInt(3, this.getType());// To store the ipv6,use an
													// if clause, the third
													// parameter is the type to
													// compare if is a ipv4 or a
													// opv6
		preparedStatement.setString(4, this.getAddress().getHostAddress());
		preparedStatement.setString(5, this.getAddress().getHostAddress());

	}

}
