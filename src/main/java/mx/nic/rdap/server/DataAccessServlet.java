package mx.nic.rdap.server;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.db.exception.NotImplementedException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.spi.DAO;
import mx.nic.rdap.server.exception.HttpException;

/**
 * An RDAP servlet that extrats information from a specific DAO.
 */
public abstract class DataAccessServlet<T extends DAO> extends RdapServlet {

	/** Warning shutupper. */
	private static final long serialVersionUID = 1L;

	/**
	 * Used to initialize {@link #dao} with whatever the servlet is supposed to
	 * serve.
	 */
	protected abstract T initAccessDAO() throws RdapDataAccessException;

	/**
	 * Returns the name of the objects this servlet handles. Used for debugging
	 * and error messages.
	 */
	protected abstract String getServedObjectName();

	@Override
	protected RdapResult doRdapGet(HttpServletRequest request) throws HttpException, RdapDataAccessException {
		T dao = initAccessDAO();
		if (dao == null) {
			throw new NotImplementedException("This server does not implement " + getServedObjectName() + " requests.");
		}

		return doRdapDaGet(request, dao);
	}

	/**
	 * Adds data-access-specific validations on top of
	 * {@link #doRdapGet(HttpServletRequest)}.
	 */
	protected abstract RdapResult doRdapDaGet(HttpServletRequest request, T dao)
			throws HttpException, RdapDataAccessException;

}
