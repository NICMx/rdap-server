package mx.nic.rdap.server.privacy;

/**
 * Interface to represent the allowed privacy settings
 */
public interface PrivacySetting {

	/**
	 * Indicates if the attribute could be displayed.
	 * 
	 * @param userInfo
	 *            to validate if the object will be displayed
	 * @return <code>true</code> if the value needs to be hidden, <code>false</code> if the value can be
	 *         displayed
	 */
	boolean isHidden(UserInfo userInfo);

}