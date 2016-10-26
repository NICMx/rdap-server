package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.model.NameserverModel;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.NameserverResult;

/**
 * Servlet that find a nameserver by its name
 * 
 * @author dalpuche
 *
 */
@WebServlet(name = "nameserver", urlPatterns = { "/nameserver/*" })
public class NameserverServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public NameserverServlet() throws IOException {
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
		NameserverRequest request = new NameserverRequest(Util.getRequestParams(httpRequest)[0]);
		Nameserver nameserver = null;
		try (Connection con = DatabaseSession.getRdapConnection()) {
			nameserver = NameserverModel.findByName(request.getName(), con);
		}
		return new NameserverResult(nameserver);
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

	private class NameserverRequest {

		private String name;

		public NameserverRequest(String name) {
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
