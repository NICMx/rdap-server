package mx.nic.rdap.server.renderer.json;

import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.db.LinkDAO;

/**
 * Parser for the LinkDAO object.
 * 
 * @author dalpuche
 *
 */
public class LinkParser  implements JsonParser {

	private LinkDAO link;

	/**
	 * Construct a LinkParser from a resulset
	 * 
	 * @throws SQLException
	 */
	public LinkParser(LinkDAO link)  {
		this.link=link;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.JsonParser#toJson()
	 */
	@Override
	public JsonObject getJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (link.getValue() != null && !link.getValue().isEmpty())
			builder.add("value", link.getValue());
		if (link.getRel() != null && !link.getRel().isEmpty())
			builder.add("rel", link.getRel());
		builder.add("href", link.getHref());
		if (link.getHreflag() != null && !link.getHref().isEmpty())
			builder.add("hreflang", link.getHreflag());
		if (link.getTitle() != null && !link.getTitle().isEmpty())
			builder.add("title", link.getTitle());
		if (link.getMedia() != null && !link.getMedia().isEmpty())
			builder.add("media", link.getMedia());
		if (link.getType() != null && !link.getType().isEmpty())
			builder.add("type", link.getType());
		return builder.build();
	}

	public String toString() {
		return getJson().toString();
	}

}
