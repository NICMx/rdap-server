package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.NameserverDAO;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapSearchRequest;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.NameserverSearchResult;
import mx.nic.rdap.server.util.IpUtil;

@WebServlet(name = "nameservers", urlPatterns = { "/nameservers" })
public class NameserverSearchServlet extends DataAccessServlet<NameserverDAO> {
	private static final long serialVersionUID = 1L;
	private static final String IP_PARAMETER_KEY = "ip";
	private static final String NAME_PARAMETER_KEY = "name";

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
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException {
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

		SearchResultStruct<Nameserver> result = null;
		switch (searchRequest.getType()) {
		case PARTIAL_SEARCH:
			result = getPartialSearch(username, searchRequest);
			break;
		case REGEX_SEARCH:
			result = getRegexSearch(username, searchRequest);
			break;
		default:
			throw new RequestHandleException(501, "Not implemented.");
		}
		return new NameserverSearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result,
				username);
	}

	private SearchResultStruct<Nameserver> getPartialSearch(String username, RdapSearchRequest request)
			throws SQLException, IOException, RequestHandleException, RdapDataAccessException {
		SearchResultStruct<Nameserver> result = new SearchResultStruct<Nameserver>();
		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);

		switch (request.getParameterName()) {
		case NAME_PARAMETER_KEY:
			result = getDAO().searchByName(request.getParameterValue().trim(), resultLimit);
			break;
		case IP_PARAMETER_KEY:
			try {
				result = getDAO().searchByIp(request.getParameterValue().trim(), resultLimit);
			} catch (InvalidValueException e) {
				throw new RequestHandleException(e.getMessage());
			}
			break;
		}
		return result;
	}

	private SearchResultStruct<Nameserver> getRegexSearch(String username, RdapSearchRequest request)
			throws SQLException, IOException, RequestHandleException, RdapDataAccessException {
		SearchResultStruct<Nameserver> result = new SearchResultStruct<Nameserver>();
		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);

		switch (request.getParameterName()) {
		case NAME_PARAMETER_KEY:
			result = getDAO().searchByRegexName(request.getParameterValue().trim(), resultLimit);
			break;
		case IP_PARAMETER_KEY:
			result = getDAO().searchByRegexIp(request.getParameterValue(), resultLimit);
			break;
		}
		return result;
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
