package mx.nic.rdap.server.db;

import java.net.IDN;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.core.db.Zone;
import mx.nic.rdap.server.renderer.json.JsonParser;
import mx.nic.rdap.server.renderer.json.JsonUtil;

/**
 * Data access class for the Domain object. The domain object class represents a
 * DNS name and point of delegation.
 * 
 * @author evaldes
 *
 */
public class DomainDAO extends Domain implements DatabaseObject, JsonParser {

	/**
	 * Default Constreuctor
	 */
	public DomainDAO() {
		super();
	}

	public DomainDAO(ResultSet resultSet) throws SQLException {
		loadFromDatabase(resultSet);
	}

	/**
	 * Loads the information coming from the database in an instance of Domain
	 * 
	 * @param resultSet
	 *            ResultSet from where all information is obtained
	 * @throws SQLException
	 *             If there is an error during ResultSet access
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getLong("dom_id"));
		this.setHandle(resultSet.getString("dom_handle"));
		this.setLdhName(resultSet.getString("dom_ldh_name"));
		this.setPort43(resultSet.getString("dom_port43"));
		this.setZone(new Zone());
		this.getZone().setId(resultSet.getInt("zone_id"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getHandle());
		preparedStatement.setString(2, this.getLdhName());
		preparedStatement.setString(3, this.getPort43());
		preparedStatement.setInt(4, this.getZone().getId());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.json.JsonParser#toJson()
	 */
	@Override
	public JsonObject toJson() {
		this.getLinks().add(new LinkDAO("domain", this.getLdhName()));

		JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("objectClassName", "domain");
		JsonUtil.getCommonRdapJsonObject(builder, this);
		builder.add("ldhName", this.getLdhName());
		builder.add("unicodeName", IDN.toUnicode(this.getLdhName()));
		if (this.getVariants() != null && !this.getVariants().isEmpty()) {
			builder.add("variants", this.getVariantsJson(this.getVariants()));
		}
		if (this.getPublicIds() != null && !this.getPublicIds().isEmpty()) {
			builder.add("publicIds", this.getPublicIdsJson(this.getPublicIds()));
		}

		if (this.getNameServers() != null && !this.getNameServers().isEmpty()) {
			builder.add("nameservers", this.getNameServersJson(this.getNameServers()));
		}
		getSecureDNSJson(builder, this.getSecureDNS());
		return builder.build();
	}

	private JsonArray getVariantsJson(List<Variant> variants) {
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Variant variant : variants) {
			JsonObject json = ((VariantDAO) variant).toJson();
			arrB.add(json);
		}
		return arrB.build();
	}

	private JsonArray getPublicIdsJson(List<PublicId> publicIds) {
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (PublicId publicId : publicIds) {
			JsonObject json = ((PublicIdDAO) publicId).toJson();
			arrB.add(json);
		}
		return arrB.build();
	}

	private JsonArray getNameServersJson(List<Nameserver> nameservers) {
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Nameserver ns : nameservers) {
			JsonObject json = ((NameserverDAO) ns).toJson();
			arrB.add(json);
		}
		return arrB.build();
	}

	private JsonObjectBuilder getSecureDNSJson(JsonObjectBuilder builder, SecureDNS secureDns) {
		if (secureDns != null) {
			builder.add("secureDNS", ((SecureDNSDAO) secureDns).toJson());
		}
		return builder;
	}
}
