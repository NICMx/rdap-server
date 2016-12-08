package mx.nic.rdap.server.result;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.db.EntityDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.renderer.json.EntityParser;

/**
 * A result from an Entity search request.
 */
public class EntitySearchResult extends RdapResult {

	List<EntityDAO> entities;

	public EntitySearchResult(String header, String contextPath, List<EntityDAO> entities, String userName) {
		this.entities = entities;
		this.userInfo = new UserInfo(userName);
		for (EntityDAO entity : entities) {
			entity.addSelfLinks(header, contextPath);
		}
	}

	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Entity entity : entities) {
			arrB.add(EntityParser.getJson(entity, userInfo.isUserAuthenticated(), userInfo.isOwner(entity)));
		}
		builder.add("entitySearchResults", arrB);
		return builder.build();
	}

}
