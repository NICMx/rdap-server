package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.db.model.ZoneModel;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.util.PrivacyUtil;

public class DomainJsonWriter {

	public static JsonArray getJsonArray(List<Domain> domains, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Domain domain : domains) {
			builder.add(getJson(domain, isAuthenticated, isOwner));
		}

		return builder.build();
	}

	public static JsonObject getJson(Domain domain, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = PrivacyUtil.getDomainPrivacySettings();
		JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("objectClassName", "domain");
		JsonUtil.fillCommonRdapJsonObject(builder, domain, isAuthenticated, isOwner, settings,
				PrivacyUtil.getDomainRemarkPrivacySettings(), PrivacyUtil.getDomainLinkPrivacySettings(),
				PrivacyUtil.getDomainEventPrivacySettings());

		String key = "ldhName";
		String value = domain.getLdhName() + "." + ZoneModel.getZoneNameById(domain.getZoneId());
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);
		// Point 1.5.2 of rdap operational profile by ICANN
		if (RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE)
				|| (!RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE)
						&& domain.getLdhName().compareTo(domain.getUnicodeName()) != 0)) {
			key = "unicodeName";
			value = domain.getUnicodeName() + "." + ZoneModel.getZoneNameById(domain.getZoneId());
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
