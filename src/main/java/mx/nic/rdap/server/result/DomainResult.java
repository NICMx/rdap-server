package mx.nic.rdap.server.result;

import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.renderer.json.DomainParser;

/**
 * A result from a Domain request
 */
public class DomainResult extends RdapResult {

	private DomainDAO domain;

	public DomainResult(String header, String contextPath, DomainDAO domain, String userName) {
		notices = new ArrayList<Remark>();
		this.domain = domain;
		this.userInfo = new UserInfo(userName);
		this.domain.addSelfLinks(header, contextPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {

		return DomainParser.getJson(domain, userInfo.isUserAuthenticated(), userInfo.isOwner(domain));
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

}
