package mx.nic.rdap.renderer;

import java.io.PrintWriter;

import javax.json.Json;
import javax.json.JsonWriter;

import mx.nic.rdap.RdapResult;
import mx.nic.rdap.Renderer;

public class JsonRenderer implements Renderer {

	@Override
	public void render(RdapResult result, PrintWriter writer) {
		JsonWriter jsonWriter = Json.createWriter(writer);
		jsonWriter.writeObject(result.toJson());
	}

}
