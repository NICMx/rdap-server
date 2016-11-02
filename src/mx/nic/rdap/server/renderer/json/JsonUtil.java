package mx.nic.rdap.server.renderer.json;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.EntityDAO;
import mx.nic.rdap.db.EventDAO;
import mx.nic.rdap.db.LinkDAO;
import mx.nic.rdap.db.RemarkDAO;
import mx.nic.rdap.server.Util;
import mx.nix.rdap.core.catalog.Status;

/**
 * Utilities for json renderer
 * 
 * @author dalpuche
 *
 */
public class JsonUtil {

	public static HashMap<String, UserAccessLevel> loadAttributesLevel() {
		HashMap<String, UserAccessLevel> attributesLevels = new HashMap<>();
		return attributesLevels;
	}

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

	/**
	 * Get the common JsonObject of the rdap objects
	 * 
	 * @param object
	 * @return
	 */
	public static JsonObjectBuilder getCommonRdapJsonObject(JsonObjectBuilder builder, RdapObject object) {
		if (object.getHandle() != null && !object.getHandle().isEmpty())
			builder.add("handle", object.getHandle());

		if (object.getRemarks() != null && !object.getRemarks().isEmpty())
			builder.add("remarks", JsonUtil.getRemarksJson(object.getRemarks()));

		builder.add("links", JsonUtil.getLinksJson(object.getLinks()));

		if (object.getEvents() != null && !object.getEvents().isEmpty())
			builder.add("events", JsonUtil.getEventsJson(object.getEvents()));

		if (object.getStatus() != null && !object.getStatus().isEmpty())
			builder.add("status", JsonUtil.getStatusJson(object.getStatus()));

		if (object.getPort43() != null && !object.getPort43().isEmpty())
			builder.add("port43", object.getPort43());

		if (object.getEntities() != null && !object.getEntities().isEmpty())
			builder.add("entity", getEntitiesJson(object.getEntities()));
		builder.add("lang", "en");// TODO: read the configuration
		return builder;
	}

	public static JsonArray getEntitiesJson(List<Entity> entities) {
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Entity entity : entities) {
			EntityParser parser=new EntityParser((EntityDAO)entity);
			JsonObject json = parser.getJson();
			arrB.add(json);
		}

		return arrB.build();
	}

	/**
	 * get the jsonArray of the links
	 * 
	 * @return
	 */
	public static JsonArray getLinksJson(List<Link> links) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (Link link : links) {
			LinkParser parser=new LinkParser((LinkDAO)link);
			arrayBuilder.add(parser.getJson());
		}
		return arrayBuilder.build();
	}

	/**
	 * get the jsonArray of the remarks
	 * 
	 * @return
	 */
	public static JsonArray getRemarksJson(List<Remark> remarks) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (Remark remark : remarks) {
			RemarkParser parser=new RemarkParser((RemarkDAO)remark);
			arrayBuilder.add(parser.getJson());
		}	
		return arrayBuilder.build();
	}

	/**
	 * get the jsonArray of the events
	 * 
	 * @return
	 */
	public static JsonArray getEventsJson(List<Event> events) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (Event event : events) {
			EventParser parser=new EventParser((EventDAO)event);
			arrayBuilder.add(parser.getJson());
		}
		return arrayBuilder.build();
	}

	/**
	 * get the jsonArray of the statusList
	 * 
	 * @return
	 */
	public static JsonArray getStatusJson(List<Status> statusList) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (Status status : statusList) {
			arrayBuilder.add(status.getValue());
		}
		return arrayBuilder.build();
	}

	/**
	 * True is a user has authorization to see the attribute
	 * 
	 * @param userLevel
	 * @param attributeLevelRequired
	 * @return
	 */
	public static boolean isAttributeVisibleForUserLevel(UserAccessLevel userLevel,
			UserAccessLevel attributeLevelRequired) {
		if (attributeLevelRequired == UserAccessLevel.ANY) {
			return true;
		} else if (attributeLevelRequired == UserAccessLevel.AUTHENTICATED) {
			return userLevel == UserAccessLevel.AUTHENTICATED || userLevel == UserAccessLevel.OWNER;
		} else if (attributeLevelRequired == UserAccessLevel.OWNER) {
			return userLevel == UserAccessLevel.OWNER;
		}
		return false;
	}

//	/**
//	 * Retrieve the user accessLevel respect to an object
//	 * 
//	 * @param object
//	 * @return
//	 */
//	public static UserAccessLevel getUserAccessLevel(RdapObject object) {
//		if (Util.getAUTHENTICATED_USER() != null) {
//			// Validate is the user is the owner of the object
//			List<Rol> rols = Util.getConfiguratedOwnerRols();
//			if (true) {
//				// TODO:The magic stuff
//				return UserAccessLevel.OWNER;
//			}
//			return UserAccessLevel.AUTHENTICATED;
//		}
//		return UserAccessLevel.ANY;
//	}

	public static HashMap<String, UserAccessLevel> loadObjectPrivacyHash(String object) {
		HashMap<String, UserAccessLevel> privacyMap = new HashMap<String, UserAccessLevel>();
		Properties properties = new Properties();
		try {
			properties = Util.loadProperties("privacy/" + object);
		} catch (IOException e) {
			throw new RuntimeException("Failed at loading files.");
		}
		for (String key : properties.stringPropertyNames()) {
			UserAccessLevel privacyLevel = UserAccessLevel.getByName(properties.getProperty(key));
			privacyMap.put(key, privacyLevel);
		}
		return privacyMap;
	}

}
