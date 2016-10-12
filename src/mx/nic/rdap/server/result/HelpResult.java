package mx.nic.rdap.server.result;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.db.RemarkDAO;

/**
 * 
 * @author dalpuche
 *
 */
public class HelpResult implements RdapResult {

	List<RemarkDAO> notices;

	public HelpResult() {
		notices = new ArrayList<RemarkDAO>();
		readNoticesFromFiles();

	}

	private void readNoticesFromFiles() {
		// TODO: Read files and build notices
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		for (RemarkDAO notice : notices) {
			builder.add(notice.toJson());
		}
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
		objectBuilder.add("notices", builder.build());
		return objectBuilder.build();
	}

}
