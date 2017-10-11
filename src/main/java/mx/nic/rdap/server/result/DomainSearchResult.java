package mx.nic.rdap.server.result;

import java.util.ArrayList;
import java.util.List;

import mx.nic.rdap.core.catalog.RemarkType;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.renderer.object.SearchResponse;

/**
 * A result from a Domain search request
 */
public class DomainSearchResult extends RdapSearchResult {

	private List<Domain> domains;
	// The max number of results allowed for the user
	private Integer maxNumberOfResultsForUser;
	// Indicate is the search has more results than the answered to the user
	private Boolean resultSetWasLimitedByUserConfiguration;
	
	public DomainSearchResult(String header, String contextPath, SearchResultStruct<Domain> result, String userName) {
		notices = new ArrayList<Remark>();
		this.domains = new ArrayList<Domain>();
		this.setMaxNumberOfResultsForUser(result.getSearchResultsLimitForUser());
		this.resultSetWasLimitedByUserConfiguration = result.getResultSetWasLimitedByUserConfiguration();
		for (Domain domain : result.getResults()) {
			DomainResult.addSelfLinks(header, contextPath, domain);
			this.domains.add(domain);
		}
		setRdapObjects(domains);
		fillNotices();
		
		setResultType(ResultType.DOMAINS);
		SearchResponse<Domain> searchResponse = new SearchResponse<>();
		searchResponse.setNotices(getNotices());
		searchResponse.setRdapObjects(domains);
		searchResponse.setRdapConformance(new ArrayList<>());
		searchResponse.getRdapConformance().add("rdap_level_0");
		
		setRdapResponse(searchResponse);
		
		
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#fillNotices()
	 */
	@Override
	public void fillNotices() {
		// At the moment, we only add the privacy notice
		if (domains.size() < maxNumberOfResultsForUser && resultSetWasLimitedByUserConfiguration) {
			notices.add(new Remark(RemarkType.RESULT_SET_UNEXPLAINABLE));
		} else if (domains.size() == maxNumberOfResultsForUser) {
			notices.add(new Remark(RemarkType.RESULT_SET_AUTHORIZATION));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
		// Nothing to validate
	}

	public List<Domain> getDomains() {
		return domains;
	}

	public void setDomains(List<Domain> domains) {
		this.domains = domains;
	}

	public Integer getMaxNumberOfResultsForUser() {
		return maxNumberOfResultsForUser;
	}

	public void setMaxNumberOfResultsForUser(Integer maxNumberOfResultsForUser) {
		this.maxNumberOfResultsForUser = maxNumberOfResultsForUser;
	}
	
}
