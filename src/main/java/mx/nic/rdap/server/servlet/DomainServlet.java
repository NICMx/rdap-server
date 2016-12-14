package mx.nic.rdap.server.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.exception.ObjectNotFoundException;
import mx.nic.rdap.db.model.DomainModel;
import mx.nic.rdap.db.model.ZoneModel;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.RdapServlet;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.result.DomainResult;
import mx.nic.rdap.server.result.OkResult;

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
			throws RequestHandleException, IOException, SQLException {
		DomainRequest request = null;
		try {
			request = new DomainRequest(Util.getRequestParams(httpRequest)[0]);
		} catch (InvalidValueException | ObjectNotFoundException e) {
			throw new ObjectNotFoundException("The RDAP server doesn't have information about the requested zone");
		}
		String userName = httpRequest.getRemoteUser();

		RdapResult result = null;

		try (Connection con = DatabaseSession.getRdapConnection()) {
			DomainDAO domain = new DomainDAO();
			try {
				domain = DomainModel.findByLdhName(request.getDomainName(), request.getZoneId(), con);
			} catch (InvalidValueException e) {
				throw new ObjectNotFoundException("The RDAP server doesn't have information about the requested zone");
			}

			result = new DomainResult(httpRequest.getHeader("Host"), httpRequest.getContextPath(), domain, userName);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapServlet#doRdapHead(javax.servlet.http.
	 * HttpServletRequest)
	 */
	@Override
	protected RdapResult doRdapHead(HttpServletRequest httpRequest)
			throws RequestHandleException, IOException, SQLException {
		DomainRequest request = null;
		try {
			request = new DomainRequest(Util.getRequestParams(httpRequest)[0]);
		} catch (InvalidValueException | ObjectNotFoundException e) {
			throw new ObjectNotFoundException("The RDAP server doesn't have information about the requested zone");
		}
		try (Connection con = DatabaseSession.getRdapConnection()) {
			DomainModel.existByLdhName(request.getDomainName(), request.getZoneId(), con);
		}
		return new OkResult();
	}

	private class DomainRequest {

		private String fullRequestValue;

		private String domainName;

		private String zoneName;

		private Integer zoneId;

		public DomainRequest(String requestValue) throws ObjectNotFoundException, InvalidValueException {
			super();
			if (requestValue.endsWith(".")) {
				requestValue = requestValue.substring(0, requestValue.length() - 1);
			}
			this.fullRequestValue = requestValue;

			DomainModel.validateDomainZone(requestValue);

			if (ZoneModel.isReverseAddress(requestValue)) {
				domainName = ZoneModel.getAddressWithoutArpaZone(requestValue);
				zoneId = ZoneModel.getZoneIdForArpaZone(requestValue);
				zoneName = ZoneModel.getZoneNameById(zoneId);
			} else {
				int indexOf = requestValue.indexOf('.');

				if (indexOf <= 0) {
					throw new InvalidValueException("Zone", "DomainServlet", "Domain");
				}

				zoneName = requestValue.substring(indexOf + 1, requestValue.length());
				domainName = requestValue.substring(0, indexOf);
				zoneId = ZoneModel.getIdByZoneName(zoneName);
			}
		}

		@SuppressWarnings("unused")
		public String getFullRequestValue() {
			return fullRequestValue;
		}

		public String getDomainName() {
			return domainName;
		}

		@SuppressWarnings("unused")
		public String getZoneName() {
			return zoneName;
		}

		public Integer getZoneId() {
			return zoneId;
		}

	}

}