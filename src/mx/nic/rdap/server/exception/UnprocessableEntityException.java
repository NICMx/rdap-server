package mx.nic.rdap.server.exception;

/**
 * Generic problems with the search request.
 * 
 * @author dalpuche
 *
 */
public class UnprocessableEntityException extends RequestHandleException {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param attributeName
	 *            the attribute missing
	 * @param className
	 *            the class
	 */
	public UnprocessableEntityException(String message) {
		super(422, message);
	}

	public UnprocessableEntityException() {
		super(422, "Unprocessable Entity");
	}
}
