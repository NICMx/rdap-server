package mx.nic.rdap.server.result;

import java.util.ArrayList;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.renderer.object.RequestResponse;

public class IpResult extends RdapSingleResult {

	public IpResult(String header, String contextPath, IpNetwork ipNetwork, String userName) {
		setRdapObject(ipNetwork);
		
		addSelfLinks(header, contextPath, ipNetwork);
		
		setResultType(ResultType.IP);
		RequestResponse<IpNetwork> ipNetworkResponse = new RequestResponse<>();
		ipNetworkResponse.setNotices(notices);
		ipNetworkResponse.setRdapConformance(new ArrayList<>());
		ipNetworkResponse.getRdapConformance().add("rdap_level_0");
		ipNetworkResponse.setRdapObject(ipNetwork);
		
		setRdapResponse(ipNetworkResponse);
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
		// Nothing to validate
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
