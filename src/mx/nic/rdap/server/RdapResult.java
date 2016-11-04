package mx.nic.rdap.server;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.RdapObject;

/**
 * A response to the user.
 */
public interface RdapResult {
	/**
	 * Builds a JSON object out of this {@link RdapResult}.
	 * 
	 * @return JSON version of this object.
	 */
	public JsonObject toJson();

}
