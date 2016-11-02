/**
 * 
 */
package mx.nic.rdap.server.result;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.renderer.json.DomainParser;

/**
 * 
 * @author evaldes
 *
 */
public class DomainSearchResult implements RdapResult {

	private List<DomainDAO> domains;

	public DomainSearchResult(List<DomainDAO> domains) {
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
		for (DomainDAO domain : domains) {
			DomainParser parser=new DomainParser(domain);
			JsonObject json = parser.getJson();
			arrB.add(json);
		}
		builder.add("domainSearchResults", arrB);
		return builder.build();
	}

}
