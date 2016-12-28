package mx.nic.rdap.server;

import java.net.IDN;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.server.catalog.RequestSearchType;

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

	private static final String SEARCHTYPE_REGEX = "regex";
	/**
	 * Type of search.
	 */
	private RequestSearchType type;

	/**
	 * Parameter value of the parameter 'searchtype'
	 */
	private String searchTypeValue;

	private String parameterName;

	private String parameterValue;

	public static RdapSearchRequest getSearchRequest(HttpServletRequest request, boolean isEntityObject,
			String... parameters) throws UnprocessableEntityException {
		int paramSize = request.getParameterMap().size();
		RdapSearchRequest searchReq = new RdapSearchRequest();
		Enumeration<String> parameterNames = request.getParameterNames();
		if (paramSize == 1) {
			searchReq.type = RequestSearchType.PARTIAL_SEARCH;

			searchReq.parameterName = parameterNames.nextElement();
			searchReq.parameterValue = request.getParameter(searchReq.getParameterName());
		} else if (paramSize == 2) {
			do {
				String paramName = parameterNames.nextElement();
				if (paramName.equals(SEARCH_TYPE_KEY_PARAM)) {
					searchReq.searchTypeValue = request.getParameter(SEARCH_TYPE_KEY_PARAM);
				} else {
					searchReq.parameterName = paramName;
					searchReq.parameterValue = request.getParameter(paramName);
				}
			} while (parameterNames.hasMoreElements());

			if (searchReq.searchTypeValue == null) {
				throw new UnprocessableEntityException(
						"The request must contain a '" + SEARCH_TYPE_KEY_PARAM + "' parameter.");
			}

			switch (searchReq.searchTypeValue) {
			case SEARCHTYPE_REGEX:
				searchReq.type = RequestSearchType.REGEX_SEARCH;
				break;
			default:
				throw new UnprocessableEntityException("Unimplemented searchtype : " + searchReq.searchTypeValue);
			}

		} else {
			throw new UnprocessableEntityException("The request must contain one or two parameter");
		}

		searchReq.validateSearchRequest(isEntityObject, parameters);

		return searchReq;
	}

	private void validateSearchRequest(boolean isEntityObject, String... validParameters)
			throws UnprocessableEntityException {
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
		validateSearchRequestParameters(this.parameterName, validParameters);
		validateSearchValue(this.parameterValue, isEntityObject);
	}

	private void validateRegexSearchRequest(String... validParameters) throws UnprocessableEntityException {
		validateSearchRequestParameters(this.parameterName, validParameters);
		try {
			byte[] decode = Base64.getUrlDecoder().decode(this.parameterValue);
			this.parameterValue = new String(decode, StandardCharsets.UTF_8);
		} catch (IllegalArgumentException e) {
			throw new UnprocessableEntityException(
					"The parameter value must be a base64url encoded POSIX extended regular expression");
		}
	}

	/**
	 * Validate if the search parameters are valid
	 */
	public static void validateSearchRequestParameters(String parameter, String... params)
			throws UnprocessableEntityException {
		// Validate if the parameter if a valid parameter for the request
		String validParametersMessage = "";
		for (String paramName : params) {
			if (paramName.compareTo(parameter) == 0) {
				return;
			}
			if (!validParametersMessage.isEmpty()) {
				validParametersMessage = validParametersMessage.concat(" or " + paramName);
			} else {
				validParametersMessage = paramName;
			}
		}
		throw new UnprocessableEntityException("Valid parameters:" + validParametersMessage);
	}

	/**
	 * Validate if the search patterns are valid
	 * 
	 * @param valuePattern
	 * @throws UnprocessableEntityException
	 */
	public static void validateSearchValue(String valuePattern, boolean isEntity) throws UnprocessableEntityException {

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

		// Validate if a partial search value has only ASCII values.
		if (valuePattern.compareTo(IDN.toASCII(valuePattern)) != 0) {
			throw new UnprocessableEntityException("Partial search must contain only ASCII values");
		}

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

	public String getSearchTypeValue() {
		return searchTypeValue;
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

	public void setSearchTypeValue(String searchTypeValue) {
		this.searchTypeValue = searchTypeValue;
	}

}
