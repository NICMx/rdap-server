package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.db.DomainDAO;

/**
 * A result from a Domain request
 * 
 * @author dalpuche
 *
 */
public class DomainResult implements RdapResult {

	private Domain domain;

	public DomainResult(Domain domain) {
		this.domain = domain;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return ((DomainDAO) domain).toJson();
	}

}
