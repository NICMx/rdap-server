package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.db.EntityDAO;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.renderer.json.EntityParser;

/**
 * A result from an Entity request
 * 
 * @author dhfelix
 *
 */
public class EntityResult implements RdapResult {

	private EntityDAO entity;

	public EntityResult(EntityDAO entity) {
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		EntityParser parser=new EntityParser(entity);
		return parser.getJson();
	}

}
