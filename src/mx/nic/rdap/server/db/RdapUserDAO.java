package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for the RDAPUser. The object is a data structure of an rdap user
 * information
 * 
 * @author dalpuche
 *
 */
public class RdapUserDAO implements DatabaseObject {

	private Long id;
	private String name;
	private String pass;
	private Integer maxSearchResults;
	private RdapUserRoleDAO userRole;

	/**
	 * Default contructor
	 */
	public RdapUserDAO() {
	}

	/**
	 * Construct a RdapUser from a resulset
	 * 
	 * @throws SQLException
	 */
	public RdapUserDAO(ResultSet resultSet) throws SQLException {
		super();
		loadFromDatabase(resultSet);
		userRole = new RdapUserRoleDAO();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pass == null) ? 0 : pass.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((maxSearchResults == null) ? 0 : maxSearchResults.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RdapUserDAO))
			return false;
		RdapUserDAO other = (RdapUserDAO) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pass == null) {
			if (other.pass != null)
				return false;
		} else if (!pass.equals(other.pass))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (maxSearchResults == null) {
			if (other.maxSearchResults != null)
				return false;
		} else if (!maxSearchResults.equals(other.maxSearchResults))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.server.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getLong("rus_id"));
		this.setName(resultSet.getString("rus_name"));
		this.setPass(resultSet.getString("rus_pass"));
		this.setMaxSearchResults(resultSet.getInt("rus_max_search_results"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getName());
		preparedStatement.setString(2, this.getPass());
		preparedStatement.setInt(3, this.getMaxSearchResults());

	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the pass
	 */
	public String getPass() {
		return pass;
	}

	/**
	 * @param pass
	 *            the pass to set
	 */
	public void setPass(String pass) {
		this.pass = pass;
	}

	/**
	 * @return the maxSearchResults
	 */
	public Integer getMaxSearchResults() {
		return maxSearchResults;
	}

	/**
	 * @param maxSearchResults
	 *            the maxSearchResults to set
	 */
	public void setMaxSearchResults(Integer maxSearchResults) {
		this.maxSearchResults = maxSearchResults;
	}

	/**
	 * @return the userRole
	 */
	public RdapUserRoleDAO getUserRole() {
		return userRole;
	}

	/**
	 * @param userRole
	 *            the userRole to set
	 */
	public void setUserRole(RdapUserRoleDAO userRole) {
		this.userRole = userRole;
		this.userRole.setUserName(this.getName());
	}

}
