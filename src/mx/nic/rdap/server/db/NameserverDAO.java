package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.Nameserver;

/**
 * DAO for the Nameserver object.The nameserver object class represents
 * information regarding DNS nameservers used in both forward and reverse DNS
 * 
 * @author dalpuche
 *
 */
public class NameserverDAO extends Nameserver implements DatabaseObject {

	/**
	 * Constructor default
	 */
	public NameserverDAO() {
		super();
	}

	/**
	 * Contruct a NameserverDAO from a resulset
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	public NameserverDAO(ResultSet resultSet) throws SQLException {
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
		if (resultSet.wasNull())
			return;
		this.setId(resultSet.getLong("nse_id"));
		this.setHandle(resultSet.getString("nse_handle"));
		this.setPunycodeName(resultSet.getString("nse_ldh_name"));
		this.setPort43(resultSet.getString("nse_port43"));
		this.setRarId(resultSet.getLong("rar_id"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getHandle());
		preparedStatement.setString(2, this.getLdhName());
		preparedStatement.setString(3, this.getPort43());
		preparedStatement.setLong(4, this.getRarId());
	}

}
