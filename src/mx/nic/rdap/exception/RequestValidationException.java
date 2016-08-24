package mx.nic.rdap.exception;

/**
 * Problems while trying to make sense out of a user request.
 * 
 * @author aleiva
 */
public class RequestValidationException extends Exception {

	private static final long serialVersionUID = 1L;

	public RequestValidationException(String message) {
		super(message);
	}

	public RequestValidationException(Throwable cause) {
		super(cause);
	}

	public RequestValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}
