package mx.nic.rdap.server.result;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.NameserverDAO;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.operational.profile.OperationalProfileValidator;
import mx.nic.rdap.server.renderer.json.NameserverJsonWriter;

/**
 * A result from a Nameserver request
 */
public class NameserverResult extends RdapResult {

	private NameserverDAO nameserver;

	public NameserverResult(String header, String contextPath, NameserverDAO nameserver, String userName)
			throws FileNotFoundException {
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
		return NameserverJsonWriter.getJson(nameserver, userInfo.isUserAuthenticated(), userInfo.isOwner(nameserver));
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
			if (nameserver.getEntities() != null && !nameserver.getEntities().isEmpty()) {
				for (Entity ent : nameserver.getEntities()) {
					OperationalProfileValidator.validateEntityEvents(ent);
					OperationalProfileValidator.validateEntityTel(ent);
				}
			}
		}
		if (RdapConfiguration.getServerProfile().equals(OperationalProfile.REGISTRY)) {
			OperationalProfileValidator.validateNameserverName(nameserver);
		}
	}
}
