package mx.nic.rdap.server.renderer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.notices.UserNotices;
import mx.nic.rdap.server.renderer.json.JsonUtil;
import mx.nic.rdap.server.renderer.json.RemarkJsonWriter;
import mx.nic.rdap.server.result.RdapResult;

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
		result.fillNotices();

		if (UserNotices.getTos() != null && !UserNotices.getTos().isEmpty()) {
			if (result.getNotices() == null)
				result.setNotices(new ArrayList<Remark>());
			result.getNotices().addAll(UserNotices.getTos());
		}
		if (result.getNotices() != null && !result.getNotices().isEmpty()) {
			object.add("notices", this.getNotices(result.getNotices()));
		}
		for (Entry<String, JsonValue> entry : result.toJson().entrySet()) {
			object.add(entry.getKey(), entry.getValue());
		}
		jsonWriter.writeObject(object.build());
	}

	private JsonArray getNotices(List<Remark> notices) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (Remark notice : notices) {
			arrayBuilder.add(RemarkJsonWriter.getNoticeJsonObject(notice));
		}
		return arrayBuilder.build();
	}

}
