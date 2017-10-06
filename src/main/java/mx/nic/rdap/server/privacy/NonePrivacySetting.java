package mx.nic.rdap.server.privacy;

public class NonePrivacySetting implements PrivacySetting {

	@Override
	public boolean isHidden(Object userInfo) {
		return true;
	}

}
