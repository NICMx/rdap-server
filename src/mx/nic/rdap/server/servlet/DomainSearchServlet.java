package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
/**
 * Servlet that finds domain 
 * 
 * @author evaldes
 * 
 */
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.exception.UnprocessableEntityException;

@WebServlet(name = "domains", urlPatterns = { "/domains" })
public class DomainSearchServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public DomainSearchServlet() throws IOException {
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
		DomainSearchRequest request = new DomainSearchRequest(httpRequest);
		RdapResult result = null;
		try (Connection con = DatabaseSession.getConnection()) {
			if (request.getParameter() == "name") {
				// TODO search by name
			}
			if (request.getParameter() == "nsLdhName") {
				// TODO search by name server name
			}
			if (request.getParameter() == "nsIp") {
				// TODO search by name server Ip
			}
		} catch (SQLException e) {
			throw new MalformedRequestException(e);
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

	private class DomainSearchRequest {

		public final String DOMAIN_NAME = "name";

		public final String NAMESERVER_NAME = "nsLdhName";

		public final String NAMESERVER_IP = "nsIp";

		private String parameter;

		private String value;

		public DomainSearchRequest(HttpServletRequest httpRequest)
				throws UnprocessableEntityException, MalformedRequestException {
			super();
			Util.validateSearchRequestParameters(httpRequest, DOMAIN_NAME, NAMESERVER_NAME, NAMESERVER_IP);
			this.parameter = httpRequest.getParameterNames().nextElement();
			this.value = httpRequest.getParameter(parameter);
			if (this.parameter == NAMESERVER_IP) {
				Util.validateIpAddress(value);
			}
		}

		public String getParameter() {
			return parameter;
		}

		public String getValue() {
			return value;
		}

	}

}
