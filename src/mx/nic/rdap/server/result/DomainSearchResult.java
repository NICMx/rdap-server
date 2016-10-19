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
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.db.DomainDAO;

/**
 * 
 * @author evaldes
 *
 */
public class DomainSearchResult implements RdapResult {

	private List<Domain> domains;

	public DomainSearchResult(List<Domain> domains) {
		this.domains = domains;
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
			JsonObject json = ((DomainDAO) domain).toJson();
			arrB.add(json);
		}
		builder.add("domainSearchResults", arrB);
		return builder.build();
	}

}
