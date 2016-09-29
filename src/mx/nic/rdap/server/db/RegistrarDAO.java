package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Registrar;
import mx.nic.rdap.server.renderer.json.JsonParser;
import mx.nic.rdap.server.renderer.json.JsonUtil;

/**
 * DAO for the Registrar object that is represented by "entity object" in RDAP.
 * This is because we want to separete Registrars from registrants.
 * 
 * @author dhfelix
 *
 */
public class RegistrarDAO extends Registrar implements DatabaseObject, JsonParser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		setId(resultSet.getLong("rar_id"));
		setHandle(resultSet.getString("rar_handle"));
		setPort43(resultSet.getString("rar_port43"));
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

		if (getRol() != null) {
			builder.add("roles", getRoles());
		}

		// Get the common JsonObject of the rdap objects
		JsonUtil.getCommonRdapJsonObject(builder, this);

		return builder.build();
	}

	private JsonArray getRoles() {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		builder.add(getRol().getValue());

		return builder.build();
	}

}
