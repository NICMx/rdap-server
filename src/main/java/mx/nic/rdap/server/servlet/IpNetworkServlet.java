package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.db.model.IpNetworkModel;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.IpResult;
import mx.nic.rdap.server.result.OkResult;
import mx.nic.rdap.server.util.Util;

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
		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

		IpNetwork ipNetwork = null;
		try (Connection con = DatabaseSession.getRdapConnection()) {
			if (request.hasCidr()) {
				ipNetwork = IpNetworkModel.getByInetAddress(request.getIp(), request.getCidr(), con);
			} else {
				ipNetwork = IpNetworkModel.getByInetAddress(request.getIp(), con);
			}

		} catch (UnknownHostException e) {
			throw new MalformedRequestException("Invalid IP address", e);
		}

		return new IpResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), ipNetwork, username);
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
		IpRequest request = new IpRequest(Util.getRequestParams(httpRequest));
		try (Connection con = DatabaseSession.getRdapConnection()) {
			if (request.hasCidr()) {
				IpNetworkModel.existByInetAddress(request.getIp(), request.getCidr(), con);
			} else {
				IpNetworkModel.existByInetAddress(request.getIp(), con);
			}

		} catch (UnknownHostException e) {
			throw new MalformedRequestException("Invalid IP address", e);
		}
		return new OkResult();
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
