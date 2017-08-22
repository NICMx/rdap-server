package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.server.result.ExceptionResult;
import mx.nic.rdap.server.result.RdapResult;

@WebServlet(name = "exception", urlPatterns = { "/exception" })
public class ExceptionServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest) {
		RdapResult result = new ExceptionResult(httpRequest);
		return result;
	}

}
