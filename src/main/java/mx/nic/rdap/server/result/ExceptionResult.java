package mx.nic.rdap.server.result;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.renderer.object.ExceptionResponse;

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
		Object objectCode = httpRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		if (objectCode != null) {
			errorCode = objectCode.toString();
		} else {
			return;
		}
		
		boolean logWarning = true;
		Object objectMessage = httpRequest.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		String localMessage = objectMessage != null ? objectMessage.toString() : null;
		switch (errorCode) {
		case "400":
			errorDescription = localMessage;
			break;
		case "401":
			errorTitle = "Forbidden request";
			errorDescription = "Must log in to process the request";
			if (localMessage != null && !localMessage.trim().isEmpty()) {
				errorDescription = localMessage + ". " + errorDescription;
			}
			break;
		case "403":
			errorTitle = "Forbidden request";
			errorDescription = localMessage + ". Verify User role";
			break;
		case "404":
			errorTitle = "Object not found";
			errorDescription = localMessage;
			break;
		case "422":
			errorTitle = "Unprocessable HTTP Entity";
			errorDescription = localMessage;
			break;
		case "500":
			errorTitle = "Internal server error";
			errorDescription = localMessage;
			// The error wasn't "manually" sent, a.k.a is unexpected
			Object errorException = httpRequest.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
			if (errorException != null) {
				Throwable throwable = (Throwable) errorException;
				logger.log(Level.SEVERE, throwable.getMessage(), throwable);
				logWarning = false;
			}
			break;
		default:
			// At least get the description, if there's one
			errorDescription = localMessage;
			break;
		}
		if (logWarning) {
			logger.log(Level.WARNING, "Returned code " + errorCode + ": " + errorDescription);
		}
		
		ExceptionResponse response = new ExceptionResponse(errorDescription, errorCode, errorTitle);
		response.setRdapConformance(new ArrayList<>());
		response.getRdapConformance().add("rdap_level_0");
		setRdapResponse(response);
		setResultType(ResultType.EXCEPTION);
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}
	
	public String getErrorTitle() {
		return errorTitle;
	}
	
}
