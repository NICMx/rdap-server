package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.db.exception.IpAddressFormatException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.IpNetworkDAO;
import mx.nic.rdap.db.struct.AddressBlock;
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
		AddressBlock block;
		IpNetwork network;

		try {
			block = new AddressBlock(request.getIp(), request.hasCidr() ? request.getCidr() : null);
		} catch (IpAddressFormatException e) {
			throw new BadRequestException(e.getMessage(), e);
		}

		network = dao.getByAddressBlock(block);
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
