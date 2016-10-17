package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.model.DomainModel;
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.DomainResult;

/**
 * Servlet that find a domain by its name
 * 
 * @author dalpuche
 *
 */
@WebServlet(name = "domain", urlPatterns = { "/domain/*" })
public class DomainServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public DomainServlet() throws IOException {
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
		DomainRequest request = new DomainRequest(Util.getRequestParams(httpRequest)[0]);

		RdapResult result = null;
		try (Connection con = DatabaseSession.getConnection();) {
			Domain domain;
			try {
				DomainModel.validateDomainZone(request.getName());
				domain = DomainModel.findByLdhName(request.getName(), con);
			} catch (InvalidValueException | SQLException e) {
				throw new MalformedRequestException(e);
			}
			result = new DomainResult(domain);

		}
		return result;
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

	private class DomainRequest {

		private String name;

		public DomainRequest(String name) {
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