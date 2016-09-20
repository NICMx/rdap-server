package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.RemarkDescription;

/**
 * A description of the remark
 * 
 * @author dalpuche
 *
 */
public class RemarkDescriptionDAO extends RemarkDescription implements DatabaseObject {

	/**
	 * Constructor default
	 */
	public RemarkDescriptionDAO() {
		super();
	}

	/**
	 * @throws SQLException
	 * 
	 */
	public RemarkDescriptionDAO(ResultSet resultSet) throws SQLException {
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
		this.setRemarkId(resultSet.getLong("rem_id"));
		this.setDescription(resultSet.getString("rem_desc_description"));
		this.setOrder(resultSet.getInt("rem_desc_order"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setInt(1, this.getOrder());
		preparedStatement.setLong(2, this.getRemarkId());
		preparedStatement.setString(3, this.getDescription());

	}

}
