package mx.nic.rdap.server.result;

import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.operational.profile.OperationalProfileValidator;
import mx.nic.rdap.server.renderer.json.DomainJsonWriter;

/**
 * A result from a Domain request
 */
public class DomainResult extends RdapResult {

	private DomainDAO domain;

	public DomainResult(String header, String contextPath, DomainDAO domain, String userName) {
		notices = new ArrayList<Remark>();
		this.domain = domain;
		this.userInfo = new UserInfo(userName);
		this.domain.addSelfLinks(header, contextPath);
		validateResponse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {

		return DomainJsonWriter.getJson(domain, userInfo.isUserAuthenticated(), userInfo.isOwner(domain));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#fillNotices()
	 */
	@Override
	public void fillNotices() {
		// At the moment, there is no notices for this request
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
		if (!RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE)) {
			OperationalProfileValidator.validateDomain(domain);
			// Point 1.5.18 of rdap operational profile by ICANN
			domain.getRemarks().add(Util.getEppInformationRemark());
			// Point 1.5.20 of rdap operational profile by ICANN
			domain.getRemarks().add(Util.getWhoisInaccuracyComplaintFormRemark());

		}
	}
}
