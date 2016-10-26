package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.NameserverDAO;
import mx.nic.rdap.server.db.model.NameserverModel;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.exception.UnprocessableEntityException;
import mx.nic.rdap.server.result.NameserverSeachResult;

/**
 * Servlet that find a nameserver by its name
 * 
 * @author dalpuche
 *
 */
@WebServlet(name = "nameservers", urlPatterns = { "/nameservers" })
public class NameserverSearchServlet extends RdapServlet {
	private static final long serialVersionUID = 1L;

	public NameserverSearchServlet() throws IOException {
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
		NameserverSearchRequest request = new NameserverSearchRequest(httpRequest);
		List<NameserverDAO> nameservers = new ArrayList<NameserverDAO>();
		try (Connection connection = DatabaseSession.getRdapConnection()) {
			if (request.getParameter().compareTo(NameserverSearchRequest.NAME_PARAMETER_KEY) == 0) {
				nameservers = NameserverModel.searchByName(request.getValue().trim(), connection);
				return new NameserverSeachResult(nameservers);
			} else {
				String ipAddress = request.getValue().trim();
				Util.validateIpAddress(ipAddress);
				nameservers = NameserverModel.searchByIp(ipAddress, connection);
				return new NameserverSeachResult(nameservers);
			}
		}
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

	private class NameserverSearchRequest {

		public static final String IP_PARAMETER_KEY = "ip";
		public static final String NAME_PARAMETER_KEY = "name";
		private String parameter;
		private String value;

		public NameserverSearchRequest(HttpServletRequest httpRequest) throws UnprocessableEntityException {
			super();
			Util.validateSearchRequest(httpRequest, IP_PARAMETER_KEY, NAME_PARAMETER_KEY);
			this.parameter = httpRequest.getParameterNames().nextElement();
			this.value = httpRequest.getParameter(this.parameter);
		}

		/**
		 * @return the parameter
		 */
		public String getParameter() {
			return parameter;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

	}
}
