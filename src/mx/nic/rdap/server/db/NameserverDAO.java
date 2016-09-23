package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.IpAddress;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.server.renderer.json.JsonParser;
import mx.nic.rdap.server.renderer.json.JsonUtil;

/**
 * DAO for the Nameserver object.The nameserver object class represents
 * information regarding DNS nameservers used in both forward and reverse DNS
 * 
 * @author dalpuche
 *
 */
public class NameserverDAO extends Nameserver implements DatabaseObject, JsonParser {

	/**
	 * Constructor default
	 */
	public NameserverDAO() {
		super();
	}

	/**
	 * Contruct a NameserverDAO from a resulset
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	public NameserverDAO(ResultSet resultSet) throws SQLException {
		super();
		loadFromDatabase(resultSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		if (resultSet.wasNull())
			return;
		this.setId(resultSet.getLong("nse_id"));
		this.setHandle(resultSet.getString("nse_handle"));
		this.setPunycodeName(resultSet.getString("nse_ldh_name"));
		this.setPort43(resultSet.getString("nse_port43"));
		this.setRarId(resultSet.getLong("rar_id"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getHandle());
		preparedStatement.setString(2, this.getLdhName());
		preparedStatement.setString(3, this.getPort43());
		preparedStatement.setLong(4, this.getRarId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.JsonParser#toJson()
	 */
	@Override
	public JsonObject toJson() {
		this.getLinks().add(new LinkDAO("nameserver", this.getLdhName()));// Add
																			// the
																			// self
																			// link

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("objectClassName", "nameserver");
		JsonUtil.getCommonRdapJsonObject(builder, this);// Get the common
														// JsonObject of the
														// rdap objects
		builder.add("ldhName", this.getLdhName());
		if ((this.getIpAddresses().getIpv4Adresses() != null || this.getIpAddresses().getIpv6Adresses() != null)
				&& (!this.getIpAddresses().getIpv6Adresses().isEmpty()
						|| !this.getIpAddresses().getIpv4Adresses().isEmpty()))
			builder.add("ipAddresses", this.getIpAddressesJson());
		return builder.build();
	}

	/**
	 * Get the json object of the Nameserver's ipaddresses
	 * 
	 * @return
	 */
	public JsonObject getIpAddressesJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (this.getIpAddresses().getIpv4Adresses() != null && !this.getIpAddresses().getIpv4Adresses().isEmpty())
			builder.add("v4", this.getAddressesJsonArray(this.getIpAddresses().getIpv4Adresses()));
		if (this.getIpAddresses().getIpv6Adresses() != null && !this.getIpAddresses().getIpv6Adresses().isEmpty())
			builder.add("v6", this.getAddressesJsonArray(this.getIpAddresses().getIpv6Adresses()));
		return builder.build();
	}

	/**
	 * Get the jsonArray of an Addresses list
	 * 
	 * @param addresses
	 * @return
	 */
	private JsonArray getAddressesJsonArray(List<IpAddress> addresses) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (IpAddress address : addresses) {
			builder.add(address.getAddress().getHostAddress());
		}
		return builder.build();
	}

	public String toString() {
		return toJson().toString();
	}

}
