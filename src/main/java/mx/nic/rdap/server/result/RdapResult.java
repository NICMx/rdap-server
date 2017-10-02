package mx.nic.rdap.server.result;

import java.util.List;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.renderer.object.RdapResponse;

/**
 * A response to the user.
 */
public abstract class RdapResult {

	private ResultType resultType;
	protected UserInfo userInfo;
	protected List<Remark> notices;
	
	private RdapResponse rdapResponse;

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

	public ResultType getResultType() {
		return resultType;
	}
	
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}
	
	public void setRdapResponse(RdapResponse rdapResponse) {
		this.rdapResponse = rdapResponse;
	}
	
	public RdapResponse getRdapResponse() {
		return rdapResponse;
	}
}
