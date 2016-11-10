package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.db.NameserverDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserRequestInfo;
import mx.nic.rdap.server.renderer.json.NameserverParser;

/**
 * A result from a Nameserver request
 */
public class NameserverResult extends UserRequestInfo implements RdapResult {

	private NameserverDAO nameserver;

	public NameserverResult(NameserverDAO nameserver, String userName) {
		this.nameserver = nameserver;
		setUserName(userName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return NameserverParser.getJson(nameserver, isUserAuthenticated(), isOwner(nameserver));
	}

}
