package mx.nic.rdap.server.privacy;

/**
 * Privacy setting for configuration value "authenticated"
 */
public class AuthenticatedPrivacySetting implements PrivacySetting {

	@Override
	public boolean isHidden(UserInfo userInfo) {
		return userInfo.getSubject() == null || !userInfo.getSubject().isAuthenticated();
	}

}
