package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.net.IDN;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.DomainDAO;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapSearchRequest;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.DomainSearchResult;
import mx.nic.rdap.server.util.IpUtil;

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
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException {
		RdapSearchRequest searchRequest;
		try {
			searchRequest = RdapSearchRequest.getSearchRequest(httpRequest, false, DOMAIN_NAME, NAMESERVER_IP,
					NAMESERVER_NAME);
			validateSearchRequest(searchRequest);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}

		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

		SearchResultStruct<Domain> result = null;
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

		return new DomainSearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result, username);
	}

	private SearchResultStruct<Domain> getPartialSearch(String username, RdapSearchRequest request)
			throws RequestHandleException, SQLException, IOException, RdapDataAccessException {
		SearchResultStruct<Domain> result = new SearchResultStruct<Domain>();
		boolean useNameserverAsAttribute = RdapConfiguration.useNameserverAsDomainAttribute();
		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);

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
				throw new RequestHandleException(501, "Not implemented yet.");
			}

			result = getDAO().searchByName(domain, resultLimit, useNameserverAsAttribute);
			break;
		case NAMESERVER_NAME:
			// Gets´s domain by it´s Nameserver name
			result = getDAO().searchByNsName(domain, resultLimit, useNameserverAsAttribute);
			break;
		case NAMESERVER_IP:
			// Get´s domain by it´s Nameserver Ip
			result = getDAO().searchByNsIp(domain, resultLimit, useNameserverAsAttribute);
			break;
		default:
			throw new RequestHandleException(501, "Not implemented.");
		}

		return result;

	}

	private SearchResultStruct<Domain> getRegexSearch(String username, RdapSearchRequest request)
			throws RequestHandleException, SQLException, IOException, RdapDataAccessException {
		SearchResultStruct<Domain> result = new SearchResultStruct<Domain>();
		boolean useNameserverAsAttribute = RdapConfiguration.useNameserverAsDomainAttribute();
		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);

		String domain = request.getParameterValue();
		switch (request.getParameterName()) {
		case DOMAIN_NAME:
			if (!RdapConfiguration.hasZoneConfigured()) {
				// Is valid if there are no available zones, because the
				// rdap could only respond to autnum and ip networks
				// (RIR).
				throw new RequestHandleException(501, "Not implemented yet.");
			}
			result = getDAO().searchByRegexName(domain, resultLimit, useNameserverAsAttribute);
			break;
		case NAMESERVER_NAME:
			// Gets´s domain by it´s Nameserver name
			result = getDAO().searchByNsName(domain, resultLimit, useNameserverAsAttribute);
			break;
		case NAMESERVER_IP:
			result = getDAO().searchByRegexNsIp(domain, resultLimit, useNameserverAsAttribute);
			break;
		default:
			throw new RequestHandleException(501, "Not implemented.");
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
	protected RdapResult doRdapDaHead(HttpServletRequest httpRequest) throws RequestHandleException {
		throw new RequestHandleException(501, "Not implemented yet.");
	}

	private static void validateSearchRequest(RdapSearchRequest searchRequest) throws MalformedRequestException {
		String parameter = searchRequest.getParameterName();
		String value = searchRequest.getParameterValue();

		if (parameter.equals(NAMESERVER_IP)) {
			IpUtil.validateIpAddress(value);
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
