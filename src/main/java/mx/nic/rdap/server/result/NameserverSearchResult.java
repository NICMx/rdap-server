package mx.nic.rdap.server.result;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.db.NameserverDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.renderer.json.NameserverParser;

/**
 * A result from a Nameserver search request
 */
public class NameserverSearchResult extends RdapResult {

	private List<NameserverDAO> nameservers;

	public NameserverSearchResult(String header, String contextPath, List<NameserverDAO> nameservers, String userName) {
		this.nameservers = nameservers;
		this.userInfo = new UserInfo(userName);
		for (NameserverDAO nameserver : nameservers) {
			nameserver.addSelfLinks(header, contextPath);
		}
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
			arrayBuilder.add(
					NameserverParser.getJson(nameserver, userInfo.isUserAuthenticated(), userInfo.isOwner(nameserver)));
		}
		builder.add("nameserverSearchResults", arrayBuilder.build());
		return builder.build();
	}

}
