package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import mx.nic.rdap.core.db.SecureDNS;

/**
 * Data access class for the SecureDNS object. It represents secure DNS
 * information about domain names.
 * 
 * @author evaldes
 *
 */
public class SecureDNSDAO extends SecureDNS implements DatabaseObject {

	/**
	 * Default constructor
	 */
	public SecureDNSDAO() {
		super();
	}

	/**
	 * Construct the object SecurDNS from a resultset
	 * 
	 * @throws SQLException
	 */
	public SecureDNSDAO(ResultSet resultSet) throws SQLException {
		super();
		loadFromDatabase(resultSet);
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
		int maxSigLife = resultSet.getInt("sdns_max_sig_life");
		if (!resultSet.wasNull()) {
			this.setMaxSigLife(maxSigLife);
		}
		this.setDomainId(resultSet.getLong("dom_id"));
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
		if (this.getMaxSigLife() == null) {
			preparedStatement.setNull(3, Types.INTEGER);
		} else {
			preparedStatement.setInt(3, this.getMaxSigLife());
		}
		preparedStatement.setLong(4, this.getDomainId());

	}

}
