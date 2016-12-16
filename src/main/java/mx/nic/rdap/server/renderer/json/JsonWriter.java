package mx.nic.rdap.server.renderer.json;

import javax.json.JsonObject;

/**
 * Convert the object to JsonObject
 */
public interface JsonWriter {

	public JsonObject getJson();
}
