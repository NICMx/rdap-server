package mx.nic.rdap.server.exception;

/**
 * Generic problems found while handling a client´s request.
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
