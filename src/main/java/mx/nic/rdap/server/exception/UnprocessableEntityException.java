package mx.nic.rdap.server.exception;

/**
 * Throws when a server receives a search request but cannot process the request
 * because it does not support a particular style of partial match searching
 */
public class UnprocessableEntityException extends HttpException {

	private static final long serialVersionUID = 1L;
	private static final int CODE = 422;
	private static final String DEFAULT_MSG = "Unprocessable Entity";

	public UnprocessableEntityException() {
		super(CODE, DEFAULT_MSG);
	}

	public UnprocessableEntityException(String message) {
		super(CODE, message);
	}

	public UnprocessableEntityException(Throwable cause) {
		super(CODE, DEFAULT_MSG, cause);
	}

	public UnprocessableEntityException(String message, Throwable cause) {
		super(CODE, message, cause);
	}

}
