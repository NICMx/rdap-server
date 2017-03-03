package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.ExceptionResult;

@WebServlet(name = "exception", urlPatterns = { "/exception" })
public class ExceptionServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public ExceptionServlet() throws IOException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {
		RdapResult result = new ExceptionResult(httpRequest);
		return result;
	}

}
