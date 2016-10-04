package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.server.renderer.json.JsonParser;
import mx.nic.rdap.server.renderer.json.JsonUtil;

/**
 * Data access class for the DsData Object. The DsData is one of the
 * representations of the SecureDNS information that is not stored in the
 * registration database.
 * 
 * @author evaldes
 *
 */
public class DsDataDAO extends mx.nic.rdap.core.db.DsData implements DatabaseObject, JsonParser {

	/**
	 * Default constructor
	 */
	public DsDataDAO() {
		super();
	}

	/**
	 * Construct DsData from a ResultSet
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	public DsDataDAO(ResultSet resultSet) throws SQLException {
		super();
		loadFromDatabase(resultSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.server.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getLong("dsd_id"));
		this.setKeytag(resultSet.getInt("dsd_keytag"));
		this.setAlgorithm(resultSet.getInt("dsd_algorithm"));
		this.setDigest(resultSet.getString("dsd_digest"));
		this.setDigestType(resultSet.getInt("dsd_digest_type"));
		this.setSecureDNSId(resultSet.getLong("sdns_id"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setLong(1, this.getSecureDNSId());
		preparedStatement.setInt(2, this.getKeytag());
		preparedStatement.setInt(3, this.getAlgorithm());
		preparedStatement.setString(4, this.getDigest());
		preparedStatement.setInt(5, this.getDigestType());

	}

	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (this.getKeytag() != null) {
			builder.add("keyTag", this.getKeytag());
		}
		if (this.getAlgorithm() != null) {
			builder.add("algorithm", this.getAlgorithm());
		}
		if (this.getDigest() != null) {
			builder.add("digest", this.getDigest());
			if (this.getDigestType() != null) {
				builder.add("digestType", this.getDigestType());
			}
		}

		if (this.getEvents() != null && !this.getEvents().isEmpty()) {
			builder.add("events", JsonUtil.getEventsJson(this.getEvents()));
		}

		if (this.getLinks() != null && !this.getLinks().isEmpty()) {
			builder.add("links", JsonUtil.getLinksJson(this.getLinks()));
		}

		return builder.build();
	}

}
