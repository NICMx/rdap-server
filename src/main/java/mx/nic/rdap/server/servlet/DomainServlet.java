package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.exception.ObjectNotFoundException;
import mx.nic.rdap.db.exception.RdapDatabaseException;
import mx.nic.rdap.db.services.DomainService;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.DomainResult;
import mx.nic.rdap.server.result.OkResult;
import mx.nic.rdap.server.util.Util;

@WebServlet(name = "domain", urlPatterns = { "/domain/*" })
public class DomainServlet extends RdapServlet {

	private static final long serialVersionUID = 1L;

	public DomainServlet() throws IOException {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapGet(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException, RdapDatabaseException {
		DomainRequest request = null;
		try {
			request = new DomainRequest(Util.getRequestParams(httpRequest)[0]);
		} catch (InvalidValueException | ObjectNotFoundException e) {
			throw new ObjectNotFoundException("The RDAP server doesn't have information about the requested zone");
		}
		String username = httpRequest.getRemoteUser();
		if (RdapConfiguration.isAnonymousUsername(username)) {
			username = null;
		}
		Domain domain = null;
		try {
			domain = DomainService.getByName(request.getFullRequestValue(),
					RdapConfiguration.useNameserverAsDomainAttribute());
		} catch (InvalidValueException e) {
			throw new ObjectNotFoundException("The RDAP server doesn't have information about the requested zone");
		}

		return new DomainResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), domain, username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapHead(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapHead(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException, RdapDatabaseException {
		DomainRequest request = null;
		try {
			request = new DomainRequest(Util.getRequestParams(httpRequest)[0]);
		} catch (InvalidValueException | ObjectNotFoundException e) {
			throw new ObjectNotFoundException("The RDAP server doesn't have information about the requested zone");
		}
		DomainService.existByName(request.getFullRequestValue());
		return new OkResult();
	}

	private class DomainRequest {

		private String fullRequestValue;

		private String domainName;

		private String zoneName;

		public DomainRequest(String requestValue) throws ObjectNotFoundException, InvalidValueException {
			super();
			if (requestValue.endsWith(".")) {
				requestValue = requestValue.substring(0, requestValue.length() - 1);
			}
			this.fullRequestValue = requestValue;

			// TODO validate if zone is handle by this server
			// if (RdapConfiguration.isReverseAddress(requestValue)) {
			// } else {
			// int indexOf = requestValue.indexOf('.');
			//
			// if (indexOf <= 0) {
			// throw new InvalidValueException("Zone", "DomainServlet",
			// "Domain");
			// }
			//
			// zoneName = requestValue.substring(indexOf + 1,
			// requestValue.length());
			// domainName = requestValue.substring(0, indexOf);
			// }
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