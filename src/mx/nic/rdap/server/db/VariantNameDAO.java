package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data access class for the VariantName object. It contains the LDH names of a
 * variant
 * 
 * @author evaldes
 *
 */
public class VariantNameDAO extends mx.nic.rdap.core.db.VariantName implements DatabaseObject {

	public VariantNameDAO() {
		super();
	}

	public VariantNameDAO(ResultSet resultSet) {
		super();
		try {
			loadFromDatabase(resultSet);
		} catch (SQLException e) {
			// TODO Manage exception
		}
	}

	public String getUnicodeName() {
		// TODO Something
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.server.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setLdhName(resultSet.getString("vna_ldh_name"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getLdhName());
		preparedStatement.setLong(2, this.getVariantId());
	}

}
