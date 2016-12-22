package mx.nic.rdap.server.renderer;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.json.JsonWriter;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.Renderer;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.operational.profile.OperationalProfileValidator;
import mx.nic.rdap.server.renderer.json.JsonUtil;
import mx.nic.rdap.server.renderer.json.RemarkJsonWriter;

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

		// Point 1.4.4 of rdap operational profile by ICANN
		if (!RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE)) {
			if (OperationalProfileValidator.validateTermsOfService()) {
				result.getNotices().add(JsonUtil.getTermsOfServiceNotice());
			}
		}

		if (result.getNotices() != null && !result.getNotices().isEmpty()) {
			object.add("notices", this.getNotices(result.getNotices()));
		}
		// Point 1.4.10 of rdap operational profile by ICANN
		if (!RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE)) {
			object.add("remarks", JsonUtil.getOperationalProfileRemark());
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
