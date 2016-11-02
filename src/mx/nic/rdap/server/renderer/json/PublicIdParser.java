package mx.nic.rdap.server.renderer.json;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.db.PublicIdDAO;

/**
 * Parser for the PublicIdDAO object. 
 * 
 * @author evaldes
 *
 */
public class PublicIdParser  implements JsonParser {

	private PublicIdDAO publicId;
	/**
	 * Default Constructor
	 */
	public PublicIdParser(PublicIdDAO publicId) {
		this.publicId=publicId;

	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.json.JsonParser#toJson()
	 */
	@Override
	public JsonObject getJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		if (publicId.getType() != null && publicId.getType().isEmpty()) {
			builder.add("type", publicId.getType());
		}
		if (publicId.getType() != null && publicId.getPublicId().isEmpty()) {
			builder.add("identifier", publicId.getPublicId());
		}
		return builder.build();
	}

}
