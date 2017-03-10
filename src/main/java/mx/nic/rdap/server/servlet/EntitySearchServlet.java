package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.EntityDAO;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapSearchRequest;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.EntitySearchResult;

@WebServlet(name = 	"entities", urlPatterns = { "/entities" })
public class EntitySearchServlet extends DataAccessServlet<EntityDAO> {

	private static final long serialVersionUID = -8023237096799052268L;

	public static final String FULL_NAME = "fn";
	public static final String HANDLE = "handle";
	
	@Override
	protected EntityDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getEntityDAO();
	}
	
	@Override
	protected String getServedObjectName() {
		return "entities";
	}

	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, EntityDAO dao)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException {
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
			result = getPartialSearch(username, searchRequest, dao);
			break;
		case REGEX_SEARCH:
			result = getRegexSearch(username, searchRequest, dao);
			break;
		default:
			throw new RequestHandleException(501, "Not implemented.");
		}

		return new EntitySearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result, username);
	}

	private SearchResultStruct<Entity> getPartialSearch(String username, RdapSearchRequest searchRequest, EntityDAO dao)
			throws SQLException, IOException, RdapDataAccessException {
		SearchResultStruct<Entity> result = null;

		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);
		switch (searchRequest.getParameterName()) {
		case FULL_NAME:
			result = dao.searchByVCardName(searchRequest.getParameterValue(), resultLimit);
			break;
		case HANDLE:
			result = dao.searchByHandle(searchRequest.getParameterValue(), resultLimit);
			break;
		}

		return result;
	}

	private SearchResultStruct<Entity> getRegexSearch(String username, RdapSearchRequest searchRequest, EntityDAO dao)
			throws SQLException, IOException, RequestHandleException, RdapDataAccessException {
		SearchResultStruct<Entity> result = null;

		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUser(username);
		switch (searchRequest.getParameterName()) {
		case FULL_NAME:
			result = dao.searchByRegexVCardName(searchRequest.getParameterValue(), resultLimit);
			break;
		case HANDLE:
			result = dao.searchByRegexHandle(searchRequest.getParameterValue(), resultLimit);
			break;
		}

		return result;
	}

}
