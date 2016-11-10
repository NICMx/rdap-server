package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.EntityDAO;
import mx.nic.rdap.db.model.EntityModel;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.EntitySearchResult;

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

		List<EntityDAO> entities = null;
		String parameter = httpRequest.getParameterNames().nextElement();
		String value = httpRequest.getParameter(parameter);
		String userName = httpRequest.getRemoteUser();

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			Integer resultLimit = Util.getMaxNumberOfResultsForUser(userName, connection);
			switch (parameter) {
			case FULL_NAME:
				entities = EntityModel.searchByVCardName(value.trim(), resultLimit, connection);
				break;
			case HANDLE:
				entities = EntityModel.searchByHandle(value.trim(), resultLimit, connection);
				break;
			}
		}

		List<Entity> entityList = null;
		if (entities != null)
			entityList = new ArrayList<Entity>(entities);
		return new EntitySearchResult(entityList, userName);
	}

	@Override
	protected RdapResult doRdapHead(HttpServletRequest request)
			throws RequestHandleException, IOException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	private static void validateRequestParameter(HttpServletRequest request) throws UnprocessableEntityException {
		Util.validateEntitySearchRequest(request, FULL_NAME, HANDLE);

	}

}
