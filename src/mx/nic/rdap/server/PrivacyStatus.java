package mx.nic.rdap.server;

public enum PrivacyStatus {
	OWNER("owner"),
	AUTHENTICATE("authenticate"),
	ANY("any"),
	NONE("none");
	
	private String name;
	
	private PrivacyStatus(String name) {
		this.name = name;
	}
	
}
