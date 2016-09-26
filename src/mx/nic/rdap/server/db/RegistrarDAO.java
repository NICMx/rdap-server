package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.Registrar;

/**
 * DAO for the Registrar object that is represented by "entity object" in RDAP.
 * This is because we want to separete Registrars from registrants.
 * 
 * @author dhfelix
 *
 */
public class RegistrarDAO extends Registrar implements DatabaseObject {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		setId(resultSet.getLong("rar_id"));
		setHandle(resultSet.getString("rar_handle"));
		setPort43(resultSet.getString("rar_port43"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, getHandle());
		preparedStatement.setString(2, getPort43());
	}

}
