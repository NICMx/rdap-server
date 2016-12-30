package mx.nic.rdap.server.result;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.catalog.RemarkType;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.RdapObject;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.db.struct.SearchResultStruct;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.UserInfo;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.operational.profile.OperationalProfileValidator;
import mx.nic.rdap.server.renderer.json.EntityJsonWriter;

/**
 * A result from an Entity search request.
 */
public class EntitySearchResult extends RdapResult {

	private List<Entity> entities;
	// The max number of results allowed for the user
	private Integer maxNumberOfResultsForUser;
	// Indicate is the search has more results than the answered to the user
	Boolean resultSetWasLimitedByUserConfiguration;

	public EntitySearchResult(String header, String contextPath, SearchResultStruct result, String userName)
			throws FileNotFoundException {
		notices = new ArrayList<Remark>();
		this.entities = new ArrayList<Entity>();
		this.userInfo = new UserInfo(userName);
		this.setMaxNumberOfResultsForUser(result.getSearchResultsLimitForUser());
		this.resultSetWasLimitedByUserConfiguration = result.getResultSetWasLimitedByUserConfiguration();
		for (RdapObject entity : result.getResults()) {
			EntityResult.addSelfLinks(header, contextPath, (Entity) entity);
			this.entities.add((Entity) entity);
		}
		validateResponse();
	}

	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Entity entity : entities) {
			arrB.add(EntityJsonWriter.getJson(entity, userInfo.isUserAuthenticated(), userInfo.isOwner(entity)));
		}
		builder.add("entitySearchResults", arrB);
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
		if (!RdapConfiguration.getServerProfile().equals(OperationalProfile.NONE)) {
			for (Entity entity : entities) {
				OperationalProfileValidator.validateEntityTel(entity);
				OperationalProfileValidator.validateEntityEvents(entity);
				if (entity.getEntities() != null && !entity.getEntities().isEmpty()) {
					for (Entity ent : entity.getEntities()) {
						OperationalProfileValidator.validateEntityTel(ent);
						OperationalProfileValidator.validateEntityEvents(ent);
					}
				}
			}
		}
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
