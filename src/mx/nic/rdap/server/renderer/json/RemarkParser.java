package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.server.PrivacyStatus;
import mx.nic.rdap.server.PrivacyUtil;

public class RemarkParser {
	public static JsonArray getJsonArray(List<Remark> remarks, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> remarkPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Remark remark : remarks) {
			getJsonObject(remark, isAuthenticated, isOwner, remarkPrivacySettings, linkPrivacySettings);
		}

		return builder.build();
	}

	public static JsonObject getJsonObject(Remark remark, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> remarkPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "title";
		String value = remark.getTitle();
		if (PrivacyUtil.isObjectVisible(value, key, remarkPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "description";
		if (PrivacyUtil.isObjectVisible(remark.getDescriptions(), key, remarkPrivacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, getDescriptionsJsonArray(remark.getDescriptions()));

		key = "type";
		value = remark.getType();
		if (PrivacyUtil.isObjectVisible(value, key, remarkPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "links";
		if (PrivacyUtil.isObjectVisible(remark.getLinks(), key, remarkPrivacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, LinkParser.getJsonArray(remark.getLinks(), isAuthenticated, isOwner, linkPrivacySettings));

		return builder.build();
	}

	private static JsonArray getDescriptionsJsonArray(List<RemarkDescription> descriptions) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (RemarkDescription description : descriptions) {
			builder.add(description.getDescription());
		}

		return builder.build();
	}
}
