/**
 * 
 */
package mx.nic.rdap.server.result;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserRequestInfo;
import mx.nic.rdap.server.renderer.json.DomainParser;

/**
 * A result from a Domain search request
 */
public class DomainSearchResult extends UserRequestInfo implements RdapResult {

	private List<DomainDAO> domains;

	public DomainSearchResult(String header, String contextPath, List<DomainDAO> domains, String userName) {
		this.domains = domains;
		setUserName(userName);
		for (DomainDAO domain : this.domains) {
			domain.addSelfLinks(header, contextPath);
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

		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Domain domain : domains) {
			arrB.add(DomainParser.getJson(domain, isUserAuthenticated(), isOwner(domain)));
		}

		builder.add("domainSearchResults", arrB);
		return builder.build();
	}

}
