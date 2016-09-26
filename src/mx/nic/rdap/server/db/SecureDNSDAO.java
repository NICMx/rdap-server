package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access class for the SecureDNS object. It represents secure DNS
 * information about domain names.
 * 
 * @author evaldes
 *
 */
public class SecureDNSDAO extends mx.nic.rdap.core.db.SecureDNS implements DatabaseObject {

	/**
	 * Default constructor
	 */
	public SecureDNSDAO() {
		super();
	}

	/**
	 * Construct the object SecurDNS from a resultset
	 */
	public SecureDNSDAO(ResultSet resultSet) {
		super();
		try {
			loadFromDatabase(resultSet);
		} catch (SQLException e) {
			// TODO Manage exception
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.server.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getLong("sdns_id"));
		this.setZoneSigned(resultSet.getBoolean("sdns_zone_signed"));
		this.setDelegationSigned(resultSet.getBoolean("sdns_delegation_signed"));
		int maxSigLife = resultSet.getInt("dsd_keytag");
		if (!resultSet.wasNull()) {
			this.setMaxSigLife(maxSigLife);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setBoolean(1, this.getZoneSigned());
		preparedStatement.setBoolean(2, this.getDelegationSigned());
		preparedStatement.setInt(3, this.getMaxSigLife());
		preparedStatement.setLong(4, this.getDomainId());

	}

}
