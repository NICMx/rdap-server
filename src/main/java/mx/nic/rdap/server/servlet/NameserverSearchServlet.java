package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.model.NameserverModel;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.NameserverSearchResult;
import mx.nic.rdap.server.result.OkResult;

@WebServlet(name = "nameservers", urlPatterns = { "/nameservers" })
public class NameserverSearchServlet extends RdapServlet {
	private static final long serialVersionUID = 1L;

	public NameserverSearchServlet() throws IOException {
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
		if (RdapConfiguration.useNameserverAsDomainAttribute()) {
			throw new RequestHandleException(501, "Not implemented.");
		}
		NameserverSearchRequest request;

		try {
			request = new NameserverSearchRequest(httpRequest);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}

		String username = httpRequest.getRemoteUser();
		SearchResultStruct result = new SearchResultStruct();

		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUnauthenticatedUser();// Default

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			resultLimit = Util.getMaxNumberOfResultsForUser(username, connection);
			if (request.getParameter().compareTo(NameserverSearchRequest.NAME_PARAMETER_KEY) == 0) {
				result = NameserverModel.searchByName(request.getValue().trim(), resultLimit, connection);
			} else {
				String ipAddress = request.getValue().trim();
				Util.validateIpAddress(ipAddress);
				try {
					result = NameserverModel.searchByIp(ipAddress, resultLimit, connection);
				} catch (InvalidValueException e) {
					throw new RequestHandleException(e.getMessage());
				}
			}
		}

		return new NameserverSearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result,
				username);
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
		if (RdapConfiguration.useNameserverAsDomainAttribute()) {
			throw new RequestHandleException(501, "Not implemented.");
		}
		NameserverSearchRequest request;

		try {
			request = new NameserverSearchRequest(httpRequest);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			if (request.getParameter().compareTo(NameserverSearchRequest.NAME_PARAMETER_KEY) == 0) {
				NameserverModel.existByName(request.getValue().trim(), connection);
			} else {
				String ipAddress = request.getValue().trim();
				Util.validateIpAddress(ipAddress);
				try {
					NameserverModel.existByIp(ipAddress, connection);
				} catch (InvalidValueException e) {
					throw new RequestHandleException(e.getMessage());
				}
			}
		}

		return new OkResult();
	}

	private class NameserverSearchRequest {

		public static final String IP_PARAMETER_KEY = "ip";
		public static final String NAME_PARAMETER_KEY = "name";
		private String parameter;
		private String value;

		public NameserverSearchRequest(HttpServletRequest httpRequest)
				throws UnprocessableEntityException, MalformedRequestException {
			super();
			Util.validateDomainNameSearchRequest(httpRequest, IP_PARAMETER_KEY, NAME_PARAMETER_KEY);
			this.parameter = httpRequest.getParameterNames().nextElement();
			this.value = httpRequest.getParameter(this.parameter);
			if (this.parameter.equals(IP_PARAMETER_KEY)) {
				Util.validateIpAddress(this.value);
			}
			if (this.value.endsWith(".")) {
				this.value = this.value.substring(0, this.value.length() - 1);
			}
		}

		/**
		 * @return the parameter
		 */
		public String getParameter() {
			return parameter;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

	}
}
