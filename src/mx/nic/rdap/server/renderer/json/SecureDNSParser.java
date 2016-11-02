package mx.nic.rdap.server.renderer.json;

import java.sql.SQLException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.db.SecureDNSDAO;

/**
 * Parser access class for the SecureDNS object
 * 
 * @author evaldes
 *
 */
public class SecureDNSParser implements JsonParser {
	
 private SecureDNSDAO secureDns;

	/**
	 * Construct the object SecurDNS from a resultset
	 * 
	 * @throws SQLException
	 */
	public SecureDNSParser(SecureDNSDAO secureDns) {
	this.secureDns=secureDns;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.json.JsonParser#toJson()
	 */
	public JsonObject getJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		if (secureDns.getZoneSigned() != null) {
			builder.add("zoneSigned", secureDns.getZoneSigned());
		}
		if (secureDns.getDelegationSigned() != null) {
			builder.add("delegationSigned", secureDns.getZoneSigned());
		}
		if (secureDns.getMaxSigLife() != null) {
			builder.add("maxSigLife", secureDns.getZoneSigned());
		}

		if (secureDns.getDsData() != null && !secureDns.getDsData().isEmpty()) {
			builder.add("dsData", ((DsDataParser) secureDns.getDsData()).getJson());
		}
		return builder.build();
	}

}
