package mx.nic.rdap.server.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.DomainLabel;
import mx.nic.rdap.core.db.DomainLabelException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.DomainDAO;
import mx.nic.rdap.server.configuration.RdapConfiguration;
import mx.nic.rdap.server.result.DomainResult;
import mx.nic.rdap.server.result.RdapResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "domain", urlPatterns = {"/domain/*"})
public class DomainServlet extends DataAccessServlet<DomainDAO> {

	private static final long serialVersionUID = 1L;

	/**
	 * Constant value to set the maximum params expected in the URI, this servlet
	 * expects: domain
	 */
	private static final int MAX_PARAMS_EXPECTED = 1;

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
		DomainRequest request = new DomainRequest(Util.getRequestParams(httpRequest, MAX_PARAMS_EXPECTED)[0]);

		DomainLabel label;
		try {
			label = new DomainLabel(request.getFullRequestValue(), true);
			if (!RdapConfiguration.allowLabelsMixture() && label.hasMixture()) {
				throw new BadRequestException("Invalid domain label: '" + label.getLabel() + "'");
			}
		} catch (DomainLabelException e) {
			if (e.getMessage() != null) {
				throw new BadRequestException(e.getMessage(), e);
			} else {
				throw new BadRequestException(e);
			}
		}
		Domain domain = dao.getByName(label);
		if (domain == null) {
			return null;
		}

		checkResponse(domain, label);
		
		return new DomainResult(Util.getServerUrl(httpRequest), httpRequest.getContextPath(), domain,
				Util.getUsername(SecurityUtils.getSubject()));
	}

	private static void checkResponse(Domain domain, DomainLabel labelRequested) {
		boolean isAlabel = labelRequested.isALabel();
		String zone = domain.getZone();
		String resultToAdd;

		if (isAlabel && (domain.getLdhName() == null || domain.getLdhName().isEmpty())) {
			resultToAdd = labelRequested.getALabel().trim();
			if (zone != null && !zone.isEmpty()) {
				resultToAdd = processResultToAdd(zone, resultToAdd);
			}

			if (resultToAdd.endsWith("."))
				resultToAdd = resultToAdd.substring(0, resultToAdd.length() - 1);

			domain.setLdhName(resultToAdd);
		} else if (!isAlabel && (domain.getUnicodeName() == null || domain.getUnicodeName().isEmpty())) {
			resultToAdd = labelRequested.getULabel().trim();
			if (zone != null && !zone.isEmpty()) {
				resultToAdd = processResultToAdd(zone, resultToAdd);
			}

			if (resultToAdd.endsWith("."))
				resultToAdd = resultToAdd.substring(0, resultToAdd.length() - 1);

			domain.setUnicodeName(resultToAdd);
		}
	}

	private static String processResultToAdd(String zone, String resultToSanitize) {
		if (resultToSanitize.endsWith("."))
			resultToSanitize = resultToSanitize.substring(0, resultToSanitize.length() - 1);

		if (zone.endsWith("."))
			zone = zone.substring(0, zone.length() - 1);

		int lastIndexOf = resultToSanitize.lastIndexOf(zone);
		if (lastIndexOf > 0)
			resultToSanitize = resultToSanitize.substring(0, lastIndexOf);

		return resultToSanitize;
	}

	private class DomainRequest {

		private String fullRequestValue;

		public DomainRequest(String requestValue) throws BadRequestException {
			super();
			if (requestValue.endsWith(".")) {
				requestValue = requestValue.substring(0, requestValue.length() - 1);
			}
			this.fullRequestValue = requestValue;

			if (!requestValue.contains("."))
				throw new BadRequestException("The requested domain does not seem to include a zone.");
		}

		public String getFullRequestValue() {
			return fullRequestValue;
		}

	}

}
