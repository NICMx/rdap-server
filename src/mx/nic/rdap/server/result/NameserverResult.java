package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.db.NameserverDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.renderer.json.NameserverParser;

/**
 * A result from a Nameserver request
 * 
 * @author dalpuche
 *
 */
public class NameserverResult implements RdapResult {

	private NameserverDAO nameserver;

	public NameserverResult(NameserverDAO nameserver) {
		this.nameserver = nameserver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		NameserverParser parser=new NameserverParser(nameserver);
		return parser.getJson();
	}

}
