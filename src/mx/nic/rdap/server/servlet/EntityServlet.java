package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.model.EntityModel;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.EntityResult;

/**
 * Servlet that find a entity by its handle
 * 
 * @author dhfelix
 *
 */
@WebServlet(name = "entity", urlPatterns = { "/entity/*" })
public class EntityServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public EntityServlet() throws IOException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {
		EntityRequest request = new EntityRequest(Util.getRequestParams(httpRequest)[0]);

		RdapResult result = null;
		try (Connection con = DatabaseSession.getConnection();) {
			Entity entity = EntityModel.getByHandle(request.getName(), con);
			result = new EntityResult(entity);

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
	protected RdapResult doRdapHead(HttpServletRequest request)
			throws RequestHandleException, IOException, SQLException {
		throw new RequestHandleException(501, "Not implemented yet.");
	}

	private class EntityRequest {

		private String name;

		public EntityRequest(String name) {
			super();
			this.name = name;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

	}

}
