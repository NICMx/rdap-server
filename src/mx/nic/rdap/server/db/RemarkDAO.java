package mx.nic.rdap.server.db;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.db.model.RemarkDescriptionModel;

/**
 * DAO for the remark Object.A remark structure denotes information about the
 * object class that contains it
 * 
 * @author dalpuche
 *
 */
public class RemarkDAO extends Remark implements DatabaseObject {

	/**
	 * Constructor default
	 */
	public RemarkDAO() {
		super();
	}

	/**
	 * Construct the Remark from a resulset
	 * 
	 * @param resultSet
	 */
	public RemarkDAO(ResultSet resultSet) {
		super();
		try {
			loadFromDatabase(resultSet);
		} catch (SQLException e) {
			// TODO Manage the exception
		}
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
		this.setId(resultSet.getLong("rem_id"));
		this.setTitle(resultSet.getString("rem_title"));
		this.setType(resultSet.getString("rem_type"));
		this.setLanguage(resultSet.getString("rem_lang"));
		try {
			this.setDescriptions(RemarkDescriptionModel.findByRemarkId(this.getId()));
		} catch (IOException e) {
			// TODO Manage the exception
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
		preparedStatement.setLong(1, this.getId());
		preparedStatement.setString(2, this.getTitle());
		preparedStatement.setString(3, this.getType());
		preparedStatement.setString(4, this.getLanguage());
	}

}
