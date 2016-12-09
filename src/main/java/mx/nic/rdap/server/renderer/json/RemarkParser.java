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
			JsonObject jsonObject = getJsonObject(remark, isAuthenticated, isOwner, remarkPrivacySettings,
					linkPrivacySettings);
			builder.add(jsonObject);
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

		key = "lang";
		if (PrivacyUtil.isObjectVisible(remark.getLanguage(), key, remarkPrivacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add("lang", remark.getLanguage());

		return builder.build();
	}

	/**
	 * There is no privacy settings for notices, the server will show every data
	 * of remarks
	 */
	public static JsonObject getNoticeJsonObject(Remark remark) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "title";
		String value = remark.getTitle();
		if (value != null && !value.isEmpty())
			builder.add(key, value);

		key = "description";
		if (remark.getDescriptions() != null && !remark.getDescriptions().isEmpty())
			builder.add(key, getDescriptionsJsonArray(remark.getDescriptions()));

		key = "type";
		value = remark.getType();
		if (value != null && !value.isEmpty())
			builder.add(key, value);

		key = "links";
		if (remark.getLinks() != null && !remark.getLinks().isEmpty())
			builder.add(key, LinkParser.getNoticeLinksJsonArray(remark.getLinks()));

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
