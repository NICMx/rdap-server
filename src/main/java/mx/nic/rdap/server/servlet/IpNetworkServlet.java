package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;

import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.ip.AddressBlock;
import mx.nic.rdap.core.ip.IpAddressFormatException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.IpNetworkDAO;
import mx.nic.rdap.server.result.IpResult;
import mx.nic.rdap.server.result.RdapResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "ip", urlPatterns = { "/ip/*" })
public class IpNetworkServlet extends DataAccessServlet<IpNetworkDAO> {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant value to set the maximum params expected in the URI, this servlet expects: ip, cidr
	 */
	private static final int MAX_PARAMS_EXPECTED = 2;

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
		IpRequest request = new IpRequest(Util.getRequestParams(httpRequest, MAX_PARAMS_EXPECTED));
		// Validate cidr parsed (if present), extra validations are made at constructor of AddressBlock
		if (request.hasCidr()) {
			if (request.getCidr() < 0) {
				throw new BadRequestException("Invalid cidr value");
			}
		}
		AddressBlock block;
		IpNetwork network;

		try {
			block = new AddressBlock(request.getIp(), request.hasCidr() ? request.getCidr() : null);
		} catch (IpAddressFormatException e) {
			throw new BadRequestException(e);
		}

		network = dao.getByAddressBlock(block);
		if (network == null) {
			return null;
		}

		return new IpResult(Util.getServerUrl(httpRequest), httpRequest.getContextPath(), network,
				Util.getUsername(SecurityUtils.getSubject()));
	}

	private class IpRequest {

		private String ip;
		private Integer cidr;

		public IpRequest(String[] params) {
			super();
			this.ip = params[0];

			if (params.length > 1) {
				try {
					cidr = Integer.parseInt(params[1]);
				} catch (NumberFormatException e) {
					cidr = -1;
				}
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
