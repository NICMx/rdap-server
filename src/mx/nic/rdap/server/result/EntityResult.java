package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.db.EntityDAO;

/**
 * A result from an Entity request
 * 
 * @author dhfelix
 *
 */
public class EntityResult implements RdapResult {

	private Entity entity;

	public EntityResult(Entity entity) {
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return ((EntityDAO) entity).toJson();
	}

}
