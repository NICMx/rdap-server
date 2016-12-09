package mx.nic.rdap.server.result;

import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.EntityDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.renderer.json.EntityParser;

/**
 * A result from an Entity request
 */
public class EntityResult extends RdapResult {

	private EntityDAO entity;

	public EntityResult(String header, String contextPath, EntityDAO entity, String userName) {
		notices = new ArrayList<Remark>();
		this.entity = entity;
		this.userInfo = new UserInfo(userName);
		this.entity.addSelfLinks(header, contextPath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return EntityParser.getJson(entity, userInfo.isUserAuthenticated(), userInfo.isOwner(entity));
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
