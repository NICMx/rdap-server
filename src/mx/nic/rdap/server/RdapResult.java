package mx.nic.rdap.server;

import javax.json.JsonObject;

/**
 * A response to the user.
 */
public interface RdapResult {

	/**
	 * Builds a JSON object out of this {@link RdapResult}.
	 * 
	 * @return JSON version os this object.
	 */
	public JsonObject toJson();

}
