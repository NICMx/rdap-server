package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The nameserver object class represents information regarding DNS nameservers
 * used in both forward and reverse DNS
 * 
 * @author dalpuche
 *
 */
public class Nameserver extends mx.nic.rdap.core.db.Nameserver implements DatabaseObject {

	/**
	 * Return the ldh name from the punycode name
	 * 
	 * @return the ldh name of the domain
	 */
	public String getLdhName() {
		return this.getPunycodeName();// ldh name is the punycode
	}

	/**
	 * Return the unicode name from the punycode name
	 * 
	 * @return the unicode name of the domain
	 */
	public String getUnicodeName() {
		// TODO:Magic stuff here
		return "";// ldh name is the punycode
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		// TODO Auto-generated method stub
	}

}
