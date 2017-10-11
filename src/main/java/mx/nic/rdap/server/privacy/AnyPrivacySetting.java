package mx.nic.rdap.server.privacy;

/**
 * Privacy setting for configuration value "any"
 */
public class AnyPrivacySetting implements PrivacySetting {

	@Override
	public boolean isHidden(UserInfo userInfo) {
		return false;
	}

}
