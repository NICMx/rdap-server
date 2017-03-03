package mx.nic.rdap.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.db.exception.NotImplementedException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.spi.DataAccessDAO;
import mx.nic.rdap.server.exception.RequestHandleException;

/**
 * An RDAP servlet that extrats information from a specific DAO.
 */
public abstract class DataAccessServlet<T extends DataAccessDAO> extends RdapServlet {

	/** Warning shutupper. */
	private static final long serialVersionUID = 1L;
	/** Typical class logger. */
	private static final Logger logger = Logger.getLogger(RdapServlet.class.getName());

	@Override
	public void init() throws ServletException {
		super.init();

		try {
			this.dao = initAccessDAO();
			if (this.dao == null) {
				logger.info("The data access implementation does not provide a " + getServedObjectName() + " DAO. "
						+ "I will not serve " + getServedObjectName() + " requests.");
			}
		} catch (RdapDataAccessException e) {
			throw new RuntimeException("Trouble loading the data access implementation's " + getServedObjectName() + //
					" DAO.", e);
		}
	}

	/**
	 * The "model" that will provide access to the data this servlet is supposed
	 * to serve.
	 */
	private T dao;

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
	protected RdapResult doRdapGet(HttpServletRequest request)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException {
		if (dao == null) {
			throw new NotImplementedException("This server does not implement " + getServedObjectName() + " requests.");
		}

		return doRdapDaGet(request);
	}

	/**
	 * Adds data-access-specific validations on top of
	 * {@link #doRdapGet(HttpServletRequest)}.
	 */
	protected abstract RdapResult doRdapDaGet(HttpServletRequest request)
			throws RequestHandleException, IOException, SQLException, RdapDataAccessException;

	/**
	 * Returns the DAO the servlet initialized during {@link #initAccessDAO()}.
	 */
	protected T getDAO() {
		return dao;
	}

}
