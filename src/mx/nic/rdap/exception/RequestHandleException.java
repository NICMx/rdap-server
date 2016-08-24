package mx.nic.rdap.exception;

import mx.nic.rdap.RdapRequestHandler;

/**
 * Problems found during a {@link RdapRequestHandler#handle(mx.nic.rdap.RdapRequest)}.
 * 
 * @author aleiva
 */
public class RequestHandleException extends Exception {

	private static final long serialVersionUID = 1L;

	public RequestHandleException(String message) {
		super(message);
	}
	
	public RequestHandleException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
