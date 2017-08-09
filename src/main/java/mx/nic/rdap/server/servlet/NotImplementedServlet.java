package mx.nic.rdap.server.servlet;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.spi.DAO;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapResult;

public class NotImplementedServlet extends DataAccessServlet<DAO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9084287298582015457L;

	public NotImplementedServlet() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected DAO initAccessDAO() throws RdapDataAccessException {
		return null;
	}

	@Override
	protected String getServedObjectName() {
		return this.getServletName();
	}

	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest request, DAO dao)
			throws HttpException, RdapDataAccessException {
		return null;
	}

}
