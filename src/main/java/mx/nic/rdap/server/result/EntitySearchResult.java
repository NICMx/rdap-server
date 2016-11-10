package mx.nic.rdap.server.result;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserRequestInfo;
import mx.nic.rdap.server.renderer.json.EntityParser;

/**
 * A result from an Entity search request.
 */
public class EntitySearchResult extends UserRequestInfo implements RdapResult {

	List<Entity> entities;

	public EntitySearchResult(List<Entity> entities, String userName) {
		this.entities = entities;
		setUserName(userName);
	}

	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Entity entity : entities) {
			arrB.add(EntityParser.getJson(entity, isUserAuthenticated(), isOwner(entity)));
		}
		builder.add("entitySearchResults", arrB);
		return builder.build();
	}

}
