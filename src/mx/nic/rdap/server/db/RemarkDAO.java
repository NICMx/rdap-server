package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.server.renderer.json.JsonParser;
import mx.nic.rdap.server.renderer.json.JsonUtil;

/**
 * DAO for the remark Object.A remark structure denotes information about the
 * object class that contains it
 * 
 * @author dalpuche
 *
 */
public class RemarkDAO extends Remark implements DatabaseObject, JsonParser {

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
	 * @throws SQLException
	 */
	public RemarkDAO(ResultSet resultSet) throws SQLException {
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
		this.setId(resultSet.getLong("rem_id"));
		this.setTitle(resultSet.getString("rem_title"));
		this.setType(resultSet.getString("rem_type"));
		this.setLanguage(resultSet.getString("rem_lang"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getTitle());
		preparedStatement.setString(2, this.getType());
		preparedStatement.setString(3, this.getLanguage());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.JsonParser#toJson()
	 */
	@Override
	public JsonObject toJson() {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (this.getTitle() != null && !this.getTitle().isEmpty())
			builder.add("title", this.getTitle());
		builder.add("description", this.getDescriptionsJson());
		if (this.getType() != null && !this.getType().isEmpty())
			builder.add("type", this.getType());
		if (this.getLinks() != null && !this.getLinks().isEmpty())
			builder.add("links", JsonUtil.getLinksJson(this.getLinks()));
		return builder.build();
	}

	/**
	 * get the jsonArray of the remark's descriptions
	 * 
	 * @return
	 */
	private JsonArray getDescriptionsJson() {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (RemarkDescription description : this.getDescriptions()) {
			arrayBuilder.add(description.getDescription());
		}
		return arrayBuilder.build();
	}

	public String toString() {
		return toJson().toString();
	}
}
