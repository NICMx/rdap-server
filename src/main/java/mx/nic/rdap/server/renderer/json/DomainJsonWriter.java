package mx.nic.rdap.server.renderer.json;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.util.PrivacyUtil;

public class DomainJsonWriter {

	public static JsonObject getJson(Domain domain, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = PrivacyUtil.getDomainPrivacySettings();
		JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("objectClassName", "domain");
		JsonUtil.fillCommonRdapJsonObject(builder, domain, isAuthenticated, isOwner, settings,
				PrivacyUtil.getDomainRemarkPrivacySettings(), PrivacyUtil.getDomainLinkPrivacySettings(),
				PrivacyUtil.getDomainEventPrivacySettings());

		String key = "ldhName";
		String value = domain.getFQDN();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		if (domain.getUnicodeName() != null)
			if (domain.getLdhName().compareTo(domain.getUnicodeName()) != 0) {
				key = "unicodeName";
				value = domain.getUnicodeFQDN();
				if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
					builder.add(key, value);
			}

		key = "variants";
		if (PrivacyUtil.isObjectVisible(domain.getVariants(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, VariantJsonWriter.getJsonArray(domain.getVariants(), isAuthenticated, isOwner));

		key = "publicIds";
		if (PrivacyUtil.isObjectVisible(domain.getPublicIds(), key, settings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, PublicIdJsonWriter.getJsonArray(domain.getPublicIds(), isAuthenticated, isOwner,
					PrivacyUtil.getDomainPublicIdsPrivacySettings()));
		}

		key = "nameservers";
		if (PrivacyUtil.isObjectVisible(domain.getNameServers(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, NameserverJsonWriter.getJsonArray(domain.getNameServers(), isAuthenticated, isOwner));

		key = "secureDNS";
		if (PrivacyUtil.isObjectVisible(domain.getSecureDNS(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, SecureDNSJsonWriter.getJsonObject(domain.getSecureDNS(), isAuthenticated, isOwner));

		key = "network";
		if (PrivacyUtil.isObjectVisible(domain.getIpNetwork(), key, settings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, IpNetworkJsonWriter.getJson(domain.getIpNetwork(), isAuthenticated, isOwner));
		}

		return builder.build();
	}

}
