/**
 * 
 */
package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.db.model.CountryCodeModel;
import mx.nic.rdap.server.PrivacyStatus;
import mx.nic.rdap.server.PrivacyUtil;

public class AutnumParser {

	public static JsonArray getJsonArray(List<Autnum> autnums, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Autnum autnum : autnums) {
			builder.add(getJson(autnum, isAuthenticated, isOwner));
		}
		return builder.build();
	}

	public static JsonObject getJson(Autnum autnum, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = PrivacyUtil.getAutnumPrivacySettings();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("objectClassName", "autnum");
		JsonUtil.fillCommonRdapJsonObject(builder, autnum, isAuthenticated, isOwner, settings,
				PrivacyUtil.getAutnumRemarkPrivacySettings(), PrivacyUtil.getAutnumLinkPrivacySettings(),
				PrivacyUtil.getAutnumEventPrivacySettings());

		String key = "startAutnum";
		if (PrivacyUtil.isObjectVisible(autnum.getStartAutnum(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, autnum.getStartAutnum().toString());

		key = "endAutnum";
		if (PrivacyUtil.isObjectVisible(autnum.getEndAutnum(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, autnum.getEndAutnum().toString());

		key = "name";
		if (PrivacyUtil.isObjectVisible(autnum.getName(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, autnum.getName());

		key = "type";
		if (PrivacyUtil.isObjectVisible(autnum.getType(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, autnum.getType());

		key = "country";
		if (PrivacyUtil.isObjectVisible(autnum.getCountry(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, CountryCodeModel.getCountryNameById(autnum.getCountry()));

		return builder.build();
	}

}
