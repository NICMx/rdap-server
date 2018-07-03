package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.NameserverDAO;
import mx.nic.rdap.server.result.NameserverResult;
import mx.nic.rdap.server.result.RdapResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "nameserver_handle", urlPatterns = { "/nameserver_handle/*" })
public class NameserverHandleServlet extends DataAccessServlet<NameserverDAO> {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant value to set the maximum params expected in the URI, this servlet
	 * expects: nameserver
	 */
	private static final int MAX_PARAMS_EXPECTED = 1;

	@Override
	protected NameserverDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getNameserverDAO();
	}

	@Override
	protected String getServedObjectName() {
		return "nameserver_handle";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, NameserverDAO dao)
			throws HttpException, RdapDataAccessException {
		String handle = Util.getRequestParams(httpRequest, MAX_PARAMS_EXPECTED)[0];

		Nameserver nameserver = dao.getByHandle(handle);
		if (nameserver == null) {
			return null;
		}

		// If it is searched by handle, then it indicates that this nameserver is
		// unique.
		int nameserverCount = 0;

		return new NameserverResult(Util.getServerUrl(httpRequest), httpRequest.getContextPath(), nameserver,
				Util.getUsername(SecurityUtils.getSubject()), nameserverCount);
	}


}
