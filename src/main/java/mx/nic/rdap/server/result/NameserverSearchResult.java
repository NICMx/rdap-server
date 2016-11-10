package mx.nic.rdap.server.result;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserRequestInfo;
import mx.nic.rdap.server.renderer.json.NameserverParser;

/**
 * A result from a Nameserver search request
 */
public class NameserverSearchResult extends UserRequestInfo implements RdapResult {

	private List<Nameserver> nameservers;

	public NameserverSearchResult(List<Nameserver> nameservers, String userName) {
		this.nameservers = nameservers;
		setUserName(userName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (Nameserver nameserver : nameservers) {
			arrayBuilder.add(NameserverParser.getJson(nameserver, isUserAuthenticated(), isOwner(nameserver)));
		}
		builder.add("nameserverSearchResults", arrayBuilder.build());
		return builder.build();
	}

}
