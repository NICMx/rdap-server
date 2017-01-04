package mx.nic.rdap.server.renderer.json;

import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.IpAddress;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.util.PrivacyUtil;

public class NameserverJsonWriter {

	public static JsonArray getJsonArray(List<Nameserver> nameserver, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Nameserver ns : nameserver) {
			builder.add(getJson(ns, isAuthenticated, isOwner));
		}

		return builder.build();
	}

	public static JsonObject getJson(Nameserver nameserver, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = PrivacyUtil.getNameserverPrivacySettings();
		JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("objectClassName", "nameserver");
		if (!RdapConfiguration.useNameserverAsDomainAttribute())
			JsonUtil.fillCommonRdapJsonObject(builder, nameserver, isAuthenticated, isOwner, settings,
					PrivacyUtil.getNameserverRemarkPrivacySettings(), PrivacyUtil.getNameserverLinkPrivacySettings(),
					PrivacyUtil.getNameserverEventPrivacySettings());

		String key = "ldhName";
		String value = nameserver.getLdhName();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);
		// Point 2.9.4 of rdap operational profile by ICANN
		if (nameserver.getUnicodeName() != null)
			if (!RdapConfiguration.getServerProfile().equals(OperationalProfile.REGISTRY)
					|| (RdapConfiguration.getServerProfile().equals(OperationalProfile.REGISTRY)
							&& nameserver.getLdhName().compareTo(nameserver.getUnicodeName()) != 0)) {
				key = "unicodeName";
				value = nameserver.getUnicodeName();
				if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
					builder.add(key, value);
			}

		fillIpAddresses(builder, nameserver.getIpAddresses(), isAuthenticated, isOwner, settings);

		return builder.build();
	}

	private static void fillIpAddresses(JsonObjectBuilder builder, NameserverIpAddressesStruct ipAdresses,
			boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> settings) {
		String key = "ipAddresses";
		if (!PrivacyUtil.isObjectVisible(ipAdresses, key, settings.get(key), isAuthenticated, isOwner))
			return;

		JsonObjectBuilder addressBuilder = Json.createObjectBuilder();
		boolean insertIpAddresses = false;
		key = "v4";
		List<IpAddress> ipv4List = ipAdresses.getIpv4Adresses();
		if (PrivacyUtil.isObjectVisible(ipv4List, key, settings.get(key), isAuthenticated, isOwner)) {
			addressBuilder.add(key, getIpAddressJsonArray(ipv4List));
			insertIpAddresses = true;
		}

		key = "v6";
		List<IpAddress> ipv6List = ipAdresses.getIpv6Adresses();
		if (PrivacyUtil.isObjectVisible(ipv6List, key, settings.get(key), isAuthenticated, isOwner)) {
			addressBuilder.add(key, getIpAddressJsonArray(ipv6List));
			insertIpAddresses = true;
		}

		if (insertIpAddresses) {
			key = "ipAddresses";
			builder.add(key, addressBuilder.build());
		}
	}

	private static JsonArray getIpAddressJsonArray(List<IpAddress> addresses) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (IpAddress address : addresses) {
			builder.add(address.getAddress().getHostAddress());
		}
		return builder.build();
	}

}
