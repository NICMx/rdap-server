package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.db.EntityDAO;
import mx.nic.rdap.db.model.EntityModel;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.EntityResult;
import mx.nic.rdap.server.result.OkResult;
import mx.nic.rdap.server.util.Util;

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
		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}
		EntityDAO entity = null;
		try (Connection con = DatabaseSession.getRdapConnection()) {
			entity = EntityModel.getByHandle(request.getHandle(), con);

		}
		return new EntityResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), entity, username);
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
		EntityRequest request = new EntityRequest(Util.getRequestParams(httpRequest)[0]);
		try (Connection con = DatabaseSession.getRdapConnection()) {
			EntityModel.existByHandle(request.getHandle(), con);
		}
		return new OkResult();
	}

	private class EntityRequest {

		private String handle;

		public EntityRequest(String handle) {
			super();
			this.handle = handle;
		}

		/**
		 * @return the name
		 */
		public String getHandle() {
			return handle;
		}

	}

}
