package mx.nic.rdap;

import java.util.Arrays;

import mx.nic.rdap.exception.RequestValidationException;

/**
 * This is just a slightly parsed version of the user's request. You should
 * likely pay it no mind; you want to implement {@link RdapRequest} instead.
 * 
 * @author aleiva
 */
public class BareRequest {

	/** If the URI was /rdap/ip/192.0.2.0/24, then this is "ip". */
	private String resourceType;
	/** If the URI was /rdap/ip/192.0.2.0/24, then this is ["192.0.2.0", "24"]. */
	private String[] payload;

	public BareRequest(String uri) throws RequestValidationException {
		String[] labels = uri.split("/");
		if (labels.length < 4) {
			throw new RequestValidationException("I need more arguments than that. Try /rdap/sample/192.0.2.1");
		}

		this.resourceType = labels[2];
		this.payload = Arrays.copyOfRange(labels, 3, labels.length);
	}

	/**
	 * @see #resourceType
	 */
	public String getResourceType() {
		return resourceType;
	}

	/**
	 * @see #payload
	 */
	public String[] getPayload() {
		return payload;
	}

}
