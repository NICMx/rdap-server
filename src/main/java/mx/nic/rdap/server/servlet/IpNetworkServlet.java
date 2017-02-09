package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.exception.RdapDatabaseException;
import mx.nic.rdap.db.services.IpNetworkService;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
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
			throws RequestHandleException, IOException, SQLException, RdapDatabaseException {
		IpRequest request = new IpRequest(Util.getRequestParams(httpRequest));
		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

		IpNetwork ipNetwork = null;
		try {
			if (request.hasCidr()) {
				ipNetwork = IpNetworkService.getByInetAddress(request.getIp(), request.getCidr());
			} else {
				ipNetwork = IpNetworkService.getByInetAddress(request.getIp());
			}

		} catch (UnknownHostException e) {
			throw new MalformedRequestException("Invalid IP address", e);
		} catch (InvalidValueException e) {
			throw new MalformedRequestException(e.getMessage(), e);
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
			throws RequestHandleException, IOException, SQLException, RdapDatabaseException {
		IpRequest request = new IpRequest(Util.getRequestParams(httpRequest));
		try {
			if (request.hasCidr()) {
				IpNetworkService.existByInetAddress(request.getIp(), request.getCidr());
			} else {
				IpNetworkService.existByInetAddress(request.getIp());
			}

		} catch (UnknownHostException e) {
			throw new MalformedRequestException("Invalid IP address", e);
		} catch (InvalidValueException e) {
			throw new MalformedRequestException(e.getMessage(), e);
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
