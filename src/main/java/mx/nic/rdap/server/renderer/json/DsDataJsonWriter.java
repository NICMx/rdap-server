package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.DsData;
import mx.nic.rdap.server.PrivacyUtil;
import mx.nic.rdap.server.catalog.PrivacyStatus;

public class DsDataParser {

	public static JsonArray getJsonArray(List<DsData> dsDataList, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		Map<String, PrivacyStatus> privacySettings = PrivacyUtil.getDsDataPrivacySettings();
		for (DsData dsData : dsDataList) {
			builder.add(getJsonObject(dsData, isAuthenticated, isOwner, privacySettings));
		}

		return builder.build();
	}

	public static JsonObject getJsonObject(DsData dsData, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		String key = "keyTag";
		Integer intValue = dsData.getKeytag();
		if (PrivacyUtil.isObjectVisible(intValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, intValue);

		key = "algorithm";
		intValue = dsData.getAlgorithm();
		if (PrivacyUtil.isObjectVisible(intValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, intValue);

		key = "digest";
		String stringValue = dsData.getDigest();
		if (PrivacyUtil.isObjectVisible(stringValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, stringValue);

		key = "digestType";
		intValue = dsData.getDigestType();
		if (PrivacyUtil.isObjectVisible(intValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, intValue);

		key = "events";
		if (PrivacyUtil.isObjectVisible(dsData.getEvents(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, EventParser.getJsonArray(dsData.getEvents(), isAuthenticated, isOwner,
					PrivacyUtil.getDomainEventPrivacySettings(), PrivacyUtil.getDomainLinkPrivacySettings()));

		key = "links";
		if (PrivacyUtil.isObjectVisible(dsData.getLinks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, LinkParser.getJsonArray(dsData.getLinks(), isAuthenticated, isOwner,
					PrivacyUtil.getDomainLinkPrivacySettings()));

		return builder.build();
	}

}
