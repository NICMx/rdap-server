package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.catalog.Role;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.util.PrivacyUtil;

public class EntityJsonWriter {

	public static JsonObject getJson(Entity entity, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = PrivacyUtil.getEntityPrivacySettings();

		Map<String, PrivacyStatus> eventPrivacySettings = PrivacyUtil.getEntityEventPrivacySettings();
		Map<String, PrivacyStatus> linkPrivacySettings = PrivacyUtil.getEntityLinkPrivacySettings();
		Map<String, PrivacyStatus> publicIdsPrivacySettings = PrivacyUtil.getEntityPublicIdsPrivacySettings();
		Map<String, PrivacyStatus> remarkPrivacySettings = PrivacyUtil.getEntityRemarkPrivacySettings();

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("objectClassName", "entity");

		JsonUtil.fillCommonRdapJsonObject(builder, entity, isAuthenticated, isOwner, settings, remarkPrivacySettings,
				linkPrivacySettings, eventPrivacySettings);

		String key = "roles";
		if (PrivacyUtil.isObjectVisible(entity.getRoles(), key, settings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, getRolesJsonArray(entity.getRoles()));
		}

		key = "publicIds";
		if (PrivacyUtil.isObjectVisible(entity.getPublicIds(), key, settings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, PublicIdJsonWriter.getJsonArray(entity.getPublicIds(), isAuthenticated, isOwner,
					publicIdsPrivacySettings));
		}

		key = "networks";
		if (PrivacyUtil.isObjectVisible(entity.getIpNetworks(), key, settings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, IpNetworkJsonWriter.getJsonArray(entity.getIpNetworks(), isAuthenticated, isOwner));
		}

		key = "autnums";
		if (PrivacyUtil.isObjectVisible(entity.getAutnums(), key, settings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, AutnumJsonWriter.getJsonArray(entity.getAutnums(), isAuthenticated, isOwner));
		}

		key = "vcardArray";
		if (PrivacyUtil.isObjectVisible(entity.getVCardList(), key, settings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, VCardJsonWriter.getJson(entity.getVCardList().get(0), isAuthenticated, isOwner));
		}

		return builder.build();
	}

	public static JsonArray getJsonArray(List<Entity> entities, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Entity entity : entities) {
			builder.add(getJson(entity, isAuthenticated, isOwner));
		}

		return builder.build();
	}

	private static JsonArray getRolesJsonArray(List<Role> roles) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Role role : roles) {
			builder.add(role.getValue());
		}

		return builder.build();
	}

}
