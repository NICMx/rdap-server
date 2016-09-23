package mx.nic.rdap.server.renderer.json;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.db.EventDAO;
import mx.nic.rdap.server.db.LinkDAO;
import mx.nic.rdap.server.db.RemarkDAO;
import mx.nix.rdap.core.catalog.Status;

/**
 * @author dalpuche
 *
 */
public class JsonUtil {

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
		builder.add("lang", "en");// TODO: read the configuration
		return builder;
	}

	/**
	 * get the jsonArray of the links
	 * 
	 * @return
	 */
	public static JsonArray getLinksJson(List<Link> links) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (Link link : links) {
			arrayBuilder.add(((LinkDAO) link).toJson());
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
			arrayBuilder.add(((RemarkDAO) remark).toJson());
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
			arrayBuilder.add(((EventDAO) event).toJson());
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

}
