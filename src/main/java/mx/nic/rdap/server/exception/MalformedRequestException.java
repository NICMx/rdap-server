package mx.nic.rdap.server.exception;

/**
 * An HTTP 400 error.
 * 
 * "The server cannot or will not process the request due to an apparent client
 * error (e.g., malformed request syntax, too large size, invalid request
 * message framing, or deceptive request routing)."
 * 
 * (Quoted from Wikipedia.)
 */
public class MalformedRequestException extends RequestHandleException {

	private static final long serialVersionUID = 1L;

	public MalformedRequestException(String message) {
		super(400, message);
	}

	public MalformedRequestException(Throwable cause) {
		super(400, cause);
	}

	public MalformedRequestException(String message, Throwable cause) {
		super(400, message, cause);
	}

}
