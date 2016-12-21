package mx.nic.rdap.server.result;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.PrivacyUtil;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.renderer.json.RemarkJsonWriter;

/**
 * A Result from a help request.
 */
public class HelpResult extends RdapResult {

	private static List<Remark> notices = new ArrayList<>();
	private static String helpFolderPath;

	public HelpResult(ServletContext servletContext) throws FileNotFoundException {
		helpFolderPath = servletContext.getRealPath(File.separator) + "\\WEB-INF\\help\\";
		if (notices == null || notices.isEmpty()) {
			notices = Util.readNoticesFromFiles(helpFolderPath);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
		JsonArray jsonArray = RemarkJsonWriter.getJsonArray(notices, true, true,
				PrivacyUtil.getEntityRemarkPrivacySettings(), PrivacyUtil.getEntityLinkPrivacySettings());
		objectBuilder.add("notices", jsonArray);
		return objectBuilder.build();
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
	}

}
