package mx.nic.rdap.server.renderer.json;

import javax.json.JsonObject;

/**
 * Convert the object to JsonObject
 */
public interface JsonParser {

	public JsonObject getJson();
}
