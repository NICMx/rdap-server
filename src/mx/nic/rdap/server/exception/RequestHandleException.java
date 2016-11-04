package mx.nic.rdap.server.exception;

/**
 * Generic problems found handling RDAP requests.
 */
public class RequestHandleException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * The code we'll return in the HTTP response.
	 */
	private int httpResponseStatusCode = 500;

	public RequestHandleException(String message) {
		super(message);
	}

	public RequestHandleException(String message, Throwable cause) {
		super(message, cause);
	}

	public RequestHandleException(int httpResponseStatusCode, String message) {
		super(message);
		this.httpResponseStatusCode = httpResponseStatusCode;
	}

	public RequestHandleException(int httpResponseStatusCode, Throwable cause) {
		super(cause);
		this.httpResponseStatusCode = httpResponseStatusCode;
	}

	public RequestHandleException(int httpResponseStatusCode, String message, Throwable cause) {
		super(message, cause);
		this.httpResponseStatusCode = httpResponseStatusCode;
	}

	/**
	 * @see #httpResponseStatusCode
	 */
	public int getHttpResponseStatusCode() {
		return httpResponseStatusCode;
	}

}
