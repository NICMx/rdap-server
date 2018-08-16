package mx.nic.rdap.server.privacy;

import java.util.HashMap;
import java.util.Map;

public class ObscuredPrivacy implements PrivacySetting {

	private String textToShow;

	private static Map<String, ObscuredPrivacy> pool;

	static {
		pool = new HashMap<>();
	}

	@Override
	public boolean isHidden(UserInfo userInfo) {
		return true;
	}

	private ObscuredPrivacy(String textToShow) {
		this.textToShow = textToShow;
		// no code, use create
	}

	public String getTextToShow() {
		return textToShow;
	}

	public void setTextToShow(String textToShow) {
		this.textToShow = textToShow;
	}

	@Override
	public int hashCode() {
		return textToShow.hashCode();
	}

	public static ObscuredPrivacy create(String textToShow) {
		ObscuredPrivacy obscuredPrivacy = pool.get(textToShow);
		if (obscuredPrivacy == null) {
			obscuredPrivacy = new ObscuredPrivacy(textToShow);
			pool.put(textToShow, obscuredPrivacy);
		}

		return obscuredPrivacy;
	}
}
