package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.db.AutnumDAO;
import mx.nic.rdap.db.model.AutnumModel;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.AutnumResult;

@WebServlet(name = "autnum", urlPatterns = { "/autnum/*" })
public class AutnumServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public AutnumServlet() {
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
		AutnumRequest request = new AutnumRequest(Util.getRequestParams(httpRequest)[0]);
		AutnumDAO autnum = null;
		String username = httpRequest.getRemoteUser();

		try (Connection con = DatabaseSession.getRdapConnection()) {
			autnum = AutnumModel.getByRange(request.getAutnum(), con);
		}
		return new AutnumResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), autnum, username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapHead(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapHead(HttpServletRequest request)
			throws RequestHandleException, IOException, SQLException {
		throw new RequestHandleException(501, "Not implemented yet.");
	}

	private class AutnumRequest {

		private Long autnum;

		public AutnumRequest(String autnum) throws MalformedRequestException {
			super();
			try {
				this.autnum = Long.parseLong(autnum);
			} catch (NumberFormatException e) {
				throw new MalformedRequestException("Autnum must be an Int number");
			}
		}

		public Long getAutnum() {
			return autnum;
		}
	}

}
