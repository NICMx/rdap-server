package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.server.renderer.json.JsonParser;

/**
 * DAO for the Link object.The object is a data structure that signify link an
 * object to other resources on the Internet.
 * 
 * @author dalpuche
 *
 */
public class LinkDAO extends Link implements DatabaseObject, JsonParser {

	/**
	 * Contructor default
	 */
	public LinkDAO() {
		super();
	}

	/**
	 * Construct a Link from a resulset
	 * 
	 * @throws SQLException
	 */
	public LinkDAO(ResultSet resultSet) throws SQLException {
		super();
		loadFromDatabase(resultSet);
	}

	/**
	 * Construct a "self" Link
	 * 
	 * @param objectClassName
	 * @param query
	 */
	public LinkDAO(String objectClassName, String query) {
		super();
		this.setValue("http://example.com/" + objectClassName + "/" + query);
		this.setRel("self");
		this.setHref("http://example.com/" + objectClassName + "/" + query);
		this.setType("application/rdap+json");
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
		this.setId(resultSet.getLong("lin_id"));
		this.setValue(resultSet.getString("lin_value"));
		this.setRel(resultSet.getString("lin_rel"));
		this.setHref(resultSet.getString("lin_href"));
		this.setHreflag(resultSet.getString("lin_hreflang"));
		this.setTitle(resultSet.getString("lin_title"));
		this.setMedia(resultSet.getString("lin_media"));
		this.setType(resultSet.getString("lin_type"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getValue());
		preparedStatement.setString(2, this.getRel());
		preparedStatement.setString(3, this.getHref());
		preparedStatement.setString(4, this.getHreflag());
		preparedStatement.setString(5, this.getTitle());
		preparedStatement.setString(6, this.getMedia());
		preparedStatement.setString(7, this.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.JsonParser#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (this.getValue() != null && !this.getValue().isEmpty())
			builder.add("value", this.getValue());
		if (this.getRel() != null && !this.getRel().isEmpty())
			builder.add("rel", this.getRel());
		builder.add("href", this.getHref());
		if (this.getHreflag() != null && !this.getHref().isEmpty())
			builder.add("hreflang", this.getHreflag());
		if (this.getTitle() != null && !this.getTitle().isEmpty())
			builder.add("title", this.getTitle());
		if (this.getMedia() != null && !this.getMedia().isEmpty())
			builder.add("media", this.getMedia());
		if (this.getType() != null && !this.getType().isEmpty())
			builder.add("type", this.getType());
		return builder.build();
	}

	public String toString() {
		return toJson().toString();
	}

}
