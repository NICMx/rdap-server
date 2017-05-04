package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.util.PrivacyUtil;

public class LinkJsonWriter {

	public static JsonArray getJsonArray(List<Link> links, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Link link : links) {
			builder.add(getJsonObject(link, isAuthenticated, isOwner, privacySettings));
		}

		return builder.build();
	}

	private static JsonObject getJsonObject(Link link, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "value";
		String value = link.getValue();
		if (PrivacyUtil.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "rel";
		value = link.getRel();
		if (PrivacyUtil.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "href";
		value = link.getHref();
		if (PrivacyUtil.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "hreflang";
		List<String> hreflangs = link.getHreflang();
		if (PrivacyUtil.isObjectVisible(hreflangs, key, privacySettings.get(key), isAuthenticated, isOwner)) {
			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			for (String lang : hreflangs) {
				arrayBuilder.add(lang);
			}
			builder.add(key, arrayBuilder.build());
		}

		key = "title";
		value = link.getTitle();
		if (PrivacyUtil.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "media";
		value = link.getMedia();
		if (PrivacyUtil.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "type";
		value = link.getType();
		if (PrivacyUtil.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		return builder.build();
	}

	/**
	 * There is no privacy settings for links in notices, the server will show
	 * every data of remarks
	 */
	public static JsonArray getNoticeLinksJsonArray(List<Link> links) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Link link : links) {
			builder.add(getNoticesLinksJsonObject(link));
		}

		return builder.build();
	}

	/**
	 * There is no privacy settings for links in notices, the server will show
	 * every data of remarks
	 */
	private static JsonValue getNoticesLinksJsonObject(Link link) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "value";
		String value = link.getValue();
		if (value != null && !value.isEmpty())
			builder.add(key, value);

		key = "rel";
		value = link.getRel();
		if (value != null && !value.isEmpty())
			builder.add(key, value);

		key = "href";
		value = link.getHref();
		if (value != null && !value.isEmpty())
			builder.add(key, value);

		key = "hreflang";
		List<String> hreflangs = link.getHreflang();
		if (hreflangs != null && !hreflangs.isEmpty()) {
			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			for (String lang : hreflangs) {
				arrayBuilder.add(lang);
			}
			builder.add(key, arrayBuilder.build());
		}

		key = "title";
		value = link.getTitle();
		if (value != null && !value.isEmpty())
			builder.add(key, value);

		key = "media";
		value = link.getMedia();
		if (value != null && !value.isEmpty())
			builder.add(key, value);

		key = "type";
		value = link.getType();
		if (value != null && !value.isEmpty())
			builder.add(key, value);

		return builder.build();
	}

}
