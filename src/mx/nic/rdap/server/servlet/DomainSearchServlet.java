package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.model.DomainModel;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.exception.UnprocessableEntityException;
import mx.nic.rdap.server.result.DomainSearchResult;

/**
 * Servlet that searches domains
 * 
 * @author evaldes
 * 
 */
@WebServlet(name = "domains", urlPatterns = { "/domains" })
public class DomainSearchServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public DomainSearchServlet() throws IOException {
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
		DomainSearchRequest request = new DomainSearchRequest(httpRequest);
		RdapResult result = null;

		try (Connection connection = DatabaseSession.getConnection()) {
			List<Domain> domains = new ArrayList<Domain>();

			if (request.getParameter() == "name") {
				if (request.getValue().contains("\\.")) {
					String domain = request.getValue().split("\\.", 2)[0];
					String zone = request.getValue().split("\\.", 2)[1];
					domains = DomainModel.searchByName(domain, zone, connection);
				} else {
					domains = DomainModel.searchByName(request.getValue(), connection);
				}
			}

			if (request.getParameter() == "nsLdhName") {
				domains = DomainModel.searchByNsLdhName(request.getValue(), connection);
			}

			if (request.getParameter() == "nsIp") {
				domains = DomainModel.searchByNsIp(request.getValue(), connection);
			}
			result = new DomainSearchResult(domains);
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

	private class DomainSearchRequest {

		public final String DOMAIN_NAME = "name";

		public final String NAMESERVER_NAME = "nsLdhName";

		public final String NAMESERVER_IP = "nsIp";

		private String parameter;

		private String value;

		public DomainSearchRequest(HttpServletRequest httpRequest)
				throws UnprocessableEntityException, MalformedRequestException {
			super();
			Util.validateSearchRequest(httpRequest, DOMAIN_NAME, NAMESERVER_NAME, NAMESERVER_IP);
			this.parameter = httpRequest.getParameterNames().nextElement();
			this.value = httpRequest.getParameter(parameter);
			if (this.parameter == NAMESERVER_IP) {
				Util.validateIpAddress(value);
			}
		}

		public String getParameter() {
			return parameter;
		}

		public String getValue() {
			return value;
		}

	}

}
