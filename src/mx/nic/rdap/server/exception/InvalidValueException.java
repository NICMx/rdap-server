package mx.nic.rdap.server.exception;

/**
 * Generic problems with the validation of the objects
 * 
 * @author dalpuche
 *
 */
public class InvalidValueException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param attributeName
	 *            the attribute missing
	 * @param className
	 *            the class
	 */
	public InvalidValueException(String attributeName, String className, Object value) {
		super("Invalid value of " + className + "." + attributeName + ": " + value.toString());
	}

}
