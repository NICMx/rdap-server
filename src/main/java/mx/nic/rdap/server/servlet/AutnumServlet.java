package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.AutnumDAO;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.AutnumResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "autnum", urlPatterns = { "/autnum/*" })
public class AutnumServlet extends DataAccessServlet<AutnumDAO> {

	private static final long serialVersionUID = 1L;

	@Override
	protected AutnumDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getAutnumDAO();
	}

	@Override
	protected String getServedObjectName() {
		return "autnum";
	}

	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, AutnumDAO dao)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException {
		AutnumRequest request = new AutnumRequest(Util.getRequestParams(httpRequest)[0]);
		Autnum autnum = null;
		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}

		autnum = dao.getByRange(request.getAutnum());
		return new AutnumResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), autnum, username);
	}

	private class AutnumRequest {

		private Long autnum;

		public AutnumRequest(String autnum) throws MalformedRequestException {
			super();
			try {
				this.autnum = Long.parseLong(autnum);
				if (this.autnum > 4294967295L || this.autnum < 0) {
					throw new MalformedRequestException("Autnum must be a positive 32 bit or less number.");
				}
			} catch (NumberFormatException e) {
				throw new MalformedRequestException("Autnum must be a positive 32 bit or less number.");
			}
		}

		public Long getAutnum() {
			return autnum;
		}
	}

}
