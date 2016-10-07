package mx.nic.rdap.server.exception;

/**
 * Invalidad structure found in the migration
 * 
 * @author dalpuche
 *
 */
public class InvalidadDataStructure extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param attributeName
	 *            the attribute missing
	 * @param className
	 *            the class
	 */
	public InvalidadDataStructure(String dataStructureName, String expectedStructure) {
		super("Invalid data structure:" + dataStructureName + " Must have the form" + expectedStructure);
	}

	public InvalidadDataStructure() {
		super();
	}

}