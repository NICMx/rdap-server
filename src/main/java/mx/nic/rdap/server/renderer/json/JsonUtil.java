package mx.nic.rdap.server.renderer.json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.catalog.RemarkType;
import mx.nic.rdap.core.catalog.Status;
import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.PrivacyStatus;
import mx.nic.rdap.server.PrivacyUtil;
import mx.nic.rdap.server.RdapConfiguration;

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
		PrivacyStatus priorityStatus = getPriorityPrivacyStatus(isAuthenticated, isOwner, allObjectPrivacySettings);

		String key = "handle";
		if (PrivacyUtil.isObjectVisible(object.getHandle(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, object.getHandle());

		key = "remarks";
		Remark privacyRemark = getObjectRemarkFromPrivacy(isAuthenticated, isOwner, priorityStatus);
		if (privacyRemark != null)
			object.getRemarks().add(privacyRemark);
		if (PrivacyUtil.isObjectVisible(object.getRemarks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, RemarkParser.getJsonArray(object.getRemarks(), isAuthenticated, isOwner,
					remarkPrivacySettings, linkPrivacySettings));

		key = "links";
		if (PrivacyUtil.isObjectVisible(object.getLinks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, LinkParser.getJsonArray(object.getLinks(), isAuthenticated, isOwner, linkPrivacySettings));

		key = "events";
		if (PrivacyUtil.isObjectVisible(object.getEvents(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, EventParser.getJsonArray(object.getEvents(), isAuthenticated, isOwner,
					eventPrivacySettings, linkPrivacySettings));

		// Verify is we have to include a Status of "privacy"
		// (REMOVE,PRIVATE,OBSCURED)
		Status privacyStatus = getObjectStatusFromPrivacy(isAuthenticated, isOwner, priorityStatus);
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
			builder.add(key, EntityParser.getJsonArray(object.getEntities(), isAuthenticated, isOwner));

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
	public static Map<String, PrivacyStatus> getAllObjectPrivacySettings(RdapObject object) {
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

	public static JsonArray getStatusJsonArray(List<Status> statusList) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Status s : statusList) {
			builder.add(s.getValue());
		}

		return builder.build();
	}

	/**
	 * Return the privacy status with most priority.something like:
	 * none>owner>authenticate>any
	 */
	private static PrivacyStatus getPriorityPrivacyStatus(boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		// First check if all the privacys settings are in "Any"
		if (!privacySettings.containsValue(PrivacyStatus.AUTHENTICATE)
				&& !privacySettings.containsValue(PrivacyStatus.OWNER)
				&& !privacySettings.containsValue(PrivacyStatus.NONE)) {
			return PrivacyStatus.ANY;
		} // Then, validate if all the privacy is
		else if (privacySettings.containsValue(PrivacyStatus.NONE)) {
			return PrivacyStatus.NONE;
		} else if (privacySettings.containsValue(PrivacyStatus.OWNER) && !isOwner) {
			return PrivacyStatus.OWNER;
		} else if (privacySettings.containsValue(PrivacyStatus.AUTHENTICATE) && !isAuthenticated) {
			return PrivacyStatus.AUTHENTICATE;
		} else
			return PrivacyStatus.ANY;
	}

	public static Status getObjectStatusFromPrivacy(boolean isAuthenticated, boolean isOwner,
			PrivacyStatus priorityStatus) {
		if (priorityStatus.equals(PrivacyStatus.ANY)) {
			return null;
		} else if (priorityStatus.equals(PrivacyStatus.NONE)) {
			return Status.REMOVED;
		} else if (priorityStatus.equals(PrivacyStatus.OWNER)) {
			return Status.PRIVATE;
		} else if (priorityStatus.equals(PrivacyStatus.AUTHENTICATE)) {
			return Status.PRIVATE;
		} else
			return null;
	}

	private static Remark getObjectRemarkFromPrivacy(boolean isAuthenticated, boolean isOwner,
			PrivacyStatus priorityStatus) {
		if (priorityStatus.equals(PrivacyStatus.ANY)) {
			return null;
		} else if (priorityStatus.equals(PrivacyStatus.NONE)) {
			return new Remark(RemarkType.OBJECT_AUTHORIZATION);
		} else if (priorityStatus.equals(PrivacyStatus.OWNER)) {
			return new Remark(RemarkType.OBJECT_AUTHORIZATION);
		} else if (priorityStatus.equals(PrivacyStatus.AUTHENTICATE)) {
			return new Remark(RemarkType.OBJECT_AUTHORIZATION);
		} else
			return new Remark(RemarkType.OBJECT_UNEXPLAINABLE);
	}

	public static Remark getPrivacyNotice(boolean isAuthenticated, boolean isOwner, PrivacyStatus priorityStatus) {
		if (priorityStatus.equals(PrivacyStatus.ANY)) {
			return null;
		} else if (priorityStatus.equals(PrivacyStatus.NONE)) {
			return new Remark(RemarkType.RESULT_SET_AUTHORIZATION);
		} else if (priorityStatus.equals(PrivacyStatus.OWNER)) {
			return new Remark(RemarkType.RESULT_SET_AUTHORIZATION);
		} else if (priorityStatus.equals(PrivacyStatus.AUTHENTICATE)) {
			return new Remark(RemarkType.RESULT_SET_AUTHORIZATION);
		} else
			return new Remark(RemarkType.RESULT_SET_UNEXPLAINABLE);
	}
}
