package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.VCardPostalInfo;

/**
 * DAO for {@link VCardPostalInfo}
 * 
 * @author dhfelix
 *
 */
public class VCardPostalInfoDAO extends VCardPostalInfo implements DatabaseObject {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		setId(resultSet.getLong("vpi_id"));
		setVCardId(resultSet.getLong("vca_id"));
		setType(resultSet.getString("vpi_type"));
		setCountry(resultSet.getString("vpi_country"));
		setCity(resultSet.getString("vpi_city"));
		setStreet1(resultSet.getString("vpi_street1"));
		setStreet2(resultSet.getString("vpi_street2"));
		setStreet3(resultSet.getString("vpi_street3"));
		setState(resultSet.getString("vpi_state"));
		setPostalCode(resultSet.getString("vpi_postal_code"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setLong(1, getVCardId());
		preparedStatement.setString(2, getType());
		preparedStatement.setString(3, getCountry());
		preparedStatement.setString(4, getCity());
		preparedStatement.setString(5, getStreet1());
		preparedStatement.setString(6, getStreet2());
		preparedStatement.setString(7, getStreet3());
		preparedStatement.setString(8, getState());
		preparedStatement.setString(9, getPostalCode());
	}
}
