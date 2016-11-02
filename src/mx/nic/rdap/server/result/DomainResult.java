package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.renderer.json.DomainParser;

/**
 * A result from a Domain request
 * 
 * @author dalpuche
 *
 */
public class DomainResult implements RdapResult {

	private DomainDAO domain;

	public DomainResult(DomainDAO domain) {
		this.domain = domain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return new DomainParser(domain).getJson();
	}

}
