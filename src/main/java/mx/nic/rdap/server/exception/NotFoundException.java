package mx.nic.rdap.server.exception;

/**
 * An HTTP 404 error.
 * 
 * "The requested resource could not be found but may be available in the
 * future. Subsequent requests by the client are permissible."
 * 
 * (Quoted from Wikipedia.)
 */
public class NotFoundException extends HttpException {

	private static final long serialVersionUID = 1L;
	private static final int CODE = 404;
	private static final String DEFAULT_MSG = "Not Found";

	public NotFoundException() {
		super(CODE, DEFAULT_MSG);
	}

	public NotFoundException(String message) {
		super(CODE, message);
	}

	public NotFoundException(Throwable cause) {
		super(CODE, DEFAULT_MSG, cause);
	}

	public NotFoundException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

}
