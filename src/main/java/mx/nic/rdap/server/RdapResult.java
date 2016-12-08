package mx.nic.rdap.server;

import javax.json.JsonObject;

/**
 * A response to the user.
 */
public abstract class RdapResult {

	protected UserInfo userInfo;

	/**
	 * Builds a JSON object out of this {@link RdapResult}.
	 * 
	 * @return JSON version of this object.
	 */
	public abstract JsonObject toJson();

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

}
