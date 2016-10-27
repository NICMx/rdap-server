package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.model.EntityModel;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.exception.UnprocessableEntityException;
import mx.nic.rdap.server.result.EntitySearchResult;

/**
 * Servlet that searchs entities
 * 
 * @author dhfelix
 *
 */
@WebServlet(name = "entities", urlPatterns = { "/entities" })
public class EntitySearchServlet extends RdapServlet {

	private static final long serialVersionUID = -8023237096799052268L;

	public static final String FULL_NAME = "fn";

	public static final String HANDLE = "handle";

	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {

		validateRequestParameter(httpRequest);

		List<Entity> entities = null;
		String parameter = httpRequest.getParameterNames().nextElement();
		String value = httpRequest.getParameter(parameter);

		try (Connection connection = DatabaseSession.getRdapConnection()) {
			Util.fillSearchDataForUser(httpRequest, connection);
			switch (parameter) {
			case FULL_NAME:
				entities = EntityModel.searchByVCardName(value.trim(), connection);
				break;
			case HANDLE:
				entities = EntityModel.searchByHandle(value.trim(), connection);
				break;
			}
		}

		return new EntitySearchResult(entities);
	}

	@Override
	protected RdapResult doRdapHead(HttpServletRequest request)
			throws RequestHandleException, IOException, SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	private static void validateRequestParameter(HttpServletRequest request) throws UnprocessableEntityException {
		Util.validateSearchRequest(request, FULL_NAME, HANDLE);

	}

}
