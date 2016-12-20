package mx.nic.rdap.server.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.model.EntityModel;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
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

		try {
			validateRequestParameter(httpRequest);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}

		String parameter = httpRequest.getParameterNames().nextElement();
		String value = httpRequest.getParameter(parameter);
		String userName = httpRequest.getRemoteUser();
		SearchResultStruct result = new SearchResultStruct();

		Integer resultLimit = RdapConfiguration.getMaxNumberOfResultsForUnauthenticatedUser();// Default

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			resultLimit = Util.getMaxNumberOfResultsForUser(userName, connection);
			switch (parameter) {
			case FULL_NAME:
				result = EntityModel.searchByVCardName(value.trim(), resultLimit, connection);
				break;
			case HANDLE:
				result = EntityModel.searchByHandle(value.trim(), resultLimit, connection);
				break;
			}
		}

		return new EntitySearchResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), result, userName,
				httpRequest.getServletContext().getRealPath(File.separator));
	}

	@Override
	protected RdapResult doRdapHead(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {
		try {
			validateRequestParameter(httpRequest);
		} catch (UnprocessableEntityException e) {
			throw new RequestHandleException(e.getHttpResponseStatusCode(), e.getMessage());
		}

		String parameter = httpRequest.getParameterNames().nextElement();
		String value = httpRequest.getParameter(parameter);

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			switch (parameter) {
			case FULL_NAME:
				EntityModel.existByVCardName(value.trim(), connection);
				break;
			case HANDLE:
				EntityModel.existByHandle(value.trim(), connection);
				break;
			}
		}

		return new OkResult();
	}

	private static void validateRequestParameter(HttpServletRequest request) throws UnprocessableEntityException {
		Util.validateEntitySearchRequest(request, FULL_NAME, HANDLE);

	}

}
