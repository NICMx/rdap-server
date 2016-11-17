/**
 * 
 */
package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.db.AutnumDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserRequestInfo;
import mx.nic.rdap.server.renderer.json.AutnumParser;

public class AutnumResult extends UserRequestInfo implements RdapResult {

	private AutnumDAO autnum;

	public AutnumResult(AutnumDAO autnum, String username) {
		this.autnum = autnum;
		setUserName(username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return AutnumParser.getJson(autnum, isUserAuthenticated(), isOwner(autnum));
	}

}
