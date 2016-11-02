package mx.nic.rdap.server.result;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.db.EntityDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.renderer.json.EntityParser;

/**
 * @author dhfelix
 *
 */
public class EntitySearchResult implements RdapResult {

	List<EntityDAO> entities;

	public EntitySearchResult(List<EntityDAO> entities) {
		this.entities = entities;
	}

	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (EntityDAO entity : entities) {
			EntityParser parser=new EntityParser(entity);
			JsonObject json = parser.getJson();
			arrB.add(json);
		}
		builder.add("entitySearchResults", arrB);
		return builder.build();
	}

}
