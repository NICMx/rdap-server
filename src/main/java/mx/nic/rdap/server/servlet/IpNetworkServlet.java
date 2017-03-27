package mx.nic.rdap.server.servlet;

import java.net.InetAddress;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.IpUtils;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.IpNetworkDAO;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.result.IpResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "ip", urlPatterns = { "/ip/*" })
public class IpNetworkServlet extends DataAccessServlet<IpNetworkDAO> {

	private static final long serialVersionUID = 1L;

	@Override
	protected IpNetworkDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getIpNetworkDAO();
	}

	@Override
	protected String getServedObjectName() {
		return "IP network";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, IpNetworkDAO dao)
			throws HttpException, RdapDataAccessException {
		IpRequest request = new IpRequest(Util.getRequestParams(httpRequest));
		InetAddress address = IpUtils.validateIpAddress(request.getIp());
		IpNetwork network;
		if (request.hasCidr()) {
			network = dao.getByInetAddress(address, request.getCidr());
		} else {
			network = dao.getByInetAddress(address);
		}

		if (network == null) {
			return null;
		}

		return new IpResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), network,
				Util.getUsername(httpRequest));
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
