package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.db.IpNetworkDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserRequestInfo;
import mx.nic.rdap.server.renderer.json.IpNetworkParser;

public class IpResult extends UserRequestInfo implements RdapResult {

	private IpNetworkDAO ipNetwork;

	public IpResult(String header, String contextPath, IpNetworkDAO ipNetwork, String userName) {
		this.ipNetwork = ipNetwork;
		setUserName(userName);
		this.ipNetwork.addSelfLinks(header, contextPath);
	}

	@Override
	public JsonObject toJson() {
		return IpNetworkParser.getJson(ipNetwork, isUserAuthenticated(), isOwner(ipNetwork));
	}
}
