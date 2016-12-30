package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.net.IDN;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.model.DomainModel;
import mx.nic.rdap.db.model.ZoneModel;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapSearchRequest;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.DomainSearchResult;
import mx.nic.rdap.server.result.OkResult;
import mx.nic.rdap.server.util.IpUtil;

@WebServlet(name = "domains", urlPatterns = { "/domains" })
public class DomainSearchServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public final static String DOMAIN_NAME = "name";

	public final static String NAMESERVER_NAME = "nsLdhName";

	public final static String NAMESERVER_IP = "nsIp";

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {
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

		SearchResultStruct result = null;
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

	private SearchResultStruct getPartialSearch(String username, RdapSearchRequest request)
			throws RequestHandleException, SQLException, IOException {
		SearchResultStruct result = new SearchResultStruct();
		boolean useNameserverAsAttribute = RdapConfiguration.useNameserverAsDomainAttribute();
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username, connection);

			String domain = request.getParameterValue();
			if (IDN.toASCII(domain) != domain) {
				domain = IDN.toASCII(domain);
			}
			switch (request.getParameterName()) {
			case DOMAIN_NAME:
				if (ZoneModel.getValidZoneIds() == null || ZoneModel.getValidZoneIds().isEmpty()) {
					// Is valid if there are no available zones, because the
					// rdap could only respond to autnum and ip networks
					// (RIR).
					throw new RequestHandleException(501, "Not implemented yet.");
				}

				if (request.getParameterValue().contains(".")) {
					// Gets domain by its name with zone
					String[] split = request.getParameterValue().split("\\.", 2);
					domain = split[0];
					if (IDN.toASCII(domain) != domain) {
						domain = IDN.toASCII(domain);
					}
					String zone = split[1];

					result = DomainModel.searchByName(domain, zone, resultLimit, useNameserverAsAttribute, connection);
				} else {

					// Search domain by it´s name without zone.
					result = DomainModel.searchByName(domain, resultLimit, useNameserverAsAttribute, connection);
				}
				break;
			case NAMESERVER_NAME:
				// Gets´s domain by it´s Nameserver name
				result = DomainModel.searchByNsLdhName(domain, resultLimit, useNameserverAsAttribute, connection);
				break;
			case NAMESERVER_IP:
				// Get´s domain by it´s Nameserver Ip
				result = DomainModel.searchByNsIp(domain, resultLimit, useNameserverAsAttribute, connection);
				break;
			default:
				throw new RequestHandleException(501, "Not implemented.");
			}

		}

		return result;

	}

	private SearchResultStruct getRegexSearch(String username, RdapSearchRequest request)
			throws RequestHandleException, SQLException, IOException {
		SearchResultStruct result = new SearchResultStruct();
		boolean useNameserverAsAttribute = RdapConfiguration.useNameserverAsDomainAttribute();
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username, connection);

			switch (request.getParameterName()) {
			case DOMAIN_NAME:
				if (ZoneModel.getValidZoneIds() == null || ZoneModel.getValidZoneIds().isEmpty()) {
					// Is valid if there are no available zones, because the
					// rdap could only respond to autnum and ip networks
					// (RIR).
					throw new RequestHandleException(501, "Not implemented yet.");
				}

				String domain = request.getParameterValue();
				if (request.getParameterValue().contains("\\.")) {
					// Gets domain by its name with zone
					String[] split = request.getParameterValue().split("\\.", 2);
					domain = split[0];
					String zone = split[1];

					result = DomainModel.searchByRegexName(domain, zone, resultLimit, useNameserverAsAttribute,
							connection);
				} else {

					// Search domain by it´s name without zone.
					result = DomainModel.searchByRegexName(domain, resultLimit, useNameserverAsAttribute, connection);
				}
				break;
			case NAMESERVER_NAME:
				// Gets´s domain by it´s Nameserver name
				result = DomainModel.searchByRegexNsLdhName(request.getParameterValue(), resultLimit,
						useNameserverAsAttribute, connection);
				break;
			case NAMESERVER_IP:
				throw new RequestHandleException(501, "Not implemented.");
				// Get´s domain by it´s Nameserver Ip
				// result =
				// DomainModel.searchByNsIp(request.getParameterValue(),
				// resultLimit, useNameserverAsAttribute, connection);
			default:
				throw new RequestHandleException(501, "Not implemented.");
			}

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
	protected RdapResult doRdapHead(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {
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

	private void doRdapHeadPartialSearch(RdapSearchRequest request) throws SQLException, UnknownHostException {
		String domain = request.getParameterValue();
		if (IDN.toASCII(domain) != domain) {
			domain = IDN.toASCII(domain);
		}
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			switch (request.getParameterName()) {
			case DOMAIN_NAME:
				// Gets domain by its name with zone
				if (request.getParameterValue().contains(".")) {
					String[] split = request.getParameterValue().split("\\.", 2);
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
	}

	private void doRdapHeadRegexSearch() throws RequestHandleException {
		throw new RequestHandleException(501, "Not implemented yet.");
	}

	private static void validateSearchRequest(RdapSearchRequest searchRequest) throws MalformedRequestException {
		String parameter = searchRequest.getParameterName();
		String value = searchRequest.getParameterValue();

		if (parameter.equals(NAMESERVER_IP)) {
			IpUtil.validateIpAddress(value);
		}

		if (value.endsWith(".")) {
			searchRequest.setParameterValue(value.substring(0, value.length() - 1));
		}
	}

}
