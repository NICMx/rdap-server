package mx.nic.rdap;

import javax.json.JsonObject;

/**
 * A response to the user.
 * 
 * @author aleiva
 */
public interface RdapResult {

	/**
	 * Builds a JSON object out of this {@link RdapResult}.
	 * 
	 * @return JSON version os this object.
	 */
	public JsonObject toJson();

}
