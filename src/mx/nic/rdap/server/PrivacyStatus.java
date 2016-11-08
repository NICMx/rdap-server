package mx.nic.rdap.server;

/**
 * Privacy levels of an object or attribute.
 */
public enum PrivacyStatus {
	OWNER, AUTHENTICATE, ANY, NONE;

	private PrivacyStatus() {
	}

}
