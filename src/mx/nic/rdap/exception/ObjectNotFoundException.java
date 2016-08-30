package mx.nic.rdap.exception;

/**
 * An error signaling that the object the user requested was not found in the
 * database.
 * 
 * @author aleiva
 */
public class ObjectNotFoundException extends RequestHandleException {

	private static final long serialVersionUID = 1L;

	public ObjectNotFoundException(String message) {
		super(404, message);
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(404, message, cause);
	}

}
