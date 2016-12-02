package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.model.DomainModel;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.DomainSearchResult;

@WebServlet(name = "domains", urlPatterns = { "/domains" })
public class DomainSearchServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public final String DOMAIN_NAME = "name";

	public final String NAMESERVER_NAME = "nsLdhName";

	public final String NAMESERVER_IP = "nsIp";

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
		DomainSearchRequest request;
		try {
			request = new DomainSearchRequest(httpRequest);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}

		List<DomainDAO> domainsDAO = null;
		String username = httpRequest.getRemoteUser();

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			Integer resultLimit = Util.getMaxNumberOfResultsForUser(username, connection);

			switch (request.getParameter()) {
			case DOMAIN_NAME:
				// Gets domain by its name with zone
				if (request.getValue().contains(".")) {
					String[] split = request.getValue().split("\\.", 2);
					String domain = split[0];
					String zone = split[1];

					try {
						domainsDAO = DomainModel.searchByName(domain, zone, resultLimit, connection);
					} catch (InvalidValueException e) {
						e.printStackTrace();
					}
				} else {
					// Search domain by it´s name without zone.
					domainsDAO = DomainModel.searchByName(request.getValue(), resultLimit, connection);
				}
				break;
			case NAMESERVER_NAME:
				// Gets´s domain by it´s Nameserver name
				domainsDAO = DomainModel.searchByNsLdhName(request.getValue(), resultLimit, connection);
				break;
			case NAMESERVER_IP:
				// Get´s domain by it´s Nameserver Ip
				domainsDAO = DomainModel.searchByNsIp(request.getValue(), resultLimit, connection);
				break;
			default:

				break;
			}

		}

		List<Domain> domains = null;
		if (domainsDAO != null)
			domains = new ArrayList<Domain>(domainsDAO);

		return new DomainSearchResult(domains, username);
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

		private String parameter;

		private String value;

		public DomainSearchRequest(HttpServletRequest httpRequest)
				throws UnprocessableEntityException, MalformedRequestException {
			super();
			Util.validateDomainNameSearchRequest(httpRequest, DOMAIN_NAME, NAMESERVER_NAME, NAMESERVER_IP);
			this.parameter = httpRequest.getParameterNames().nextElement();
			this.value = httpRequest.getParameter(parameter);
			if (this.parameter.equals(NAMESERVER_IP)) {
				Util.validateIpAddress(value);
			}

			if (this.value.endsWith(".")) {
				this.value = this.value.substring(0, this.value.length() - 1);
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
