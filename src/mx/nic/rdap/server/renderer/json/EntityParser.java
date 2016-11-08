package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.PrivacyStatus;
import mx.nic.rdap.server.PrivacyUtil;
import mx.nix.rdap.core.catalog.Rol;

public class EntityParser {

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
			builder.add(key, PublicIdParser.getJsonArray(entity.getPublicIds(), isAuthenticated, isOwner,
					publicIdsPrivacySettings));
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

	private static JsonArray getRolesJsonArray(List<Rol> roles) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Rol rol : roles) {
			builder.add(rol.getValue());
		}

		return builder.build();
	}

}
