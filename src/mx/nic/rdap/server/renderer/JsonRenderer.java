package mx.nic.rdap.server.renderer;

import java.io.PrintWriter;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.Renderer;
import mx.nic.rdap.server.renderer.json.JsonUtil;

public class JsonRenderer implements Renderer {

	private String[] contentTypes = { //
			"application/rdap+json", //
			"application/json", //
	};

	public String[] getRequestContentTypes() {
		return contentTypes;
	}

	public String getResponseContentType() {
		return contentTypes[0];
	}

	@Override
	public void render(RdapResult result, PrintWriter writer) {
		JsonWriter jsonWriter = Json.createWriter(writer);
		JsonObjectBuilder object = Json.createObjectBuilder();
		object.add("rdapConformance", JsonUtil.getRdapConformance());
		for (Entry<String, JsonValue> entry : result.toJson().entrySet()) {
			object.add(entry.getKey(), entry.getValue());
		}
		jsonWriter.writeObject(object.build());
	}

}
