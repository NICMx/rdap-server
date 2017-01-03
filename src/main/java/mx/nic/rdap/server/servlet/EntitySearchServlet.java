package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.model.EntityModel;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapSearchRequest;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.EntitySearchResult;
import mx.nic.rdap.server.result.OkResult;

@WebServlet(name = "entities", urlPatterns = { "/entities" })
public class EntitySearchServlet extends RdapServlet {

	private static final long serialVersionUID = -8023237096799052268L;

	public static final String FULL_NAME = "fn";
	public static final String HANDLE = "handle";

	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {

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

		return new EntitySearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result, username);
	}

	private SearchResultStruct getPartialSearch(String username, RdapSearchRequest searchRequest)
			throws SQLException, IOException {
		SearchResultStruct result = null;

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username, connection);
			switch (searchRequest.getParameterName()) {
			case FULL_NAME:
				result = EntityModel.searchByVCardName(searchRequest.getParameterValue().trim(), resultLimit,
						connection);
				break;
			case HANDLE:
				result = EntityModel.searchByHandle(searchRequest.getParameterValue().trim(), resultLimit, connection);
				break;
			}
		}

		return result;
	}

	private SearchResultStruct getRegexSearch(String username, RdapSearchRequest searchRequest)
			throws SQLException, IOException, RequestHandleException {
		SearchResultStruct result = null;

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username, connection);
			switch (searchRequest.getParameterName()) {
			case FULL_NAME:
				result = EntityModel.searchByRegexName(searchRequest.getParameterValue().trim(), resultLimit,
						connection);
				break;
			case HANDLE:
				result = EntityModel.searchByRegexHandle(searchRequest.getParameterValue().trim(), resultLimit,
						connection);
				break;
			}
		} catch (SQLSyntaxErrorException e) {
			throw new RequestHandleException(400, e.getMessage());
		}

		return result;
	}

	@Override
	protected RdapResult doRdapHead(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {
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

	private void doRdapHeadPartialSearch(RdapSearchRequest searchRequest) throws SQLException {
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			switch (searchRequest.getParameterName()) {
			case FULL_NAME:
				EntityModel.existByVCardName(searchRequest.getParameterValue().trim(), connection);
				break;
			case HANDLE:
				EntityModel.existByHandle(searchRequest.getParameterValue().trim(), connection);
				break;
			}
		}
	}

	private void doRdapHeadRegexSearch() throws RequestHandleException {
		throw new RequestHandleException(501, "Not implemented yet.");
	}

}
