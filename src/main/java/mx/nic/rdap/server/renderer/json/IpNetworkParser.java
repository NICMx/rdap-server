package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.server.PrivacyStatus;
import mx.nic.rdap.server.PrivacyUtil;

public class IpNetworkParser {

	public static JsonArray getJsonArray(List<IpNetwork> ips, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (IpNetwork ip : ips) {
			builder.add(getJson(ip, isAuthenticated, isOwner));
		}

		return builder.build();
	}

	public static JsonObject getJson(IpNetwork ipNetwork, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = PrivacyUtil.getIpNetworkPrivacySettings();

		Map<String, PrivacyStatus> eventPrivacySettings = PrivacyUtil.getIpNetworkEventPrivacySettings();
		Map<String, PrivacyStatus> linkPrivacySettings = PrivacyUtil.getIpNetworkLinkPrivacySettings();
		Map<String, PrivacyStatus> remarkPrivacySettings = PrivacyUtil.getIpNetworkRemarkPrivacySettings();

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("objectClassName", "IpNetwork");

		String key = "startAddress";
		String value = ipNetwork.getStartAddress().getHostAddress();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "endAddress";
		value = ipNetwork.getEndAddress().getHostAddress();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "ipVersion";
		value = ipNetwork.getIpVersion().getVersionName();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "name";
		value = ipNetwork.getName();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "type";
		value = ipNetwork.getType();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "country";
		value = ipNetwork.getCountry();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		JsonUtil.fillCommonRdapJsonObject(builder, ipNetwork, isAuthenticated, isOwner, settings, remarkPrivacySettings,
				linkPrivacySettings, eventPrivacySettings);

		key = "parentHandle";
		value = ipNetwork.getParentHandle();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		return builder.build();
	}

}
