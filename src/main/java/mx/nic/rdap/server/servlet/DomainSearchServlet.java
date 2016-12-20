package mx.nic.rdap.server.servlet;

import java.io.File;
import java.io.IOException;
import java.net.IDN;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.model.DomainModel;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.DomainSearchResult;
import mx.nic.rdap.server.result.OkResult;

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

		SearchResultStruct result = new SearchResultStruct();
		String username = httpRequest.getRemoteUser();

		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUnauthenticatedUser();// Default
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			resultLimit = Util.getMaxNumberOfResultsForUser(username, connection);
			String domain = request.getValue();
			if (IDN.toASCII(domain) != domain) {
				domain = IDN.toASCII(domain);
			}
			switch (request.getParameter()) {
			case DOMAIN_NAME:
				// Gets domain by its name with zone
				if (request.getValue().contains(".")) {
					String[] split = request.getValue().split("\\.", 2);
					domain = split[0];
					if (IDN.toASCII(domain) != domain) {
						domain = IDN.toASCII(domain);
					}
					String zone = split[1];

					try {
						result = DomainModel.searchByName(domain, zone, resultLimit, connection);
					} catch (InvalidValueException e) {
						e.printStackTrace();
					}
				} else {

					// Search domain by it´s name without zone.
					result = DomainModel.searchByName(domain, resultLimit, connection);
				}
				break;
			case NAMESERVER_NAME:
				// Gets´s domain by it´s Nameserver name
				result = DomainModel.searchByNsLdhName(domain, resultLimit, connection);
				break;
			case NAMESERVER_IP:
				// Get´s domain by it´s Nameserver Ip
				result = DomainModel.searchByNsIp(domain, resultLimit, connection);
				break;
			default:

				break;
			}

		}

		return new DomainSearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result, username,
				httpRequest.getServletContext().getRealPath(File.separator));
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
		DomainSearchRequest request;
		try {
			request = new DomainSearchRequest(httpRequest);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}
		String domain = request.getValue();
		if (IDN.toASCII(domain) != domain) {
			domain = IDN.toASCII(domain);
		}
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			switch (request.getParameter()) {
			case DOMAIN_NAME:
				// Gets domain by its name with zone
				if (request.getValue().contains(".")) {
					String[] split = request.getValue().split("\\.", 2);
					domain = split[0];
					domain = IDN.toASCII(domain);
					String zone = split[1];
					DomainModel.existByName(domain, zone, connection);
				} else {
					// Search domain by it´s name without zone.
					DomainModel.existByName(domain, connection);
				}
				break;
			case NAMESERVER_NAME:
				// Gets´s domain by it´s Nameserver name
				DomainModel.existByNsLdhName(domain, connection);
				break;
			case NAMESERVER_IP:
				// Get´s domain by it´s Nameserver Ip
				DomainModel.existByNsIp(domain, connection);
				break;
			default:
				break;
			}

		}

		return new OkResult();
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
