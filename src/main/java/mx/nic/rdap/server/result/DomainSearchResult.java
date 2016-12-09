/**
 * 
 */
package mx.nic.rdap.server.result;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.catalog.RemarkType;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.renderer.json.DomainParser;

/**
 * A result from a Domain search request
 */
public class DomainSearchResult extends RdapResult {

	private List<DomainDAO> domains;
	//The max number of results allowed for the user
	private Integer maxNumberOfResultsForUser;
	//Indicate is the search has more results than the answered to the user
	Boolean resultSetWasLimitedByUserConfiguration;


	public DomainSearchResult(String header, String contextPath, SearchResultStruct result, String userName) {
		notices = new ArrayList<Remark>();
		this.domains =new ArrayList<DomainDAO>();
		this.userInfo = new UserInfo(userName);
		this.setMaxNumberOfResultsForUser(result.getSearchResultsLimitForUser());
		this.resultSetWasLimitedByUserConfiguration=result.getResultSetWasLimitedByUserConfiguration();
		for (RdapObject domain : result.getResults()) {
			DomainDAO dao=(DomainDAO)domain;
			domains.add(dao);
			dao.addSelfLinks(header, contextPath);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Domain domain : domains) {
			arrB.add(DomainParser.getJson(domain, userInfo.isUserAuthenticated(), userInfo.isOwner(domain)));
		}

		builder.add("domainSearchResults", arrB);
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#fillNotices()
	 */
	@Override
	public void fillNotices() {
		// At the moment, we only add the privacy notice
		if (domains.size()<maxNumberOfResultsForUser&&resultSetWasLimitedByUserConfiguration) {
			notices.add(new Remark(RemarkType.RESULT_SET_UNEXPLAINABLE));
		}else if (domains.size()==maxNumberOfResultsForUser) {
			notices.add(new Remark(RemarkType.RESULT_SET_AUTHORIZATION));
		}
	}

	public List<DomainDAO> getDomains() {
		return domains;
	}

	public void setDomains(List<DomainDAO> domains) {
		this.domains = domains;
	}


	public Integer getMaxNumberOfResultsForUser() {
		return maxNumberOfResultsForUser;
	}

	public void setMaxNumberOfResultsForUser(Integer maxNumberOfResultsForUser) {
		this.maxNumberOfResultsForUser = maxNumberOfResultsForUser;
	}

}
