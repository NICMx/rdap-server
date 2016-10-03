package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.renderer.json.JsonParser;
import mx.nic.rdap.server.renderer.json.JsonUtil;
import mx.nix.rdap.core.catalog.Rol;

/**
 * DAO for the Entity Object.This object class represents the information of
 * organizations, corporations, governments, non-profits, clubs, individual
 * persons, and informal groups of people.
 * 
 * @author dhfelix
 *
 */
public class EntityDAO extends Entity implements DatabaseObject, JsonParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		setId(resultSet.getLong("ent_id"));
		setHandle(resultSet.getString("ent_handle"));
		setPort43(resultSet.getString("ent_port43"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, getHandle());
		preparedStatement.setString(2, getPort43());
	}

	@Override
	public JsonObject toJson() {
		// Add the self link
		this.getLinks().add(new LinkDAO("entity", this.getHandle()));

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("objectClassName", "entity");

		// builder.add("roles", getRoles());

		// Get the common JsonObject of the rdap objects
		JsonUtil.getCommonRdapJsonObject(builder, this);

		if (getJsonRoles() != null && !getJsonRoles().isEmpty()) {
			builder.add("roles", getJsonRoles());
		}

		return builder.build();
	}

	private JsonArray getJsonRoles() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (Rol rol : getRoles()) {
			builder.add(rol.getValue());
		}
		return builder.build();
	}

}
