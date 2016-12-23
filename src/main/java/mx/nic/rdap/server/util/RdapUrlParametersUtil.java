package mx.nic.rdap.server.util;

import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.server.RdapConfiguration;
import mx.nic.rdap.server.exception.RequestHandleException;

public class RdapUrlParametersUtil {

	/**
	 * Regular expression to validate an incoming partial search request.
	 */
	private static final String PARTIAL_DOMAIN_SEARCH_REGEX = "([\\w-]*\\*?\\.)*([\\w-]*\\*?\\.?)";

	/**
	 * Compiled pattern of <code>PARTIAL_SEARCH_REGEX</code>.
	 */
	private static final Pattern PARTIAL_DOMAIN_SEARCH_PATTERN = Pattern.compile(PARTIAL_DOMAIN_SEARCH_REGEX);

	private RdapUrlParametersUtil() {
		// Static function class
	}

	/**
	 * If the request's URI is /rdap/ip/192.0.2.0/24, then this returns
	 * ["192.0.2.0", "24"].
	 * 
	 * @param request
	 *            request you want the arguments from.
	 * @return request arguments.
	 * @throws RequestHandleException
	 *             <code>request</code> is not a valid RDAP URI.
	 * @throws UnsupportedEncodingException
	 */
	public static String[] getRequestParams(HttpServletRequest request)
			throws RequestHandleException, UnsupportedEncodingException {
		String[] labels = URLDecoder.decode(request.getRequestURI(), "UTF-8").split("/");

		if (labels.length < 4) {
			// TODO improve error message.
			throw new RequestHandleException(404, "I need more arguments than that. Try /rdap/sample/192.0.2.1");
		}

		// resourceType = labels[2];
		return Arrays.copyOfRange(labels, 3, labels.length);
	}

	public static void validateEntitySearchRequest(HttpServletRequest request, String... params)
			throws UnprocessableEntityException {
		validateSearchRequest(request, true, params);
	}

	public static void validateDomainNameSearchRequest(HttpServletRequest request, String... params)
			throws UnprocessableEntityException {
		validateSearchRequest(request, false, params);
	}

	/**
	 * Validate if the request is valid
	 * 
	 * @throws UnprocessableEntityException
	 */
	private static void validateSearchRequest(HttpServletRequest request, boolean isEntityObject, String... params)
			throws UnprocessableEntityException {
		// Only accept one parameter in the request
		if (request.getParameterMap().size() != 1) {
			throw new UnprocessableEntityException("The request must contain one parameter");
		}
		String parameter = request.getParameterNames().nextElement();
		String valuePattern = request.getParameter(parameter);
		validateSearchRequestParameters(parameter, params);
		validateSearchValue(valuePattern, isEntityObject);
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
		// Validating if is a partial search and if it is, only can contain
		// ASCII

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
}
