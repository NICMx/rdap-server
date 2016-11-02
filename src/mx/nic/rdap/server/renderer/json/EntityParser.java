package mx.nic.rdap.server.renderer.json;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.db.EntityDAO;
import mx.nic.rdap.db.LinkDAO;
import mx.nix.rdap.core.catalog.Rol;

/**
 * Parser for the EntityDAO Object.
 * 
 * @author dhfelix
 *
 */
public class EntityParser   implements JsonParser {

	private EntityDAO entity;
	
	public EntityParser(EntityDAO entity) {
		this.entity=entity;
	}
	
	
	@Override
	public JsonObject getJson() {
		// Add the self link
		entity.getLinks().add(new LinkDAO("entity", entity.getHandle()));

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("objectClassName", "entity");

		// Get the common JsonObject of the rdap objects
		JsonUtil.getCommonRdapJsonObject(builder, entity);

		if (getJsonRoles() != null && !getJsonRoles().isEmpty()) {
			builder.add("roles", this.getJsonRoles());
		}

		return builder.build();
	}

	private JsonArray getJsonRoles() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (Rol rol : entity.getRoles()) {
			builder.add(rol.getValue());
		}
		return builder.build();
	}

}
