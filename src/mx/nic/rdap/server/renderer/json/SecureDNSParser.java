package mx.nic.rdap.server.renderer.json;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.server.PrivacyStatus;
import mx.nic.rdap.server.PrivacyUtil;

public class SecureDNSParser {

	public static JsonObject getJsonObject(SecureDNS secureDNS, boolean isAuthenticated, boolean isOwner) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		Map<String, PrivacyStatus> settings = PrivacyUtil.getSecureDnsPrivacySettings();
		String key = "zoneSigned";
		if (PrivacyUtil.isObjectVisible(secureDNS.getZoneSigned(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, secureDNS.getZoneSigned());

		key = "delegationSigned";
		if (PrivacyUtil.isObjectVisible(secureDNS.getDelegationSigned(), key, settings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, secureDNS.getDelegationSigned());

		key = "maxSigLife";
		if (PrivacyUtil.isObjectVisible(secureDNS.getMaxSigLife(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, secureDNS.getMaxSigLife());

		key = "dsData";
		if (PrivacyUtil.isObjectVisible(secureDNS.getDsData(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, DsDataParser.getJsonArray(secureDNS.getDsData(), isAuthenticated, isOwner));

		return builder.build();
	}

}
