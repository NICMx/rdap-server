package mx.nic.rdap.server.renderer.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.catalog.Status;
import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.configuration.RdapConfiguration;
import mx.nic.rdap.server.util.PrivacyUtil;

/**
 * Utilities for json renderer
 */
public class JsonUtil {

	public static JsonArray getRdapConformance(String... others) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		arrayBuilder.add("rdap_level_0");
		if (others != null) {
			for (String s : others) {
				arrayBuilder.add(s);
			}
		}
		return arrayBuilder.build();
	}

	public static JsonObjectBuilder fillCommonRdapJsonObject(JsonObjectBuilder builder, RdapObject object,
			boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> privacySettings,
			Map<String, PrivacyStatus> remarkPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings,
			Map<String, PrivacyStatus> eventPrivacySettings) {

		Map<String, PrivacyStatus> allObjectPrivacySettings = getAllObjectPrivacySettings(object);
		PrivacyStatus priorityStatus = PrivacyUtil.getPriorityPrivacyStatus(isAuthenticated, isOwner,
				allObjectPrivacySettings);

		String key = "handle";
		if (PrivacyUtil.isObjectVisible(object.getHandle(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, object.getHandle());

		key = "remarks";
		Remark privacyRemark = PrivacyUtil.getObjectRemarkFromPrivacy(isAuthenticated, isOwner, priorityStatus);
		if (privacyRemark != null)
			object.getRemarks().add(privacyRemark);
		if (PrivacyUtil.isObjectVisible(object.getRemarks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, RemarkJsonWriter.getJsonArray(object.getRemarks(), isAuthenticated, isOwner,
					remarkPrivacySettings, linkPrivacySettings));

		key = "links";
		if (PrivacyUtil.isObjectVisible(object.getLinks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key,
					LinkJsonWriter.getJsonArray(object.getLinks(), isAuthenticated, isOwner, linkPrivacySettings));

		key = "events";
		if (PrivacyUtil.isObjectVisible(object.getEvents(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, EventJsonWriter.getJsonArray(object.getEvents(), isAuthenticated, isOwner,
					eventPrivacySettings, linkPrivacySettings));

		// Verify is we have to include a Status of "privacy"
		// (REMOVE,PRIVATE,OBSCURED)
		Status privacyStatus = PrivacyUtil.getObjectStatusFromPrivacy(isAuthenticated, isOwner, priorityStatus);
		if (privacyStatus != null)
			object.getStatus().add(privacyStatus);

		key = "status";
		if (PrivacyUtil.isObjectVisible(object.getStatus(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, getStatusJsonArray(object.getStatus()));

		key = "port43";
		if (PrivacyUtil.isObjectVisible(object.getPort43(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, object.getPort43());

		key = "entities";
		if (PrivacyUtil.isObjectVisible(object.getEntities(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, EntityJsonWriter.getJsonArray(object.getEntities(), isAuthenticated, isOwner));

		key = "lang";
		if (PrivacyUtil.isObjectVisible(RdapConfiguration.getServerLanguage(), key, privacySettings.get(key),
				isAuthenticated, isOwner))
			builder.add("lang", RdapConfiguration.getServerLanguage());
		return builder;
	}

	/**
	 * Used later to verify if the result will be affected by any object the
	 * privacy settings
	 */
	private static Map<String, PrivacyStatus> getAllObjectPrivacySettings(RdapObject object) {
		HashMap<String, PrivacyStatus> allObjectPrivacySettings = new HashMap<>();
		HashMap<String, PrivacyStatus> remarkPrivacySettings = new HashMap<>();
		HashMap<String, PrivacyStatus> linkPrivacySettings = new HashMap<>();
		HashMap<String, PrivacyStatus> eventPrivacySettings = new HashMap<>();
		if (object instanceof Autnum) {
			allObjectPrivacySettings.putAll(PrivacyUtil.getAutnumPrivacySettings());
			remarkPrivacySettings.putAll(PrivacyUtil.getAutnumRemarkPrivacySettings());
			linkPrivacySettings.putAll(PrivacyUtil.getAutnumLinkPrivacySettings());
			eventPrivacySettings.putAll(PrivacyUtil.getAutnumEventPrivacySettings());
		} else if (object instanceof Entity) {
			allObjectPrivacySettings.putAll(PrivacyUtil.getEntityPrivacySettings());
			remarkPrivacySettings.putAll(PrivacyUtil.getEntityRemarkPrivacySettings());
			linkPrivacySettings.putAll(PrivacyUtil.getEntityLinkPrivacySettings());
			eventPrivacySettings.putAll(PrivacyUtil.getEntityEventPrivacySettings());
			Map<String, PrivacyStatus> publicIdPrivacySettings = PrivacyUtil.getEntityPublicIdsPrivacySettings();
			for (String key : publicIdPrivacySettings.keySet()) {
				allObjectPrivacySettings.put("publicId-" + key, publicIdPrivacySettings.get(key));
			}
			Map<String, PrivacyStatus> vCardPrivacySettings = PrivacyUtil.getVCardPrivacySettings();
			for (String key : vCardPrivacySettings.keySet()) {
				allObjectPrivacySettings.put("vcard-" + key, vCardPrivacySettings.get(key));
			}
		} else if (object instanceof Domain) {
			allObjectPrivacySettings.putAll(PrivacyUtil.getDomainPrivacySettings());
			remarkPrivacySettings.putAll(PrivacyUtil.getDomainRemarkPrivacySettings());
			linkPrivacySettings.putAll(PrivacyUtil.getDomainLinkPrivacySettings());
			eventPrivacySettings.putAll(PrivacyUtil.getDomainEventPrivacySettings());
			Map<String, PrivacyStatus> publicIdPrivacySettings = PrivacyUtil.getDomainPublicIdsPrivacySettings();
			for (String key : publicIdPrivacySettings.keySet()) {
				allObjectPrivacySettings.put("publicId-" + key, publicIdPrivacySettings.get(key));
			}
			Map<String, PrivacyStatus> variantPrivacySettings = PrivacyUtil.getDomainVariantsPrivacySettings();
			for (String key : variantPrivacySettings.keySet()) {
				allObjectPrivacySettings.put("variant-" + key, variantPrivacySettings.get(key));
			}
			Map<String, PrivacyStatus> secureDNStPrivacySettings = PrivacyUtil.getSecureDnsPrivacySettings();
			for (String key : secureDNStPrivacySettings.keySet()) {
				allObjectPrivacySettings.put("secureDNS-" + key, secureDNStPrivacySettings.get(key));
			}
			Map<String, PrivacyStatus> dsDataPrivacySettings = PrivacyUtil.getDsDataPrivacySettings();
			for (String key : dsDataPrivacySettings.keySet()) {
				allObjectPrivacySettings.put("dsData-" + key, dsDataPrivacySettings.get(key));
			}
		} else if (object instanceof IpNetwork) {
			allObjectPrivacySettings.putAll(PrivacyUtil.getIpNetworkPrivacySettings());
			remarkPrivacySettings.putAll(PrivacyUtil.getIpNetworkPrivacySettings());
			linkPrivacySettings.putAll(PrivacyUtil.getAutnumLinkPrivacySettings());
			eventPrivacySettings.putAll(PrivacyUtil.getIpNetworkEventPrivacySettings());
		} else if (object instanceof Nameserver) {
			allObjectPrivacySettings.putAll(PrivacyUtil.getNameserverPrivacySettings());
			remarkPrivacySettings.putAll(PrivacyUtil.getNameserverRemarkPrivacySettings());
			linkPrivacySettings.putAll(PrivacyUtil.getNameserverLinkPrivacySettings());
			eventPrivacySettings.putAll(PrivacyUtil.getNameserverEventPrivacySettings());
		}

		// To avoid overwrite properties with the same name, add a prefix
		for (String key : remarkPrivacySettings.keySet()) {
			allObjectPrivacySettings.put("remark-" + key, remarkPrivacySettings.get(key));
		}
		for (String key : linkPrivacySettings.keySet()) {
			allObjectPrivacySettings.put("link-" + key, linkPrivacySettings.get(key));
		}
		for (String key : eventPrivacySettings.keySet()) {
			allObjectPrivacySettings.put("event-" + key, eventPrivacySettings.get(key));
		}
		return allObjectPrivacySettings;
	}

	private static JsonArray getStatusJsonArray(List<Status> statusList) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Status s : statusList) {
			builder.add(s.getValue());
		}

		return builder.build();
	}

}
