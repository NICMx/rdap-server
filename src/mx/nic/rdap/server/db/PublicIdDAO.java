package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.server.renderer.json.JsonParser;

/**
 * Data access class for the PublicId object. This data structure maps a public
 * identifier to an object class.
 * 
 * @author evaldes
 *
 */
public class PublicIdDAO extends PublicId implements DatabaseObject, JsonParser {

	public PublicIdDAO() {
		super();

	}

	public PublicIdDAO(ResultSet resultSet) throws SQLException {
		super();
		this.loadFromDatabase(resultSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.server.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getLong("pid_id"));
		this.setType(resultSet.getString("pid_type"));
		this.setPublicId(resultSet.getString("pid_identifier"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getType());
		preparedStatement.setString(2, this.getPublicId());
	}

	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		if (this.getType() != null && this.getType().isEmpty()) {
			builder.add("type", getType());
		}
		if (this.getType() != null && this.getPublicId().isEmpty()) {
			builder.add("identifier", getPublicId());
		}
		return builder.build();
	}

}
