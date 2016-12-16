package mx.nic.rdap.server.result;

import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.IpNetworkDAO;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.operational.profile.OperationalProfileValidator;
import mx.nic.rdap.server.renderer.json.IpNetworkJsonWriter;

public class IpResult extends RdapResult {

	private IpNetworkDAO ipNetwork;

	public IpResult(String header, String contextPath, IpNetworkDAO ipNetwork, String userName) {
		notices = new ArrayList<Remark>();
		this.ipNetwork = ipNetwork;
		this.userInfo = new UserInfo(userName);
		this.ipNetwork.addSelfLinks(header, contextPath);
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
	
	/* (non-Javadoc)
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
		if(!RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE)){
			if(ipNetwork.getEntities()!=null&&!ipNetwork.getEntities().isEmpty()){
				for(Entity ent:ipNetwork.getEntities()){
					OperationalProfileValidator.validateEntityEvents(ent);
				}
			}
		}
	}
}
