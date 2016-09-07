package mx.nic.rdap.exception;

import java.sql.SQLException;

/**
 * An error signaling that the object the user requested was not found in the
 * database. Going to become an HTTP 404.
 * 
 * @author aleiva
 */
public class ObjectNotFoundException extends SQLException {

	private static final long serialVersionUID = 1L;

	public ObjectNotFoundException(String message) {
		super(message);
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
