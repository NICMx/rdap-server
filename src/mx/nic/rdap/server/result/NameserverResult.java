package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.db.NameserverDAO;

/**
 * A result from a Nameserver request
 * 
 * @author dalpuche
 *
 */
public class NameserverResult implements RdapResult {

	private Nameserver nameserver;

	public NameserverResult(Nameserver nameserver) {
		this.nameserver = nameserver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return ((NameserverDAO) nameserver).toJson();
	}

}
