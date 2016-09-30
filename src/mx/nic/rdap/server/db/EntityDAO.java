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
		setRarId(resultSet.getLong("rar_id"));
		setVCardId(resultSet.getLong("vca_id"));

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
		preparedStatement.setLong(3, getRarId());
		preparedStatement.setLong(4, getVCardId());
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

		if (getRegistrar() != null) {
			builder.add("entity", getEntities());
		}

		return builder.build();
	}

	private JsonArray getEntities() {
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		JsonObject json = ((RegistrarDAO) getRegistrar()).toJson();
		arrB.add(json);

		return arrB.build();
	}

	private JsonArray getJsonRoles() {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		builder.add(getRegistrar().getRol().getValue());

		return builder.build();
	}

}
