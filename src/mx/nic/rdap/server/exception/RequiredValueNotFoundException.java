package mx.nic.rdap.server.exception;

/**
 * Generic problems with the validation of the objects
 * 
 * @author dalpuche
 *
 */
public class RequiredValueNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param attributeName
	 *            the attribute missing
	 * @param className
	 *            the class
	 */
	public RequiredValueNotFoundException(String attributeName, String className) {
		super("Missing required value: " + attributeName + " in Class: " + className);
	}

}
