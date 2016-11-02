package mx.nic.rdap.server.renderer.json;

import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.db.DsDataDAO;

/**
 *Parser for the DsDataDAO Object. 
 * 
 * @author evaldes
 *
 */
public class DsDataParser  implements  JsonParser {
 
	 private DsDataDAO dsData;

	/**
	 * Construct DsData from a ResultSet
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	public DsDataParser( DsDataDAO dsData) {
		this.dsData=dsData;
	}

	public JsonObject getJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (dsData.getKeytag() != null) {
			builder.add("keyTag", dsData.getKeytag());
		}
		if (dsData.getAlgorithm() != null) {
			builder.add("algorithm", dsData.getAlgorithm());
		}
		if (dsData.getDigest() != null) {
			builder.add("digest", dsData.getDigest());
			if (dsData.getDigestType() != null) {
				builder.add("digestType", dsData.getDigestType());
			}
		}

		if (dsData.getEvents() != null && !dsData.getEvents().isEmpty()) {
			builder.add("events", JsonUtil.getEventsJson(dsData.getEvents()));
		}

		if (dsData.getLinks() != null && !dsData.getLinks().isEmpty()) {
			builder.add("links", JsonUtil.getLinksJson(dsData.getLinks()));
		}

		return builder.build();
	}

}
