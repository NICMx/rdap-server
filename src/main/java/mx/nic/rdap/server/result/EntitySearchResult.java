package mx.nic.rdap.server.result;

import java.util.ArrayList;
import java.util.List;

import mx.nic.rdap.core.catalog.RemarkType;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.renderer.object.SearchResponse;

/**
 * A result from an Entity search request.
 */
public class EntitySearchResult extends RdapSearchResult {

	private List<Entity> entities;
	// The max number of results allowed for the user
	private Integer maxNumberOfResultsForUser;
	// Indicate is the search has more results than the answered to the user
	private Boolean resultSetWasLimitedByUserConfiguration;
	
	public EntitySearchResult(String header, String contextPath, SearchResultStruct<Entity> result, String userName) {
		notices = new ArrayList<Remark>();
		this.entities = new ArrayList<Entity>();
		this.setMaxNumberOfResultsForUser(result.getSearchResultsLimitForUser());
		this.resultSetWasLimitedByUserConfiguration = result.getResultSetWasLimitedByUserConfiguration();
		for (Entity entity : result.getResults()) {
			EntityResult.addSelfLinks(header, contextPath, entity);
			this.entities.add(entity);
		}
		setRdapObjects(entities);
		validateResponse();
		fillNotices();
		
		setResultType(ResultType.ENTITIES);
		SearchResponse<Entity> searchResponse = new SearchResponse<>();
		searchResponse.setNotices(getNotices());
		searchResponse.setRdapObjects(getEntities());
		searchResponse.setRdapConformance(new ArrayList<>());
		searchResponse.getRdapConformance().add("rdap_level_0");
		
		setEntities(entities);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#fillNotices()
	 */
	@Override
	public void fillNotices() {
		// At the moment, we only add the privacy notice
		if (entities.size() < maxNumberOfResultsForUser && resultSetWasLimitedByUserConfiguration) {
			notices.add(new Remark(RemarkType.RESULT_SET_UNEXPLAINABLE));
		} else if (entities.size() == maxNumberOfResultsForUser) {
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

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}

	public Integer getMaxNumberOfResultsForUser() {
		return maxNumberOfResultsForUser;
	}

	public void setMaxNumberOfResultsForUser(Integer maxNumberOfResultsForUser) {
		this.maxNumberOfResultsForUser = maxNumberOfResultsForUser;
	}

	public Boolean getResultSetWasLimitedByUserConfiguration() {
		return resultSetWasLimitedByUserConfiguration;
	}

	public void setResultSetWasLimitedByUserConfiguration(Boolean resultSetWasLimitedByUserConfiguration) {
		this.resultSetWasLimitedByUserConfiguration = resultSetWasLimitedByUserConfiguration;
	}
	
}
