package mx.nic.rdap.server.servlet;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.UnprocessableEntityException;
import mx.nic.rdap.server.catalog.RequestSearchType;
import mx.nic.rdap.server.configuration.RdapConfiguration;

public class RdapSearchRequest {

	/**
	 * Regular expression to validate an incoming partial search request.
	 */
	private static final String PARTIAL_DOMAIN_SEARCH_REGEX = "([\\w-]*\\*?\\.)*([\\w-]*\\*?\\.?)";

	/**
	 * Compiled pattern of <code>PARTIAL_SEARCH_REGEX</code>.
	 */
	private static final Pattern PARTIAL_DOMAIN_SEARCH_PATTERN = Pattern.compile(PARTIAL_DOMAIN_SEARCH_REGEX);

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

	public static RdapSearchRequest getSearchRequest(HttpServletRequest request, boolean isEntityObject,
			String... parameters) throws RdapDataAccessException {
		if (request.getParameterMap().isEmpty()) {
			throw new BadRequestException(
					"The request must contain at least one of the following parameters: " + Arrays.asList(parameters));
		}
		RdapSearchRequest searchReq = new RdapSearchRequest();
		String searchTypeValue = request.getParameter(SEARCH_TYPE_KEY_PARAM);
		if (searchTypeValue == null) {
			searchTypeValue = SEARCHTYPE_PARTIAL_VALUE;
		}

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

		switch (searchTypeValue) {
		case SEARCHTYPE_REGEX_VALUE:
			searchReq.type = RequestSearchType.REGEX_SEARCH;
			break;
		default:
			searchReq.type = RequestSearchType.PARTIAL_SEARCH;
			break;
		}

		searchReq.validateSearchRequest(isEntityObject, parameters);

		return searchReq;
	}

	private void validateSearchRequest(boolean isEntityObject, String... validParameters)
			throws RdapDataAccessException {
		switch (this.type) {
		case PARTIAL_SEARCH:
			validatePartialSearchRequest(isEntityObject, validParameters);
			break;
		case REGEX_SEARCH:
			validateRegexSearchRequest(validParameters);
			break;
		default:
			break;
		}
	}

	private void validatePartialSearchRequest(boolean isEntityObject, String... validParameters)
			throws UnprocessableEntityException {
		validatePartialSearchValue(this.parameterValue, isEntityObject);
	}

	private void validateRegexSearchRequest(String... validParameters) throws RdapDataAccessException {
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
	 * @throws UnprocessableEntityException
	 */
	private static void validatePartialSearchValue(String valuePattern, boolean isEntity)
			throws UnprocessableEntityException {

		// Validate if the length of the pattern is valid
		if (valuePattern.length() < RdapConfiguration.getMinimumSearchPatternLength()) {
			throw new UnprocessableEntityException("Search pattern must be at least "
					+ RdapConfiguration.getMinimumSearchPatternLength() + " characters");
		}
		boolean partialSearch = false;
		partialSearch = valuePattern.contains("*");

		// Validate if is a valid partial search
		if (!partialSearch) {
			return;
		}

		if (RdapConfiguration.allowSearchWildcardsAnywhere()) {
			return;
		}

		// validates that asterisk are only in the end of the search pattern
		if (isEntity) {
			if (!valuePattern.endsWith("*")) {
				throw new UnprocessableEntityException(
						"Partial search can only have a wildcard at the end of the search");
			}
			int asteriskCount = valuePattern.length() - valuePattern.replaceAll("\\*", "").length();
			if (asteriskCount > 1) {
				throw new UnprocessableEntityException("Partial search can only have one wildcard");
			}
		} else {
			if (!PARTIAL_DOMAIN_SEARCH_PATTERN.matcher(valuePattern).matches()) {
				throw new UnprocessableEntityException(
						"Partial search can only have wildcards at the end of each label");
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
