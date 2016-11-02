package mx.nic.rdap.server.renderer.json;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.db.RemarkDAO;

/**
 * Parser for the remark Object.
 * 
 * @author dalpuche
 *
 */
public class RemarkParser  implements JsonParser {

	private RemarkDAO remark;
	/**
	 * Constructor default
	 */
	public RemarkParser(RemarkDAO remark) {
		this.remark=remark;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.JsonParser#toJson()
	 */
	@Override
	public JsonObject getJson() {

		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (remark.getTitle() != null && !remark.getTitle().isEmpty())
			builder.add("title", remark.getTitle());
		builder.add("description", this.getDescriptionsJson());
		if (remark.getType() != null && !remark.getType().isEmpty())
			builder.add("type", remark.getType());
		if (remark.getLinks() != null && !remark.getLinks().isEmpty())
			builder.add("links", JsonUtil.getLinksJson(remark.getLinks()));
		return builder.build();
	}

	/**
	 * get the jsonArray of the remark's descriptions
	 * 
	 * @return
	 */
	private JsonArray getDescriptionsJson() {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (RemarkDescription description : remark.getDescriptions()) {
			arrayBuilder.add(description.getDescription());
		}
		return arrayBuilder.build();
	}

	public String toString() {
		return getJson().toString();
	}
}
