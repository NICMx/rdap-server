package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.NameserverDAO;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.NameserverResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "nameserver", urlPatterns = { "/nameserver/*" })
public class NameserverServlet extends DataAccessServlet<NameserverDAO> {

	private static final long serialVersionUID = 1L;

	@Override
	protected NameserverDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getNameserverDAO();
	}

	@Override
	protected String getServedObjectName() {
		return "nameservers";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, NameserverDAO dao)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException {
		if (RdapConfiguration.useNameserverAsDomainAttribute()) {
			throw new RequestHandleException(501, "Not implemented.");
		}
		NameserverRequest request = new NameserverRequest(Util.getRequestParams(httpRequest)[0]);

		Nameserver nameserver = dao.getByName(request.getName());
		if (nameserver == null) {
			return null;
		}

		return new NameserverResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), nameserver,
				Util.getUsername(httpRequest));
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
