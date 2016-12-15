package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.server.PrivacyUtil;
import mx.nic.rdap.server.catalog.PrivacyStatus;

public class PublicIdParser {

	public static JsonArray getJsonArray(List<PublicId> publicIds, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (PublicId publicId : publicIds) {
			builder.add(getJson(publicId, isAuthenticated, isOwner, privacySettings));
		}

		return builder.build();
	}

	public static JsonObject getJson(PublicId publicId, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		String key = "type";
		if (PrivacyUtil.isObjectVisible(publicId.getType(), key, privacySettings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, publicId.getType());
		}

		key = "identifier";
		if (PrivacyUtil.isObjectVisible(publicId.getPublicId(), key, privacySettings.get(key), isAuthenticated,
				isOwner)) {
			builder.add(key, publicId.getPublicId());
		}

		return builder.build();
	}

}
