package mx.nic.rdap.server;

import java.util.List;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Remark;

/**
 * A response to the user.
 */
public abstract class RdapResult {

	protected UserInfo userInfo;
	protected List<Remark> notices;

	/**
	 * Builds a JSON object out of this {@link RdapResult}.
	 * 
	 * @return JSON version of this object.
	 */
	public abstract JsonObject toJson();
	
	/**
	 * Some validations for the responses
	 */
	public abstract void validateResponse();

	/**
	 * Fills the notices of the request
	 * 
	 */
	public abstract void fillNotices();

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public List<Remark> getNotices() {
		return notices;
	}

	public void setNotices(List<Remark> notices) {
		this.notices = notices;
	}

}
