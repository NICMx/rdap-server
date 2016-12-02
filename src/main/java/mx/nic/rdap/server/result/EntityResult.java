package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.db.EntityDAO;
import mx.nic.rdap.db.LinkDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserRequestInfo;
import mx.nic.rdap.server.renderer.json.EntityParser;

/**
 * A result from an Entity request
 */
public class EntityResult extends UserRequestInfo implements RdapResult {

	private EntityDAO entity;

	public EntityResult(EntityDAO entity, String userName) {
		this.entity = entity;
		setUserName(userName);
		LinkDAO self = new LinkDAO("entity", entity.getHandle());
		entity.getLinks().add(self);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return EntityParser.getJson(entity, isUserAuthenticated(), isOwner(entity));
	}

}
