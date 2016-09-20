package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * An object that can be store in the Database
 * 
 * @author dalpuche
 *
 */
public interface DatabaseObject {
	/**
	 * Load the information coming from the database in an instance of the
	 * object
	 * 
	 * @param resultSet
	 *            ResultSet from where all information is obtained
	 * 
	 */
	public void loadFromDatabase(ResultSet resultSet) throws SQLException;

	/**
	 * Puts the information contained in the object inside a PreparedStatement
	 * 
	 * @param preparedStatement
	 *            PreparedStatement where all the information is saved
	 */
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException;
}
