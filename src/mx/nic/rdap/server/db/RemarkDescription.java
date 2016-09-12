package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A description of the remark
 * 
 * @author dalpuche
 *
 */
public class RemarkDescription extends mx.nic.rdap.core.db.RemarkDescription implements DatabaseObject {

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
		preparedStatement.setString(2, this.getDescription());
		preparedStatement.setInt(3, this.getOrder());

	}

}
