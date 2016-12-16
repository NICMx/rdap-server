package mx.nic.rdap.server.result;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.RdapResult;

/**
 * A result from a exception generated in a request.
 */
public class ExceptionResult extends RdapResult {

	private final static Logger logger = Logger.getLogger(ExceptionResult.class.getName());

	private String errorCode;
	private String errorTitle;
	private String errorDescription;

	/**
	 * Read the request and fill the error data
	 * 
	 * @param httpRequest
	 */
	public ExceptionResult(HttpServletRequest httpRequest) {
		notices = new ArrayList<Remark>();
		errorCode = httpRequest.getAttribute("javax.servlet.error.status_code").toString();
		if (errorCode != null) {
			switch (errorCode) {
			case "401":
				errorTitle = "Forbidden request";
				errorDescription = "Must loggin to process the request";
				break;
			case "403":
				errorTitle = "Forbidden request";
				errorDescription = httpRequest.getAttribute("javax.servlet.error.message").toString()
						+ ". Verify User role";
				break;
			case "404":
				errorTitle = "Object not found";
				errorDescription = httpRequest.getAttribute("javax.servlet.error.message").toString();
				break;
			case "422":
				errorTitle = "Unprocessable Entity";
				errorDescription = httpRequest.getAttribute("javax.servlet.error.message").toString();
				break;
			case "500":
				errorTitle = "Internal server error";
				errorDescription = httpRequest.getAttribute("javax.servlet.error.message").toString();
				break;
			}
			logger.log(Level.WARNING,
					errorCode + ":" + httpRequest.getAttribute("javax.servlet.error.message").toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObjectBuilder object = Json.createObjectBuilder();
		if (errorCode != null) {
			object.add("errorCode", errorCode);
		}
		if (errorTitle != null) {
			object.add("title", errorTitle);
		}
		if (errorCode != null && errorDescription != null) {
			if (errorCode.compareTo("500") != 0)
				object.add("description", errorDescription);
		}
		return object.build();
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

	/* (non-Javadoc)
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
	}
}
