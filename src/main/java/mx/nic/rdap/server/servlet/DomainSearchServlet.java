package mx.nic.rdap.server.servlet;

import java.net.IDN;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.IpUtils;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.exception.http.NotImplementedException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.DomainDAO;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapSearchRequest;
import mx.nic.rdap.server.result.DomainSearchResult;

@WebServlet(name = "domains", urlPatterns = { "/domains" })
public class DomainSearchServlet extends DataAccessServlet<DomainDAO> {

	private static final long serialVersionUID = 1L;

	public final static String DOMAIN_NAME = "name";

	public final static String NAMESERVER_NAME = "nsLdhName";

	public final static String NAMESERVER_IP = "nsIp";

	@Override
	protected DomainDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getDomainDAO();
	}

	@Override
	protected String getServedObjectName() {
		return "domains";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, DomainDAO dao)
			throws HttpException, RdapDataAccessException {
		RdapSearchRequest searchRequest = RdapSearchRequest.getSearchRequest(httpRequest, false, DOMAIN_NAME,
				NAMESERVER_IP, NAMESERVER_NAME);
		validateSearchRequest(searchRequest);

		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

		SearchResultStruct<Domain> result = null;
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

		return new DomainSearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result, username);
	}

	private SearchResultStruct<Domain> getPartialSearch(String username, RdapSearchRequest request, DomainDAO dao)
			throws HttpException, RdapDataAccessException {
		SearchResultStruct<Domain> result = new SearchResultStruct<Domain>();
		int resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);

		String domain = request.getParameterValue();
		if (IDN.toASCII(domain) != domain) {
			domain = IDN.toASCII(domain);
		}
		switch (request.getParameterName()) {
		case DOMAIN_NAME:
			if (!RdapConfiguration.hasZoneConfigured()) {
				// Is valid if there are no available zones, because the
				// rdap could only respond to autnum and ip networks
				// (RIR).
				throw new NotImplementedException();
			}

			result = dao.searchByName(domain, resultLimit);
			break;
		case NAMESERVER_NAME:
			// Gets´s domain by it´s Nameserver name
			result = dao.searchByNsName(domain, resultLimit);
			break;
		case NAMESERVER_IP:
			// Get´s domain by it´s Nameserver Ip
			result = dao.searchByNsIp(domain, resultLimit);
			break;
		default:
			throw new NotImplementedException();
		}

		if (result != null) {
			result.truncate(resultLimit);
		}

		return result;
	}

	private SearchResultStruct<Domain> getRegexSearch(String username, RdapSearchRequest request, DomainDAO dao)
			throws HttpException, RdapDataAccessException {
		SearchResultStruct<Domain> result = new SearchResultStruct<Domain>();
		int resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);

		String domain = request.getParameterValue();
		switch (request.getParameterName()) {
		case DOMAIN_NAME:
			if (!RdapConfiguration.hasZoneConfigured()) {
				// Is valid if there are no available zones, because the
				// rdap could only respond to autnum and ip networks
				// (RIR).
				throw new NotImplementedException();
			}
			result = dao.searchByRegexName(domain, resultLimit);
			break;
		case NAMESERVER_NAME:
			// Gets´s domain by it´s Nameserver name
			result = dao.searchByRegexNsName(domain, resultLimit);
			break;
		case NAMESERVER_IP:
			result = dao.searchByRegexNsIp(domain, resultLimit);
			break;
		default:
			throw new NotImplementedException();
		}

		if (result != null) {
			result.truncate(resultLimit);
		}

		return result;
	}

	private static void validateSearchRequest(RdapSearchRequest searchRequest) throws BadRequestException {
		String parameter = searchRequest.getParameterName();
		String value = searchRequest.getParameterValue();

		if (parameter.equals(NAMESERVER_IP)) {
			IpUtils.validateIpAddress(value);
		}

		if (value.endsWith("\\.")) {
			value = value.substring(0, value.length() - 2);
			searchRequest.setParameterValue(value);
		}

		if (value.endsWith(".")) {
			searchRequest.setParameterValue(value.substring(0, value.length() - 1));
		}
	}

}
