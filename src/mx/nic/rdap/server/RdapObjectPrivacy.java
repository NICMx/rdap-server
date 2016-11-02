package mx.nic.rdap.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.IDN;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.DsData;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.IpAddress;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nix.rdap.core.catalog.Rol;
import mx.nix.rdap.core.catalog.Status;
import mx.nix.rdap.core.catalog.VariantRelation;

public class RdapObjectPrivacy {

	private final static Logger logger = Logger.getLogger(RdapObjectPrivacy.class.getName());

	private static final Map<String, Map<String, PrivacyStatus>> PRIVACY_OBJECTS_SETTINGS = new HashMap<>();

	private static final String DEFAULT_PATH = "META-INF/privacy_default/";
	private static final String USER_PATH = "META-INF/privacy/";

	private static final String ENTITY = "entity";
	private static final String ENTITY_PUBLIC_ID = "entity_public_id";

	private static final String NAMESERVER = "nameserver";

	private static final String DOMAIN_PUBLIC_ID = "domain_public_id";
	private static final String DOMAIN_VARIANTS = "domain_variants_id";
	private static final String DOMAIN = "domain";
	private static final String SECURE_DNS = "secure_dns";
	private static final String DS_DATA = "ds_data";

	private static final String ENTITY_LINKS = "entity_links";
	private static final String ENTITY_REMARKS = "entity_remarks";
	private static final String ENTITY_EVENTS = "entity_events";

	private static final String DOMAIN_LINKS = "domain_links";
	private static final String DOMAIN_REMARKS = "domain_remarks";
	private static final String DOMAIN_EVENTS = "domain_events";

	private static final String NAMESERVER_LINKS = "nameserver_links";
	private static final String NAMESERVER_REMARKS = "nameserver_remarks";
	private static final String NAMESERVER_EVENTS = "nameserver_events";

	public static void loadPrivacySettings() throws IOException {
		loadObjectPrivacySettings(ENTITY);
		loadObjectPrivacySettings(ENTITY_PUBLIC_ID);
		loadObjectPrivacySettings(DOMAIN_PUBLIC_ID);
		loadObjectPrivacySettings(DOMAIN_VARIANTS);
		loadObjectPrivacySettings(DOMAIN);
		loadObjectPrivacySettings(NAMESERVER);
		loadObjectPrivacySettings(SECURE_DNS);
		loadObjectPrivacySettings(DS_DATA);
		loadObjectPrivacySettings(ENTITY_LINKS);
		loadObjectPrivacySettings(ENTITY_EVENTS);
		loadObjectPrivacySettings(ENTITY_REMARKS);
		loadObjectPrivacySettings(DOMAIN_LINKS);
		loadObjectPrivacySettings(DOMAIN_EVENTS);
		loadObjectPrivacySettings(DOMAIN_REMARKS);
		loadObjectPrivacySettings(NAMESERVER_LINKS);
		loadObjectPrivacySettings(NAMESERVER_EVENTS);
		loadObjectPrivacySettings(NAMESERVER_REMARKS);

	}

	private static void loadObjectPrivacySettings(String objectName) throws IOException {
		Properties properties = new Properties();
		ClassLoader classLoader = RdapObjectPrivacy.class.getClassLoader();
		HashMap<String, PrivacyStatus> objectProperties = new HashMap<>();
		try (InputStream in = classLoader.getResourceAsStream(DEFAULT_PATH + objectName + ".properties");) {
			properties.load(in);
		} catch (NullPointerException e) {
			logger.log(Level.WARNING, "Cannot load file: " + DEFAULT_PATH + objectName + ".properties");
			throw e;
		}

		InputStream in = classLoader.getResourceAsStream(USER_PATH + objectName + ".properties");
		if (in != null) {
			properties.load(in);
			in.close();
		}

		StringBuilder builder = new StringBuilder();
		boolean isInvalidProperties = false;
		Set<Object> keySet = properties.keySet();
		for (Object key : keySet) {

			// There is no empty value.
			if (((String) key).isEmpty()) {
				continue;
			}

			String property = properties.getProperty((String) key).trim();
			PrivacyStatus privacyProperty = PrivacyStatus.valueOf(property.toUpperCase());

			switch (privacyProperty) {
			case OWNER:
				break;
			case AUTHENTICATE:
				break;
			case ANY:
				break;
			case NONE:
				break;
			default:
				isInvalidProperties = true;
				builder.append(key + "=" + property + ", ");
				continue;
			}
			objectProperties.put((String) key, privacyProperty);
		}

		if (isInvalidProperties) {
			throw new RuntimeException(
					"Invalid privacy file '" + objectName + ".properties'.\n Invalid values: " + builder.toString());
		}

		PRIVACY_OBJECTS_SETTINGS.put(objectName, Collections.unmodifiableMap(objectProperties));
	}

	@SuppressWarnings("rawtypes")
	public static boolean isObjectVisible(Object objectValue, String attName, PrivacyStatus attPermission,
			boolean isAuthenticated, boolean isOwner) {
		if (objectValue == null)
			return false;

		if (objectValue instanceof String) {
			if (((String) objectValue).isEmpty()) {
				return false;
			}
		} else if (objectValue instanceof List && ((List) objectValue).isEmpty()) {
			return false;
		}

		if (attPermission == null) {
			throw new NullPointerException("Attribute '" + attName + "' does not have permission configured.");
		}

		boolean result = false;
		switch (attPermission) {
		case OWNER:
			if (isOwner) {
				result = true;
			}
			break;
		case AUTHENTICATE:
			if (isAuthenticated) {
				result = true;
			}
			break;
		case ANY:
			result = true;
			break;
		case NONE:
			break;
		}
		return result;
	}

	public static Map<String, PrivacyStatus> getEntityPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(ENTITY);
	}

	public static Map<String, PrivacyStatus> getEntityPublicIdsPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(ENTITY_PUBLIC_ID);
	}

	public static Map<String, PrivacyStatus> getNameserverPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(NAMESERVER);
	}

	public static Map<String, PrivacyStatus> getDomainPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(DOMAIN);
	}

	public static Map<String, PrivacyStatus> getDomainPublicIdsPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(DOMAIN_PUBLIC_ID);
	}

	public static Map<String, PrivacyStatus> getDomainVariantsPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(DOMAIN_VARIANTS);
	}

	public static Map<String, PrivacyStatus> getSecureDnsPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(SECURE_DNS);
	}

	public static Map<String, PrivacyStatus> getDsDataPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(DS_DATA);
	}

	public static Map<String, PrivacyStatus> getEntityLinkPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(ENTITY_LINKS);
	}

	public static Map<String, PrivacyStatus> getEntityRemarkPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(ENTITY_REMARKS);
	}

	public static Map<String, PrivacyStatus> getEntityEventPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(ENTITY_EVENTS);
	}

	public static Map<String, PrivacyStatus> getDomainLinkPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(DOMAIN_LINKS);
	}

	public static Map<String, PrivacyStatus> getDomainRemarkPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(DOMAIN_REMARKS);
	}

	public static Map<String, PrivacyStatus> getDomainEventPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(DOMAIN_EVENTS);
	}

	public static Map<String, PrivacyStatus> getNameserverLinkPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(NAMESERVER_LINKS);
	}

	public static Map<String, PrivacyStatus> getNameserverRemarkPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(NAMESERVER_REMARKS);
	}

	public static Map<String, PrivacyStatus> getNameserverEventPrivacySettings() {
		return PRIVACY_OBJECTS_SETTINGS.get(NAMESERVER_EVENTS);
	}

	public static JsonObject getDomainJson(Domain domain, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = RdapObjectPrivacy.getDomainPrivacySettings();
		JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("objectClassName", "domain");
		RdapObjectPrivacy.fillCommonRdapJsonObject(builder, domain, isAuthenticated, isOwner, settings,
				getDomainRemarkPrivacySettings(), getDomainLinkPrivacySettings(), getDomainEventPrivacySettings());

		String key = "ldhName";
		String value = domain.getLdhName();
		if (RdapObjectPrivacy.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "unicodeName";
		value = IDN.toUnicode(domain.getLdhName());
		if (RdapObjectPrivacy.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "variants";
		if (RdapObjectPrivacy.isObjectVisible(domain.getVariants(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, getVariansJsonArray(domain.getVariants(), isAuthenticated, isOwner));

		key = "publicIds";
		if (RdapObjectPrivacy.isObjectVisible(domain.getPublicIds(), key, settings.get(key), isAuthenticated,
				isOwner)) {
			builder.add(key, getPublicIds(domain.getPublicIds(), isAuthenticated, isOwner,
					RdapObjectPrivacy.getDomainPublicIdsPrivacySettings()));
		}

		key = "nameservers";
		if (RdapObjectPrivacy.isObjectVisible(domain.getNameServers(), key, settings.get(key), isAuthenticated,
				isOwner))
			builder.add(key,
					RdapObjectPrivacy.getNameserverJsonArray(domain.getNameServers(), isAuthenticated, isOwner));

		key = "secureDNS";
		if (RdapObjectPrivacy.isObjectVisible(domain.getSecureDNS(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, RdapObjectPrivacy.getSecureDnsJsonObject(domain.getSecureDNS(), isAuthenticated, isOwner));

		return builder.build();
	}

	public static JsonObject getSecureDnsJsonObject(SecureDNS secureDNS, boolean isAuthenticated, boolean isOwner) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		Map<String, PrivacyStatus> settings = RdapObjectPrivacy.getSecureDnsPrivacySettings();
		String key = "zoneSigned";
		if (RdapObjectPrivacy.isObjectVisible(secureDNS.getZoneSigned(), key, settings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, secureDNS.getZoneSigned());

		key = "delegationSigned";
		if (RdapObjectPrivacy.isObjectVisible(secureDNS.getDelegationSigned(), key, settings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, secureDNS.getDelegationSigned());

		key = "maxSigLife";
		if (RdapObjectPrivacy.isObjectVisible(secureDNS.getMaxSigLife(), key, settings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, secureDNS.getMaxSigLife());

		key = "dsData";
		if (RdapObjectPrivacy.isObjectVisible(secureDNS.getDsData(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, getDsDataJsonArray(secureDNS.getDsData(), isAuthenticated, isOwner));

		return builder.build();
	}

	public static JsonArray getDsDataJsonArray(List<DsData> dsDataList, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		Map<String, PrivacyStatus> privacySettings = getDsDataPrivacySettings();
		for (DsData dsData : dsDataList) {
			getDsDataJsonObject(dsData, isAuthenticated, isOwner, privacySettings);
		}

		return builder.build();
	}

	public static JsonObject getDsDataJsonObject(DsData dsData, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		String key = "keyTag";
		Integer intValue = dsData.getKeytag();
		if (RdapObjectPrivacy.isObjectVisible(intValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, intValue);

		key = "algorithm";
		intValue = dsData.getAlgorithm();
		if (RdapObjectPrivacy.isObjectVisible(intValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, intValue);

		key = "digest";
		String stringValue = dsData.getDigest();
		if (RdapObjectPrivacy.isObjectVisible(stringValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, stringValue);

		key = "digestType";
		intValue = dsData.getDigestType();
		if (RdapObjectPrivacy.isObjectVisible(intValue, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, intValue);

		key = "events";
		if (RdapObjectPrivacy.isObjectVisible(dsData.getEvents(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getEventsJsonArray(dsData.getEvents(), isAuthenticated, isOwner,
					getDomainEventPrivacySettings(), getDomainLinkPrivacySettings()));

		key = "links";
		if (RdapObjectPrivacy.isObjectVisible(dsData.getLinks(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getLinksJsonArray(dsData.getLinks(), isAuthenticated, isOwner,
					getDomainLinkPrivacySettings()));

		return builder.build();
	}

	public static JsonArray getVariansJsonArray(List<Variant> variants, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Variant variant : variants) {
			getVariantJsonObject(variant, isAuthenticated, isOwner);
		}

		return builder.build();
	}

	public static JsonObject getVariantJsonObject(Variant variant, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = getDomainVariantsPrivacySettings();
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "relation";
		if (RdapObjectPrivacy.isObjectVisible(variant.getRelations(), key, settings.get(key), isAuthenticated,
				isOwner)) {
			builder.add(key, getDomainRelationsJsonArray(variant.getRelations()));
		}

		key = "idnTable";
		String value = variant.getIdnTable();
		if (RdapObjectPrivacy.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "variantNames";
		if (RdapObjectPrivacy.isObjectVisible(variant.getVariantNames(), key, settings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getVariantNamesJsonArray(variant.getVariantNames()));

		return builder.build();
	}

	public static JsonArray getDomainRelationsJsonArray(List<VariantRelation> relations) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (VariantRelation relation : relations) {
			arrayBuilder.add(relation.getValue());
		}
		return arrayBuilder.build();
	}

	private static JsonArray getVariantNamesJsonArray(List<VariantName> variantNames) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

		for (VariantName variantName : variantNames) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("ldhName", variantName.getLdhName());
			builder.add("unicodeName", IDN.toUnicode(variantName.getLdhName()));
			arrayBuilder.add(builder);
		}

		return arrayBuilder.build();
	}

	public static JsonArray getNameserverJsonArray(List<Nameserver> nameserver, boolean isAuthenticated,
			boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Nameserver ns : nameserver) {
			builder.add(getNameserverJson(ns, isAuthenticated, isOwner));
		}

		return builder.build();
	}

	public static JsonObject getNameserverJson(Nameserver nameserver, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = RdapObjectPrivacy.getNameserverPrivacySettings();
		JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("objectClassName", "nameserver");
		RdapObjectPrivacy.fillCommonRdapJsonObject(builder, nameserver, isAuthenticated, isOwner, settings,
				getNameserverRemarkPrivacySettings(), getNameserverLinkPrivacySettings(),
				getNameserverEventPrivacySettings());

		String key = "ldhName";
		String value = nameserver.getLdhName();
		if (RdapObjectPrivacy.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "unicodeName";
		value = nameserver.getUnicodeName();
		if (RdapObjectPrivacy.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		RdapObjectPrivacy.fillIpAddresses(builder, nameserver.getIpAddresses(), isAuthenticated, isOwner, settings);

		return builder.build();
	}

	public static void fillIpAddresses(JsonObjectBuilder builder, NameserverIpAddressesStruct ipAdresses,
			boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> settings) {
		String key = "ipAddresses";
		if (!RdapObjectPrivacy.isObjectVisible(ipAdresses, key, settings.get(key), isAuthenticated, isOwner))
			return;

		JsonObjectBuilder addressBuilder = Json.createObjectBuilder();
		boolean insertIpAddresses = false;
		key = "v4";
		List<IpAddress> ipv4List = ipAdresses.getIpv4Adresses();
		if (RdapObjectPrivacy.isObjectVisible(ipv4List, key, settings.get(key), isAuthenticated, isOwner)) {
			addressBuilder.add(key, getIpAddressJsonArray(ipv4List));
			insertIpAddresses = true;
		}

		key = "v6";
		List<IpAddress> ipv6List = ipAdresses.getIpv6Adresses();
		if (RdapObjectPrivacy.isObjectVisible(ipv6List, key, settings.get(key), isAuthenticated, isOwner)) {
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

	public static JsonArray getEntitiesJsonArray(List<Entity> entities, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Entity entity : entities) {
			builder.add(getEntityJson(entity, isAuthenticated, isOwner));
		}

		return builder.build();
	}

	public static JsonObject getEntityJson(Entity entity, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = RdapObjectPrivacy.getEntityPrivacySettings();

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("objectClassName", "entity");

		RdapObjectPrivacy.fillCommonRdapJsonObject(builder, entity, isAuthenticated, isOwner, settings,
				getEntityRemarkPrivacySettings(), getEntityLinkPrivacySettings(), getEntityPrivacySettings());

		String key = "roles";
		if (RdapObjectPrivacy.isObjectVisible(entity.getRoles(), key, settings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, getEntityJsonRoles(entity.getRoles()));
		}

		key = "publicIds";
		if (RdapObjectPrivacy.isObjectVisible(entity.getPublicIds(), key, settings.get(key), isAuthenticated,
				isOwner)) {
			builder.add(key, getPublicIds(entity.getPublicIds(), isAuthenticated, isOwner,
					RdapObjectPrivacy.getEntityPublicIdsPrivacySettings()));
		}

		return builder.build();
	}

	public static JsonArray getEntityJsonRoles(List<Rol> roles) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Rol rol : roles) {
			builder.add(rol.getValue());
		}

		return builder.build();
	}

	public static JsonArray getPublicIds(List<PublicId> publicIds, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (PublicId publicId : publicIds) {
			builder.add(getPublicId(publicId, isAuthenticated, isOwner, privacySettings));
		}

		return builder.build();
	}

	public static JsonObject getPublicId(PublicId publicId, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		String key = "type";
		if (RdapObjectPrivacy.isObjectVisible(publicId.getType(), key, privacySettings.get(key), isAuthenticated,
				isOwner)) {
			builder.add(key, publicId.getType());
		}
		key = "identifier";
		if (RdapObjectPrivacy.isObjectVisible(publicId.getPublicId(), key, privacySettings.get(key), isAuthenticated,
				isOwner)) {
			builder.add(key, publicId.getPublicId());
		}

		return builder.build();
	}

	public static JsonObjectBuilder fillCommonRdapJsonObject(JsonObjectBuilder builder, RdapObject object,
			boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> privacySettings,
			Map<String, PrivacyStatus> remarkPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings,
			Map<String, PrivacyStatus> eventPrivacySettings) {

		String key = "handle";
		if (RdapObjectPrivacy.isObjectVisible(object.getHandle(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, object.getHandle());

		key = "remarks";
		if (RdapObjectPrivacy.isObjectVisible(object.getRemarks(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getRemarksJsonArray(object.getRemarks(), isAuthenticated, isOwner,
					remarkPrivacySettings, linkPrivacySettings));

		key = "links";
		if (RdapObjectPrivacy.isObjectVisible(object.getLinks(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getLinksJsonArray(object.getLinks(), isAuthenticated, isOwner,
					linkPrivacySettings));

		key = "events";
		if (RdapObjectPrivacy.isObjectVisible(object.getEvents(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getEventsJsonArray(object.getEvents(), isAuthenticated, isOwner,
					eventPrivacySettings, linkPrivacySettings));

		key = "status";
		if (RdapObjectPrivacy.isObjectVisible(object.getStatus(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getStatusJsonArray(object.getStatus()));

		key = "port43";
		if (RdapObjectPrivacy.isObjectVisible(object.getPort43(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, object.getPort43());

		key = "entities";
		if (RdapObjectPrivacy.isObjectVisible(object.getEntities(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getEntitiesJsonArray(object.getEntities(), isAuthenticated, isOwner));

		key = "lang";
		builder.add("lang", "en");// TODO: read the configuration
		return builder;
	}

	public static JsonArray getStatusJsonArray(List<Status> statusList) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Status s : statusList) {
			builder.add(s.getValue());
		}

		return builder.build();
	}

	public static JsonArray getLinksJsonArray(List<Link> links, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Link link : links) {
			builder.add(getLinkJsonObject(link, isAuthenticated, isOwner, privacySettings));
		}

		return builder.build();
	}

	public static JsonObject getLinkJsonObject(Link link, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "value";
		String value = link.getValue();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "rel";
		value = link.getRel();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "href";
		value = link.getHref();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "hreflang";
		value = link.getHreflag();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "title";
		value = link.getTitle();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "media";
		value = link.getMedia();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "type";
		value = link.getType();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		return builder.build();
	}

	public static JsonArray getEventsJsonArray(List<Event> events, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> eventPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Event event : events) {
			builder.add(getEventJsonObject(event, isAuthenticated, isOwner, eventPrivacySettings, linkPrivacySettings));
		}

		return builder.build();
	}

	public static JsonObject getEventJsonObject(Event event, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> eventPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "eventAction";
		String value = event.getEventAction().getValue();
		if (RdapObjectPrivacy.isObjectVisible(value, key, eventPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "eventActor";
		value = event.getEventActor();
		if (RdapObjectPrivacy.isObjectVisible(value, key, eventPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "eventDate";
		value = event.getEventDate().toInstant().toString();
		if (RdapObjectPrivacy.isObjectVisible(value, key, eventPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "links";
		if (RdapObjectPrivacy.isObjectVisible(event.getLinks(), key, eventPrivacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getLinksJsonArray(event.getLinks(), isAuthenticated, isOwner,
					linkPrivacySettings));

		return builder.build();
	}

	public static JsonArray getRemarksJsonArray(List<Remark> remarks, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> remarkPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Remark remark : remarks) {
			getRemarkJsonObject(remark, isAuthenticated, isOwner, remarkPrivacySettings, linkPrivacySettings);
		}

		return builder.build();
	}

	public static JsonObject getRemarkJsonObject(Remark remark, boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> remarkPrivacySettings, Map<String, PrivacyStatus> linkPrivacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "title";
		String value = remark.getTitle();
		if (RdapObjectPrivacy.isObjectVisible(value, key, remarkPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "description";
		if (RdapObjectPrivacy.isObjectVisible(remark.getDescriptions(), key, remarkPrivacySettings.get(key),
				isAuthenticated, isOwner))
			builder.add(key, getDescriptionsJsonArray(remark.getDescriptions()));

		key = "type";
		value = remark.getType();
		if (RdapObjectPrivacy.isObjectVisible(value, key, remarkPrivacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "links";
		if (RdapObjectPrivacy.isObjectVisible(remark.getLinks(), key, remarkPrivacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getLinksJsonArray(remark.getLinks(), isAuthenticated, isOwner,
					linkPrivacySettings));

		return builder.build();
	}

	public static JsonArray getDescriptionsJsonArray(List<RemarkDescription> descriptions) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (RemarkDescription description : descriptions) {
			builder.add(description.getDescription());
		}

		return builder.build();
	}
}
