package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.exception.RdapDatabaseException;
import mx.nic.rdap.db.services.EntityService;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapSearchRequest;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.EntitySearchResult;

@WebServlet(name = "entities", urlPatterns = { "/entities" })
public class EntitySearchServlet extends RdapServlet {

	private static final long serialVersionUID = -8023237096799052268L;

	public static final String FULL_NAME = "fn";
	public static final String HANDLE = "handle";

	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException, RdapDatabaseException {

		RdapSearchRequest searchRequest = null;
		try {
			searchRequest = RdapSearchRequest.getSearchRequest(httpRequest, true, FULL_NAME, HANDLE);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}

		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

		SearchResultStruct<Entity> result = null;
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

		return new EntitySearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result, username);
	}

	private SearchResultStruct<Entity> getPartialSearch(String username, RdapSearchRequest searchRequest)
			throws SQLException, IOException, RdapDatabaseException {
		SearchResultStruct<Entity> result = null;

		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);
		switch (searchRequest.getParameterName()) {
		case FULL_NAME:
			result = EntityService.searchByVCardName(searchRequest.getParameterValue(), resultLimit);
			break;
		case HANDLE:
			result = EntityService.searchByHandle(searchRequest.getParameterValue(), resultLimit);
			break;
		}

		return result;
	}

	private SearchResultStruct<Entity> getRegexSearch(String username, RdapSearchRequest searchRequest)
			throws SQLException, IOException, RequestHandleException, RdapDatabaseException {
		SearchResultStruct<Entity> result = null;

		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);
		switch (searchRequest.getParameterName()) {
		case FULL_NAME:
			result = EntityService.searchByRegexVCardName(searchRequest.getParameterValue(), resultLimit);
			break;
		case HANDLE:
			result = EntityService.searchByRegexHandle(searchRequest.getParameterValue(), resultLimit);
			break;
		}

		return result;
	}

	@Override
	protected RdapResult doRdapHead(HttpServletRequest httpRequest) throws RequestHandleException {
		throw new RequestHandleException(501, "Not implemented yet.");
	}

}
