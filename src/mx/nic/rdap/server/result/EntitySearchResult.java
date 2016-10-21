package mx.nic.rdap.server.result;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.db.EntityDAO;

/**
 * @author dhfelix
 *
 */
public class EntitySearchResult implements RdapResult {

	List<Entity> entities;

	public EntitySearchResult(List<Entity> entities) {
		this.entities = entities;
	}

	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Entity ent : entities) {
			JsonObject json = ((EntityDAO) ent).toJson();
			arrB.add(json);
		}
		builder.add("entitySearchResults", arrB);
		return builder.build();
	}

}
