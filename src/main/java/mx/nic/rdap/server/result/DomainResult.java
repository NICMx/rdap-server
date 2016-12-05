package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserRequestInfo;
import mx.nic.rdap.server.renderer.json.DomainParser;

/**
 * A result from a Domain request
 */
public class DomainResult extends UserRequestInfo implements RdapResult {

	private DomainDAO domain;

	public DomainResult(String header, String contextPath, DomainDAO domain, String userName) {
		this.domain = domain;
		setUserName(userName);
		this.domain.addSelfLinks(header, contextPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {

		return DomainParser.getJson(domain, isUserAuthenticated(), isOwner(domain));
	}

}
