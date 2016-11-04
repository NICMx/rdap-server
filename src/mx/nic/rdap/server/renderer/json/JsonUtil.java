package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.server.PrivacyStatus;
import mx.nic.rdap.server.PrivacyUtil;
import mx.nix.rdap.core.catalog.Status;

/**
 * Utilities for json renderer
 */
public class JsonUtil {

	public static JsonArray getRdapConformance(String... others) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		arrayBuilder.add("rdap_level_0");
		if (others != null) {
			for (String s : others) {
				arrayBuilder.add(s);
			}
		}
		return arrayBuilder.build();
	}

	public static JsonObjectBuilder fillCommonRdapJsonObject(JsonObjectBuilder builder, RdapObject object,
			boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> privacySettings,
			Map<String, PrivacyStatus> remarkPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings,
			Map<String, PrivacyStatus> eventPrivacySettings) {

		String key = "handle";
		if (PrivacyUtil.isObjectVisible(object.getHandle(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, object.getHandle());

		key = "remarks";
		if (PrivacyUtil.isObjectVisible(object.getRemarks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, RemarkParser.getJsonArray(object.getRemarks(), isAuthenticated, isOwner,
					remarkPrivacySettings, linkPrivacySettings));

		key = "links";
		if (PrivacyUtil.isObjectVisible(object.getLinks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, LinkParser.getJsonArray(object.getLinks(), isAuthenticated, isOwner, linkPrivacySettings));

		key = "events";
		if (PrivacyUtil.isObjectVisible(object.getEvents(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, EventParser.getJsonArray(object.getEvents(), isAuthenticated, isOwner,
					eventPrivacySettings, linkPrivacySettings));

		key = "status";
		if (PrivacyUtil.isObjectVisible(object.getStatus(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, getStatusJsonArray(object.getStatus()));

		key = "port43";
		if (PrivacyUtil.isObjectVisible(object.getPort43(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, object.getPort43());

		key = "entities";
		if (PrivacyUtil.isObjectVisible(object.getEntities(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, EntityParser.getJsonArray(object.getEntities(), isAuthenticated, isOwner));

		key = "lang";
		builder.add("lang", "en");// TODO: read the configuration
		return builder;
	}

	public static JsonArray getStatusJsonArray(List<Status> statusList) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Status s : statusList) {
			builder.add(s.getValue());
		}

		return builder.build();
	}

}
