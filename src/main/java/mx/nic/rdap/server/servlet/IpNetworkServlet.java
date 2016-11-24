package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.db.model.IpNetworkModel;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.IpResult;

@WebServlet(name = "ip", urlPatterns = { "/ip/*" })
public class IpNetworkServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public IpNetworkServlet() throws IOException {
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
		IpRequest request = new IpRequest(Util.getRequestParams(httpRequest));
		String userName = httpRequest.getRemoteUser();

		RdapResult result = null;
		try (Connection con = DatabaseSession.getRdapConnection()) {
			IpNetwork ipNetwork = null;
			if (request.hasCidr()) {
				ipNetwork = IpNetworkModel.getByInetAddress(request.getIp(), request.getCidr(), con);
			} else {
				ipNetwork = IpNetworkModel.getByInetAddress(request.getIp(), con);
			}
			result = new IpResult(ipNetwork, userName);

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

	private class IpRequest {

		private String ip;
		private Integer cidr;

		public IpRequest(String[] params) {
			super();
			this.ip = params[0];

			if (params.length > 1) {
				cidr = Integer.parseInt(params[1]);
			}
		}

		public String getIp() {
			return ip;
		}

		public Integer getCidr() {
			return cidr;
		}

		public boolean hasCidr() {
			return cidr != null;
		}

	}

}
