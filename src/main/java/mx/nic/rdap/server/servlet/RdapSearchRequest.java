package mx.nic.rdap.server.servlet;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.UnprocessableEntityException;
import mx.nic.rdap.server.catalog.RequestSearchType;
import mx.nic.rdap.server.configuration.RdapConfiguration;

public class RdapSearchRequest {

	/**
	 * Parameter name for a search type.
	 */
	private static final String SEARCH_TYPE_KEY_PARAM = "searchtype";

	private static final String SEARCHTYPE_REGEX_VALUE = "regex";
	private static final String SEARCHTYPE_PARTIAL_VALUE = "partial";

	/**
	 * Type of search.
	 */
	private RequestSearchType type;

	private String parameterName;

	private String parameterValue;

	/**
	 * Return a {@link RdapSearchRequest} created from the {@link HttpServletRequest} sent, if its
	 * values are valid.
	 * 
	 * @param request
	 *            request received by the server
	 * @param isEntityObject
	 *            indicates if the searched object is an Entity (used for validations)
	 * @param isIp
	 *            indicates if the parameter of the request is an IP address (used for validations)
	 * @param parameters
	 *            list of query parameters allowed for the request
	 * @return new instance of a {@link RdapSearchRequest}
	 * @throws RdapDataAccessException if an error is detected
	 */
	public static RdapSearchRequest getSearchRequest(HttpServletRequest request, boolean isEntityObject,
			boolean isIp, String... parameters) throws RdapDataAccessException {
		if (request.getParameterMap().isEmpty()) {
			throw new BadRequestException(
					"The request must contain at least one of the following parameters: " + Arrays.asList(parameters));
		}
		RdapSearchRequest searchReq = new RdapSearchRequest();

		boolean hasValidParameter = false;
		for (String parameter : parameters) {
			String paramValue = request.getParameter(parameter);
			if (paramValue == null) {
				continue;
			}
			if (hasValidParameter) {
				throw new UnprocessableEntityException(
						"The request must contain only one of the following parameters: " + Arrays.asList(parameters));
			}
			hasValidParameter = true;
			searchReq.parameterName = parameter;
			searchReq.parameterValue = paramValue.trim();
		}

		if (searchReq.parameterName == null) {
			throw new BadRequestException(
					"The request must contain at least one of the following parameters: " + Arrays.asList(parameters));
		}

		if (RdapConfiguration.allowRegexSearches()) {
			String searchTypeValue = request.getParameter(SEARCH_TYPE_KEY_PARAM);
			if (searchTypeValue == null) {
				searchTypeValue = SEARCHTYPE_PARTIAL_VALUE;
			}
			switch (searchTypeValue) {
			case SEARCHTYPE_REGEX_VALUE:
				searchReq.type = RequestSearchType.REGEX_SEARCH;
				break;
			default:
				searchReq.type = RequestSearchType.PARTIAL_SEARCH;
				break;
			}
		} else {
			searchReq.type = RequestSearchType.PARTIAL_SEARCH;
		}

		searchReq.validateSearchRequest(isEntityObject, isIp);

		return searchReq;
	}

	private void validateSearchRequest(boolean isEntityObject, boolean isIp)
			throws RdapDataAccessException {
		switch (this.type) {
		case PARTIAL_SEARCH:
			validatePartialSearchRequest(isEntityObject, isIp);
			break;
		case REGEX_SEARCH:
			validateRegexSearchRequest();
			break;
		default:
			break;
		}
	}

	private void validatePartialSearchRequest(boolean isEntityObject, boolean isIp)
			throws UnprocessableEntityException {
		validatePartialSearchValue(this.parameterValue, isEntityObject, isIp);
	}

	private void validateRegexSearchRequest() throws RdapDataAccessException {
		try {
			byte[] decode = Base64.getUrlDecoder().decode(this.parameterValue);
			this.parameterValue = new String(decode, StandardCharsets.UTF_8);
		} catch (IllegalArgumentException e) {
			throw new BadRequestException(
					"The parameter value must be a base64url encoded POSIX extended regular expression");
		}
	}

	/**
	 * Validate if the search patterns are valid
	 * 
	 * @param valuePattern
	 *            search pattern to validate
	 * @param isEntity
	 *            indicates if the searched object is an Entity
	 * @param isIp
	 *            indicates if the parameter of the request is an IP address
	 * @throws UnprocessableEntityException
	 */
	private static void validatePartialSearchValue(String valuePattern, boolean isEntity, boolean isIp)
			throws UnprocessableEntityException {

		// Validate if the length of the pattern is valid
		validateMinimumPatternLength(valuePattern, "Search pattern must be at least "
				+ RdapConfiguration.getMinimumSearchPatternLength() + " characters");

		// Validate if it's a partial search
		if (!valuePattern.contains("*")) {
			return;
		}
		
		// Consecutive wildcards are handled as one; validate again, if the exception is raised send a different message
		valuePattern = valuePattern.replaceAll("(\\*)+", "\\*");
		validateMinimumPatternLength(valuePattern, "Search pattern must be at least "
				+ RdapConfiguration.getMinimumSearchPatternLength() + " characters, "
				+ "consecutive wildcards '*' are treated as only one character. Pattern received: " + valuePattern);

		// Validate number of wildcards and that wildcard comes at the end of the search pattern/labels (depending on configuration)
		if (isEntity) {
			validateWildcardUse(valuePattern, null, "Partial search can only have a wildcard at the end of the search");
		} else if (isIp) {
			// Validate that the wildcard comes at the end of each octet/field
			validateWildcardUse(valuePattern, "(\\.)|(:)", "Partial search can only have a wildcard at the end of each octet/field");
		} else {
			validateWildcardUse(valuePattern, "\\.", "Partial search can only have wildcards at the end of each label");
		}
	}
	
	/**
	 * Validates the minimum pattern length allowed based on {@link RdapConfiguration#getMinimumSearchPatternLength()}
	 * 
	 * @param value
	 *            Value to validate
	 * @param errorMessage
	 *            Error message to send in case of exception
	 * @throws UnprocessableEntityException if the value length is larger than expected
	 */
	private static void validateMinimumPatternLength(String value, String errorMessage) throws UnprocessableEntityException {
		if (value.length() < RdapConfiguration.getMinimumSearchPatternLength()) {
			throw new UnprocessableEntityException(errorMessage);
		}
	}
	
	/**
	 * Validate use of wildcard: number of wildcards allowed per label
	 * (based on {@link RdapConfiguration#allowMultipleWildcards()}
	 * and if wilcard comes at the end of each label or value sent 
	 * (based on {@link RdapConfiguration#allowSearchWildcardAnywhere()}).
	 * If <code>splitRegex</code> is sent, the <code>value</code> will be splitted using this regex
	 * and each element/label will be validated; if not sent (null or empty) the validation will be done directly to
	 * <code>value<code>.
	 * 
	 * @param value
	 *            Value to validate
	 * @param splitRegex
	 *            Regex used to split the <code>value</code> sent
	 * @param badEndUsageErrorMessage
	 *            Error message returned when an exception related to bad usage of the wildcard at the end of label is raised 
	 * @throws UnprocessableEntityException if the value or labels don't have the wildcard at the end
	 */
	private static void validateWildcardUse(String value, String splitRegex, String badEndUsageErrorMessage)
		throws UnprocessableEntityException {
		if (!RdapConfiguration.allowMultipleWildcards()) {
			if (splitRegex != null && !splitRegex.isEmpty()) {
				// Regex sent, validate each label
				String[] labels = value.split(splitRegex);
				for (String label : labels) {
					if (label.contains("*")) {
						int asteriskCount = label.length() - label.replaceAll("\\*", "").length();
						if (asteriskCount > 1) {
							throw new UnprocessableEntityException("Partial search can only have one wildcard at each label");
						}
						if (!RdapConfiguration.allowSearchWildcardAnywhere() && !label.endsWith("*")) {
							throw new UnprocessableEntityException(badEndUsageErrorMessage);
						}
					}
				}
			} else {
				// Regex not sent, validate the value
				if (value.contains("*")) {
					int asteriskCount = value.length() - value.replaceAll("\\*", "").length();
					if (asteriskCount > 1) {
						throw new UnprocessableEntityException("Partial search can only have one wildcard");
					}
					if (!RdapConfiguration.allowSearchWildcardAnywhere() && !value.endsWith("*")) {
						throw new UnprocessableEntityException(badEndUsageErrorMessage);
					}
				}
			}
		}
	}

	private RdapSearchRequest() {
		// No Code
	}

	public RequestSearchType getType() {
		return type;
	}

	public String getParameterName() {
		return parameterName;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setType(RequestSearchType type) {
		this.type = type;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

}
