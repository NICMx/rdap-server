package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.db.exception.ObjectNotFoundException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.DomainDAO;
import mx.nic.rdap.server.DataAccessServlet;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.exception.BadRequestException;
import mx.nic.rdap.server.exception.HttpException;
import mx.nic.rdap.server.result.DomainResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "domain", urlPatterns = { "/domain/*" })
public class DomainServlet extends DataAccessServlet<DomainDAO> {

	private static final long serialVersionUID = 1L;

	@Override
	protected DomainDAO initAccessDAO() throws RdapDataAccessException {
		return DataAccessService.getDomainDAO();
	}

	@Override
	protected String getServedObjectName() {
		return "domain";
	}

	@Override
	protected RdapResult doRdapDaGet(HttpServletRequest httpRequest, DomainDAO dao)
			throws HttpException, RdapDataAccessException {
		DomainRequest request = new DomainRequest(Util.getRequestParams(httpRequest)[0]);

		Domain domain = dao.getByName(request.getFullRequestValue());
		if (domain == null) {
			return null;
		}

		return new DomainResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), domain,
				Util.getUsername(httpRequest));
	}

	private class DomainRequest {

		private String fullRequestValue;

		private String domainName;

		private String zoneName;

		public DomainRequest(String requestValue) throws ObjectNotFoundException, BadRequestException {
			super();
			if (requestValue.endsWith(".")) {
				requestValue = requestValue.substring(0, requestValue.length() - 1);
			}
			this.fullRequestValue = requestValue;

			if (!requestValue.contains("."))
				throw new BadRequestException("The requested domain does not seem to include a zone.");
			if (!RdapConfiguration.isValidZone(requestValue))
				throw new ObjectNotFoundException("The zone is unmanaged by this server.");
		}

		public String getFullRequestValue() {
			return fullRequestValue;
		}

		@SuppressWarnings("unused")
		public String getDomainName() {
			return domainName;
		}

		@SuppressWarnings("unused")
		public String getZoneName() {
			return zoneName;
		}

	}

}