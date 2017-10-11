package mx.nic.rdap.server.privacy;

/**
 * Privacy setting for configuration value "none"
 */
public class NonePrivacySetting implements PrivacySetting {

	@Override
	public boolean isHidden(UserInfo userInfo) {
		return true;
	}

}
