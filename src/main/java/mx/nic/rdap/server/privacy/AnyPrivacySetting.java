package mx.nic.rdap.server.privacy;

public class AnyPrivacySetting implements PrivacySetting {

	@Override
	public boolean isHidden(Object userInfo) {
		return false;
	}

}
