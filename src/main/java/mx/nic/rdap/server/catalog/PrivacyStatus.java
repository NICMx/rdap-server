package mx.nic.rdap.server.catalog;

/**
 * Privacy levels of an object or attribute.
 */
public enum PrivacyStatus {
	OWNER, AUTHENTICATED, ANY, NONE;

	private PrivacyStatus() {
	}

}
