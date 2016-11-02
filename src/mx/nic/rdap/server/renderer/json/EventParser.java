package mx.nic.rdap.server.renderer.json;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.db.EventDAO;

/**
 * Parser for the EventDAO object.
 * 
 * @author dalpuche
 *
 */
public class EventParser  implements JsonParser {

	private EventDAO event;
	
	public EventParser(EventDAO event) {
		this.event=event;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.JsonParser#toJson()
	 */
	@Override
	public JsonObject getJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("eventAction", event.getEventAction().getValue());
		if (event.getEventActor() != null && !event.getEventActor().isEmpty())
			builder.add("eventActor", event.getEventActor());
		builder.add("eventDate", event.getEventDate().toInstant().toString());
		if (event.getLinks() != null && !event.getLinks().isEmpty())
			builder.add("links", JsonUtil.getLinksJson(event.getLinks()));
		return builder.build();
	}

	public String toString() {
		return getJson().toString();
	}
}
