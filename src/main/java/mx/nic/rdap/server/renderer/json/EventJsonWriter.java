package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.server.PrivacyUtil;
import mx.nic.rdap.server.catalog.PrivacyStatus;

public class EventJsonWriter {
	public static JsonArray getJsonArray(List<Event> events, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> eventPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Event event : events) {
			builder.add(getJsonObject(event, isAuthenticated, isOwner, eventPrivacySettings, linkPrivacySettings));
		}

		return builder.build();
	}

	public static JsonObject getJsonObject(Event event, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> eventPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "eventAction";
		String value = event.getEventAction().getValue();
		if (PrivacyUtil.isObjectVisible(value, key, eventPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "eventActor";
		value = event.getEventActor();
		if (PrivacyUtil.isObjectVisible(value, key, eventPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "eventDate";
		value = event.getEventDate().toInstant().toString();
		if (PrivacyUtil.isObjectVisible(value, key, eventPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "links";
		if (PrivacyUtil.isObjectVisible(event.getLinks(), key, eventPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, LinkJsonWriter.getJsonArray(event.getLinks(), isAuthenticated, isOwner, linkPrivacySettings));

		return builder.build();
	}
}
