package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.VCard;

/**
 * DAO for {@link VCard}.
 * 
 * @author dhfelix
 *
 */
public class VCardDAO extends VCard implements DatabaseObject {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		setId(resultSet.getLong("vca_id"));
		setName(resultSet.getString("vca_name"));
		setCompanyName(resultSet.getString("vca_company_name"));
		setCompanyURL(resultSet.getString("vca_company_url"));
		setEmail(resultSet.getString("vca_email"));
		setVoice(resultSet.getString("vca_voice"));
		setCellphone(resultSet.getString("vca_cellphone"));
		setFax(resultSet.getString("vca_fax"));
		setJobTitle(resultSet.getString("vca_job_title"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, getName());
		preparedStatement.setString(2, getCompanyName());
		preparedStatement.setString(3, getCompanyURL());
		preparedStatement.setString(4, getEmail());
		preparedStatement.setString(5, getVoice());
		preparedStatement.setString(6, getCellphone());
		preparedStatement.setString(7, getFax());
		preparedStatement.setString(8, getJobTitle());
	}

}
