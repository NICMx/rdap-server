package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import mx.nic.rdap.core.db.KeyData;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.util.PrivacyUtil;

public class KeyDataJsonWriter {

	public static JsonArray getJsonArray(List<KeyData> keyDataList, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		Map<String, PrivacyStatus> privacySettings = PrivacyUtil.getKeyDataPrivacySettings();
		for (KeyData keyData : keyDataList) {
			builder.add(getJsonObject(keyData, isAuthenticated, isOwner, privacySettings));
		}
		return builder.build();
	}

	private static JsonValue getJsonObject(KeyData keyData, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "flags";
		Integer intValue = keyData.getFlags();
		if (PrivacyUtil.isObjectVisible(intValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, intValue);

		key = "protocol";
		intValue = keyData.getProtocol();
		if (PrivacyUtil.isObjectVisible(intValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, intValue);

		key = "publicKey";
		String stringValue = keyData.getPublicKey();
		if (PrivacyUtil.isObjectVisible(stringValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, stringValue);

		key = "algorithm";
		intValue = keyData.getAlgorithm();
		if (PrivacyUtil.isObjectVisible(intValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, intValue);

		key = "events";
		if (PrivacyUtil.isObjectVisible(keyData.getEvents(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, EventJsonWriter.getJsonArray(keyData.getEvents(), isAuthenticated, isOwner,
					PrivacyUtil.getDomainEventPrivacySettings(), PrivacyUtil.getDomainLinkPrivacySettings()));

		key = "links";
		if (PrivacyUtil.isObjectVisible(keyData.getLinks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, LinkJsonWriter.getJsonArray(keyData.getLinks(), isAuthenticated, isOwner,
					PrivacyUtil.getDomainLinkPrivacySettings()));

		return builder.build();
	}

}
