package mx.nic.rdap.server;

import java.io.IOException;
import java.io.InputStream;
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

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.server.db.LinkDAO;
import mx.nic.rdap.server.renderer.json.JsonUtil;
import mx.nix.rdap.core.catalog.Rol;
import mx.nix.rdap.core.catalog.Status;

public class RdapObjectPrivacy {

	private final static Logger logger = Logger.getLogger(RdapObjectPrivacy.class.getName());

	private static final Map<String, Map<String, PrivacyStatus>> PRIVACY_OBJECTS_SETTINGS = new HashMap<>();

	private static final String DEFAULT_PATH = "META-INF/privacy_default/";
	private static final String USER_PATH = "META-INF/privacy/";

	private static final String ENTITY = "entity";
	private static final String ENTITY_PUBLIC_ID = "entity_public_id";
	// private static final String DOMAIN = "domain";
	 private static final String NAMESERVER = "nameserver";

	public static void loadPrivacySettings() throws IOException {
		loadObjectPrivacySettings(ENTITY);
		loadObjectPrivacySettings(ENTITY_PUBLIC_ID);
		// loadObjectPrivacySettings(DOMAIN);
		 loadObjectPrivacySettings(NAMESERVER);
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

		RdapObjectPrivacy.fillCommonRdapJsonObject(builder, entity, isAuthenticated, isOwner, settings);

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
	
	public static JsonObject getNameserverJson(Nameserver nameserver, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = RdapObjectPrivacy.getNameserverPrivacySettings();
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		builder.add("objectClassName", "nameserver");
		RdapObjectPrivacy.fillCommonRdapJsonObject(builder, nameserver, isAuthenticated, isOwner, settings);
		
		// TODO 
//		builder.add("ldhName", this.getLdhName());
//		
//		builder.add("unicodeName", this.getUnicodeName());
//		
//		if ((this.getIpAddresses().getIpv4Adresses() != null || this.getIpAddresses().getIpv6Adresses() != null)
//				&& (!this.getIpAddresses().getIpv6Adresses().isEmpty()
//						|| !this.getIpAddresses().getIpv4Adresses().isEmpty()))
//			builder.add("ipAddresses", this.getIpAddressesJson());
		
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
			boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> privacySettings) {

		String key = "handle";
		if (RdapObjectPrivacy.isObjectVisible(object.getHandle(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, object.getHandle());

		key = "remarks";
		if (RdapObjectPrivacy.isObjectVisible(object.getRemarks(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getRemarksJsonArray(object.getRemarks(), isAuthenticated, isOwner, null)); // TODO check privacy settings

		key = "links";
		builder.add(key, RdapObjectPrivacy.getLinksJsonArray(object.getLinks(), isAuthenticated, isOwner, null)); // TODO check privacy settings

		key = "events";
		if (RdapObjectPrivacy.isObjectVisible(object.getEvents(), key, privacySettings.get(key), isAuthenticated,
				isOwner))
			builder.add(key, RdapObjectPrivacy.getEventsJsonArray(object.getEvents(), isAuthenticated, isOwner, null)); // TODO check privacy settings

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
	
	public static JsonArray getEventsJsonArray(List<Event> events, boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> privacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Event event : events) {
			builder.add(getEventJsonObject(event, isAuthenticated, isOwner, privacySettings));
		}

		return builder.build();
	}
	
	public static JsonObject getEventJsonObject(Event event, boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> privacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		String key = "eventAction";
		String value = event.getEventAction().getValue();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);
		
		key = "eventActor";
		value = event.getEventActor();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);
		
		key = "eventDate";
		value = event.getEventDate().toInstant().toString();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);
		
		key = "links";
		if (RdapObjectPrivacy.isObjectVisible(event.getLinks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, RdapObjectPrivacy.getLinksJsonArray(event.getLinks(), isAuthenticated, isOwner, null)); // TODO check privacy settings
		
		
		return builder.build();
	}

	public static JsonArray getRemarksJsonArray(List<Remark> remarks, boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> privacySettings) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		for (Remark remark : remarks) {
			getRemarkJsonObject(remark, isAuthenticated, isOwner, privacySettings);
		}
		
		return builder.build();
	}
	
	public static JsonObject getRemarkJsonObject(Remark remark, boolean isAuthenticated, boolean isOwner, Map<String, PrivacyStatus> privacySettings) {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		String key = "title";
		String value = remark.getTitle();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);
		
		key = "description";
		if (RdapObjectPrivacy.isObjectVisible(remark.getDescriptions(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, getDescriptionsJsonArray(remark.getDescriptions()));
		
		key = "type";
		value = remark.getType();
		if (RdapObjectPrivacy.isObjectVisible(value, key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "links";
		if (RdapObjectPrivacy.isObjectVisible(remark.getLinks(), key, privacySettings.get(key), isAuthenticated, isOwner))
			builder.add(key, RdapObjectPrivacy.getLinksJsonArray(remark.getLinks(), isAuthenticated, isOwner, null)); // TODO check privacy settings
		
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
