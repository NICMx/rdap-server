package mx.nic.rdap.server.result;

import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.configuration.RdapConfiguration;
import mx.nic.rdap.server.operational.profile.OperationalProfileValidator;
import mx.nic.rdap.server.renderer.json.IpNetworkJsonWriter;

public class IpResult extends RdapResult {

	private IpNetwork ipNetwork;

	public IpResult(String header, String contextPath, IpNetwork ipNetwork, String userName) {
		notices = new ArrayList<Remark>();
		this.ipNetwork = ipNetwork;
		this.userInfo = new UserInfo(userName);
		addSelfLinks(header, contextPath, ipNetwork);

	}

	@Override
	public JsonObject toJson() {
		return IpNetworkJsonWriter.getJson(ipNetwork, userInfo.isUserAuthenticated(), userInfo.isOwner(ipNetwork));
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
			if (ipNetwork.getEntities() != null && !ipNetwork.getEntities().isEmpty()) {
				for (Entity ent : ipNetwork.getEntities()) {
					OperationalProfileValidator.validateEntityEvents(ent);
					OperationalProfileValidator.validateEntityTel(ent);
				}
			}
		}
	}

	/**
	 * Generates a link with the self information and add it to the domain
	 * 
	 * @param ipNetwork
	 */
	private void addSelfLinks(String header, String contextPath, IpNetwork ipNetwork) {
		Link self = new Link(header, contextPath, "ip",
				ipNetwork.getStartAddress().getHostAddress() + "/" + ipNetwork.getPrefix());
		ipNetwork.getLinks().add(self);

		for (Entity ent : ipNetwork.getEntities()) {
			self = new Link(header, contextPath, "entity", ent.getHandle());
			ent.getLinks().add(self);
		}
	}

}
