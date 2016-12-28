package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.net.UnknownHostException;
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
import mx.nic.rdap.server.RdapSearchRequest;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.NameserverSearchResult;
import mx.nic.rdap.server.result.OkResult;
import mx.nic.rdap.server.util.IpUtil;

@WebServlet(name = "nameservers", urlPatterns = { "/nameservers" })
public class NameserverSearchServlet extends RdapServlet {
	private static final long serialVersionUID = 1L;
	private static final String IP_PARAMETER_KEY = "ip";
	private static final String NAME_PARAMETER_KEY = "name";

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

		RdapSearchRequest searchRequest;
		try {
			searchRequest = RdapSearchRequest.getSearchRequest(httpRequest, false, IP_PARAMETER_KEY,
					NAME_PARAMETER_KEY);
			validateSearchRequest(searchRequest);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}

		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

		SearchResultStruct result = null;
		switch (searchRequest.getType()) {
		case PARTIAL_SEARCH:
			result = getPartialSearch(username, searchRequest);
			break;
		case REGEX_SEARCH:
			result = getRegexSearch();
			break;
		default:
			throw new RequestHandleException(501, "Not implemented.");
		}
		return new NameserverSearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result,
				username);
	}

	private SearchResultStruct getPartialSearch(String username, RdapSearchRequest request)
			throws SQLException, IOException, RequestHandleException {
		SearchResultStruct result = new SearchResultStruct();
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username, connection);

			switch (request.getParameterName()) {
			case NAME_PARAMETER_KEY:
				result = NameserverModel.searchByName(request.getParameterValue().trim(), resultLimit, connection);
				break;
			case IP_PARAMETER_KEY:
				try {
					result = NameserverModel.searchByIp(request.getParameterValue().trim(), resultLimit, connection);
				} catch (InvalidValueException e) {
					throw new RequestHandleException(e.getMessage());
				}
				break;
			}
		}
		return result;
	}

	private SearchResultStruct getRegexSearch() throws RequestHandleException {
		throw new RequestHandleException(501, "Not implemented.");
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

		RdapSearchRequest searchRequest;
		try {
			searchRequest = RdapSearchRequest.getSearchRequest(httpRequest, false, IP_PARAMETER_KEY,
					NAME_PARAMETER_KEY);
			validateSearchRequest(searchRequest);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}

		switch (searchRequest.getType()) {
		case PARTIAL_SEARCH:
			doRdapHeadPartialSearch(searchRequest);
			break;
		case REGEX_SEARCH:
			doRdapHeadRegexSearch();
			break;
		default:
			throw new RequestHandleException(501, "Not implemented.");
		}

		return new OkResult();
	}

	private void doRdapHeadPartialSearch(RdapSearchRequest request)
			throws SQLException, UnknownHostException, RequestHandleException {
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			switch (request.getParameterName()) {
			case NAME_PARAMETER_KEY:
				NameserverModel.existByName(request.getParameterValue().trim(), connection);
				break;
			case IP_PARAMETER_KEY:
				try {
					NameserverModel.existByIp(request.getParameterValue().trim(), connection);
				} catch (InvalidValueException e) {
					throw new RequestHandleException(e.getMessage());
				}
				break;
			}

		}
	}

	private void doRdapHeadRegexSearch() throws RequestHandleException {
		throw new RequestHandleException(501, "Not implemented yet.");
	}

	private static void validateSearchRequest(RdapSearchRequest searchRequest) throws MalformedRequestException {
		String parameter = searchRequest.getParameterName();
		String value = searchRequest.getParameterValue();

		if (parameter.equals(IP_PARAMETER_KEY)) {
			IpUtil.validateIpAddress(value);
		}

		if (value.endsWith(".")) {
			searchRequest.setParameterValue(value.substring(0, value.length() - 1));
		}
	}

}
