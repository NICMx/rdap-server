package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.NameserverResult;
import mx.nic.rdap.server.result.OkResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "nameserver", urlPatterns = { "/nameserver/*" })
public class NameserverServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public NameserverServlet() throws IOException {
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
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException {
		if (RdapConfiguration.useNameserverAsDomainAttribute()) {
			throw new RequestHandleException(501, "Not implemented.");
		}
		NameserverRequest request = new NameserverRequest(Util.getRequestParams(httpRequest)[0]);
		Nameserver nameserver = null;

		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

		nameserver = DataAccessService.getNameserverDAO().getByName(request.getName());
		return new NameserverResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), nameserver, username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapHead(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapHead(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException {
		if (RdapConfiguration.useNameserverAsDomainAttribute()) {
			throw new RequestHandleException(501, "Not implemented.");
		}
		NameserverRequest request = new NameserverRequest(Util.getRequestParams(httpRequest)[0]);
		DataAccessService.getNameserverDAO().existByName(request.getName());
		return new OkResult();
	}

	private class NameserverRequest {

		private String name;

		public NameserverRequest(String name) {
			super();
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

	}

}
