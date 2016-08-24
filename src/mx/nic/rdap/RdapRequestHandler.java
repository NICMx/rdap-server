package mx.nic.rdap;

import mx.nic.rdap.exception.RequestHandleException;
import mx.nic.rdap.exception.RequestValidationException;

public interface RdapRequestHandler {

	/**
	 * Returns the "Resource Type Path Segment" label this query is supposed to
	 * handle.
	 * 
	 * For example, if this handler is supposed to take care of queries in the
	 * form "/rdap/domain/<domain name>", then this function must return
	 * "domain".
	 * 
	 * @return the "Resource Type Path Segment" label this query is supposed to
	 *         handle.
	 */
	public String getResourceType();

	/**
	 * Validates the request of the user, parses the arguments and creates a
	 * more handler-friendly {@link RdapRequest} out of the `query` parameters.
	 * 
	 * @param query
	 *            Arguments to the request. For example, if the request was
	 *            "/rdap/ip/192.0.2.0/24", then "query" is ["192.0.2.0", "24"].
	 *            This array can neither be null nor zero-sized presently, but
	 *            maybe this will change after paying more attention to the
	 *            RFCs.
	 * @return Parsed version of `query`.
	 * @throws RequestValidationException
	 *             Errors found parsing `query`.
	 */
	public RdapRequest validate(String[] query) throws RequestValidationException;

	/**
	 * Handles the `request` request and builds a response.
	 * 
	 * @param request
	 *            result from {@link #validate(String[])}.
	 * @return response to the user.
	 * @throws RequestHandleException
	 *             Errors found handling `request`.
	 */
	public RdapResult handle(RdapRequest request) throws RequestHandleException;

}
