package mx.nic.rdap.server.privacy;

/**
 * Privacy setting for configuration value "owner"
 */
public class OwnerPrivacySetting implements PrivacySetting {

	@Override
	public boolean isHidden(UserInfo userInfo) {
		return !userInfo.isObjectOwner();
	}

}
