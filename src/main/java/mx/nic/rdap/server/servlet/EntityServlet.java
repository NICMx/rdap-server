package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.EntityDAO;
import mx.nic.rdap.server.result.EntityResult;
import mx.nic.rdap.server.result.RdapResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "entity", urlPatterns = { "/entity/*" })
public class EntityServlet extends DataAccessServlet<EntityDAO> {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant value to set the maximum params expected in the URI, this servlet expects: handle
	 */
	private static final int MAX_PARAMS_EXPECTED = 1;

	@Override
	protected EntityDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getEntityDAO();
	}

	@Override
	protected String getServedObjectName() {
		return "entity";
	}

	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, EntityDAO dao)
			throws HttpException, RdapDataAccessException {
		EntityRequest request = new EntityRequest(Util.getRequestParams(httpRequest, MAX_PARAMS_EXPECTED)[0]);

		Entity entity = dao.getByHandle(request.getHandle());
		if (entity == null) {
			return null;
		}

		return new EntityResult(Util.getServerUrl(httpRequest), httpRequest.getContextPath(), entity,
				Util.getUsername(SecurityUtils.getSubject()));
	}

	private class EntityRequest {

		private String handle;

		public EntityRequest(String handle) {
			super();
			this.handle = handle;
		}

		/**
		 * @return the name
		 */
		public String getHandle() {
			return handle;
		}

	}

}
