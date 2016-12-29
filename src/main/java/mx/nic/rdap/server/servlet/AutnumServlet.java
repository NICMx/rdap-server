package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.db.model.AutnumModel;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.AutnumResult;
import mx.nic.rdap.server.result.OkResult;
import mx.nic.rdap.server.util.Util;

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
		Autnum autnum = null;
		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

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
	protected RdapResult doRdapHead(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {
		AutnumRequest request = new AutnumRequest(Util.getRequestParams(httpRequest)[0]);
		try (Connection con = DatabaseSession.getRdapConnection()) {
			AutnumModel.existByRange(request.getAutnum(), con);
		}
		return new OkResult();
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
