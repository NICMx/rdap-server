package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.DomainLabel;
import mx.nic.rdap.core.db.DomainLabelException;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.ip.IpAddressFormatException;
import mx.nic.rdap.core.ip.IpUtils;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.exception.http.NotImplementedException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.NameserverDAO;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapSearchRequest;
import mx.nic.rdap.server.result.NameserverSearchResult;

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
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, NameserverDAO dao)
			throws HttpException, RdapDataAccessException {
		RdapSearchRequest searchRequest = RdapSearchRequest.getSearchRequest(httpRequest, false, IP_PARAMETER_KEY,
				NAME_PARAMETER_KEY);
		validateSearchRequest(searchRequest);

		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

		SearchResultStruct<Nameserver> result = null;
		switch (searchRequest.getType()) {
		case PARTIAL_SEARCH:
			result = getPartialSearch(username, searchRequest, dao);
			break;
		case REGEX_SEARCH:
			result = getRegexSearch(username, searchRequest, dao);
			break;
		default:
			throw new NotImplementedException();
		}

		if (result == null) {
			return null;
		}

		return new NameserverSearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result,
				username);
	}

	private SearchResultStruct<Nameserver> getPartialSearch(String username, RdapSearchRequest request,
			NameserverDAO dao) throws RdapDataAccessException {
		SearchResultStruct<Nameserver> result = new SearchResultStruct<Nameserver>();
		int resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);

		switch (request.getParameterName()) {
		case NAME_PARAMETER_KEY:
			DomainLabel label;
			try {
				label = new DomainLabel(request.getParameterValue());
			} catch (DomainLabelException e) {
				throw new BadRequestException(e);
			}
			result = dao.searchByName(label, resultLimit);
			break;
		case IP_PARAMETER_KEY:
			result = dao.searchByIp(request.getParameterValue().trim(), resultLimit);
			break;
		}

		if (result != null) {
			result.truncate(resultLimit);
		}

		return result;
	}

	private SearchResultStruct<Nameserver> getRegexSearch(String username, RdapSearchRequest request, NameserverDAO dao)
			throws RdapDataAccessException {
		SearchResultStruct<Nameserver> result = new SearchResultStruct<Nameserver>();
		int resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);

		switch (request.getParameterName()) {
		case NAME_PARAMETER_KEY:
			result = dao.searchByRegexName(request.getParameterValue().trim(), resultLimit);
			break;
		case IP_PARAMETER_KEY:
			result = dao.searchByRegexIp(request.getParameterValue(), resultLimit);
			break;
		}

		if (result != null) {
			result.truncate(resultLimit);
		}

		return result;
	}

	private static void validateSearchRequest(RdapSearchRequest searchRequest) throws BadRequestException {
		String parameter = searchRequest.getParameterName();
		String value = searchRequest.getParameterValue();

		if (parameter.equals(IP_PARAMETER_KEY)) {
			try {
				IpUtils.parseAddress(value);
			} catch (IpAddressFormatException e) {
				throw new BadRequestException(e);
			}
		}

		if (value.endsWith(".")) {
			searchRequest.setParameterValue(value.substring(0, value.length() - 1));
		}
	}

}
