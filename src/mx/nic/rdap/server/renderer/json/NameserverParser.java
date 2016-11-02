package mx.nic.rdap.server.renderer.json;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.IpAddress;
import mx.nic.rdap.db.LinkDAO;
import mx.nic.rdap.db.NameserverDAO;

/**
 * Parser for the Nameserver object.
 * 
 * @author dalpuche
 *
 */
public class NameserverParser   implements JsonParser {

	private NameserverDAO nameserver;
	/**
	 * Constructor default
	 */
	public NameserverParser(NameserverDAO nameserver) {
		this.nameserver=nameserver;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.JsonParser#toJson()
	 */
	@Override
	public JsonObject getJson() {
		// Add the self link
		nameserver.getLinks().add(new LinkDAO("nameserver", nameserver.getLdhName()));

		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("objectClassName", "nameserver");
		// Get the common JsonObject of the  rdap objects
		JsonUtil.getCommonRdapJsonObject(builder, nameserver);
		builder.add("ldhName", nameserver.getLdhName());
		builder.add("unicodeName", nameserver.getUnicodeName());
		if ((nameserver.getIpAddresses().getIpv4Adresses() != null || nameserver.getIpAddresses().getIpv6Adresses() != null)
				&& (!nameserver.getIpAddresses().getIpv6Adresses().isEmpty()
						|| !nameserver.getIpAddresses().getIpv4Adresses().isEmpty()))
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
		if (nameserver.getIpAddresses().getIpv4Adresses() != null && !nameserver.getIpAddresses().getIpv4Adresses().isEmpty())
			builder.add("v4", this.getAddressesJsonArray(nameserver.getIpAddresses().getIpv4Adresses()));
		if (nameserver.getIpAddresses().getIpv6Adresses() != null && !nameserver.getIpAddresses().getIpv6Adresses().isEmpty())
			builder.add("v6", this.getAddressesJsonArray(nameserver.getIpAddresses().getIpv6Adresses()));
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
		return getJson().toString();
	}

}
