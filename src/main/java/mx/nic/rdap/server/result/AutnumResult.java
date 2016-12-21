/**
 * 
 */
package mx.nic.rdap.server.result;

import java.io.FileNotFoundException;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.db.AutnumDAO;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.operational.profile.OperationalProfileValidator;
import mx.nic.rdap.server.renderer.json.AutnumJsonWriter;

public class AutnumResult extends RdapResult {

	private AutnumDAO autnum;

	public AutnumResult(String header, String contextPath, AutnumDAO autnum, String username)
			throws FileNotFoundException {
		this.autnum = autnum;
		this.userInfo = new UserInfo(username);
		this.autnum.addSelfLinks(header, contextPath);
		validateResponse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return AutnumJsonWriter.getJson(autnum, userInfo.isUserAuthenticated(), userInfo.isOwner(autnum));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#fillNotices()
	 */
	@Override
	public void fillNotices() {
		// At the moment, there is no notices for this request
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
		if (!RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE)) {
			if (autnum.getEntities() != null && !autnum.getEntities().isEmpty()) {
				for (Entity ent : autnum.getEntities()) {
					OperationalProfileValidator.validateEntityEvents(ent);
					OperationalProfileValidator.validateEntityTel(ent);
				}
			}
		}
	}

}
