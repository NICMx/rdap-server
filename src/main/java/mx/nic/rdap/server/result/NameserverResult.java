package mx.nic.rdap.server.result;

import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.configuration.RdapConfiguration;
import mx.nic.rdap.server.operational.profile.OperationalProfileValidator;
import mx.nic.rdap.server.renderer.json.NameserverJsonWriter;

/**
 * A result from a Nameserver request
 */
public class NameserverResult extends RdapResult {

	private Nameserver nameserver;

	public NameserverResult(String header, String contextPath, Nameserver nameserver, String userName) {
		notices = new ArrayList<Remark>();
		this.nameserver = nameserver;
		this.userInfo = new UserInfo(userName);
		addSelfLinks(header, contextPath, nameserver);

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

	/**
	 * Generates a link with the self information and add it to the domain
	 * 
	 * @param nameserver
	 */
	public static void addSelfLinks(String header, String contextPath, Nameserver nameserver) {
		Link self = new Link(header, contextPath, "nameserver", nameserver.getLdhName());
		nameserver.getLinks().add(self);

		for (Entity ent : nameserver.getEntities()) {
			self = new Link(header, contextPath, "entity", ent.getHandle());
			ent.getLinks().add(self);
		}
	}
}
