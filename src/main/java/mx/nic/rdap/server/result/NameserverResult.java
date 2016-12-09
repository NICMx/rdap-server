package mx.nic.rdap.server.result;

import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.NameserverDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.renderer.json.NameserverParser;

/**
 * A result from a Nameserver request
 */
public class NameserverResult extends RdapResult {

	private NameserverDAO nameserver;

	public NameserverResult(String header, String contextPath, NameserverDAO nameserver, String userName) {
		notices = new ArrayList<Remark>();
		this.nameserver = nameserver;
		this.userInfo = new UserInfo(userName);
		this.nameserver.addSelfLinks(header, contextPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return NameserverParser.getJson(nameserver, userInfo.isUserAuthenticated(), userInfo.isOwner(nameserver));
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
