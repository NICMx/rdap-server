package mx.nic.rdap.server.privacy;

public interface PrivacySetting {

	/**
	 * Indicates if the attribute could be displayed.
	 * 
	 * @param userInfo
	 *            to validate if the object
	 * @return true if the value needs to be hidden, false if the value can be
	 *         displayed
	 */
	boolean isHidden(Object userInfo);

}
