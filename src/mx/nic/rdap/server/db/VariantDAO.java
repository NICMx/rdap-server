package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access class for the Variant object.
 * 
 * @author evaldes
 *
 */
public class VariantDAO extends mx.nic.rdap.core.db.Variant implements DatabaseObject {

	/**
	 * Default constructor
	 */
	public VariantDAO() {
		super();
	}

	/**
	 * Constructor with result set
	 * 
	 * @param resultSet
	 */
	public VariantDAO(ResultSet resultSet) {
		super();
		try {
			loadFromDatabase(resultSet);
		} catch (SQLException e) {
			// TODO manage exception
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
		this.setId(resultSet.getLong("var_id"));
		this.setIdnTable(resultSet.getString("var_idn_table"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		// TODO Auto-generated method stub
		preparedStatement.setString(1, this.getIdnTable());
		preparedStatement.setLong(2, this.getDomainId());
	}

}
