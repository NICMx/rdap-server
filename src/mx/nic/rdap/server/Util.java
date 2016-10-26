package mx.nic.rdap.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.exception.UnprocessableEntityException;
import mx.nix.rdap.core.catalog.Rol;

/**
 * Random miscellaneous functions useful anywhere.
 *
 * @author aleiva
 */
public class Util {

	// This regex match with XXX.XXX.# or XXX.# or #, where XXX. is 000 or 0 to
	// 255, and # is any integer number
	private static String IP4_GENERIC_REGEX = "(((0|1)?[0-9]{0,2}|2[0-4][0-9]|25[0-5])\\.){0,2}\\d*[^\\.]";

	// this regex will match with a valid IPv4 from 0.0.0.0 or 000.000.000.000
	// to 255.255.255.255
	private static String IP4_EXACT_REGEX = "(((0|1)?[0-9]{0,2}|2[0-4][0-9]|25[0-5])\\.){3}((0|1)?[0-9]{0,2}|2[0-4][0-9]|25[0-5])";

	private static String IP4_REGEX = "(" + IP4_EXACT_REGEX + "|" + IP4_GENERIC_REGEX + ")";
	private static Pattern IP4_PATTERN = Pattern.compile(IP4_REGEX);

	/**
	 * Loads the properties configuration file
	 * <code>META-INF/fileName.properties</code> and returns it.
	 * 
	 * @param fileName
	 *            name of the configuration file you want to load.
	 * @return configuration requested.
	 * @throws IOException
	 *             Error attempting to read the configuration out of the
	 *             classpath.
	 */
	public static Properties loadProperties(String fileName) throws IOException {
		fileName = "META-INF/" + fileName + ".properties";
		Properties result = new Properties();
		try (InputStream configStream = Util.class.getClassLoader().getResourceAsStream(fileName)) {
			result.load(configStream);
		}
		return result;
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
			throw new RequestHandleException(404, "I need more arguments than that. Try /rdap/sample/192.0.2.1");
		}

		// resourceType = labels[2];
		return Arrays.copyOfRange(labels, 3, labels.length);
	}

	/**
	 * Validate if the request is valid
	 * 
	 * @param request
	 * @param params
	 * @throws UnprocessableEntityException
	 */
	public static void validateSearchRequest(HttpServletRequest request, String... params)
			throws UnprocessableEntityException {
		// Only accept one parameter in the request
		if (request.getParameterMap().size() != 1) {
			throw new UnprocessableEntityException("The request must contain one parameter");
		}
		String parameter = request.getParameterNames().nextElement();
		String pattern = request.getParameter(parameter);
		validateSearchRequestParameters(parameter, params);
		validateSearchPatterns(pattern);
	}

	/**
	 * Validate if the search parameters are valid
	 * 
	 * @param request
	 * @throws UnprocessableEntityException
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
	 * @param pattern
	 * @throws UnprocessableEntityException
	 */
	public static void validateSearchPatterns(String pattern) throws UnprocessableEntityException {
		// Validating if is a partial search and if it is, only can contain
		// ASCII

		if (pattern.length() < RdapConfiguration.getMinimumSearchPatternLength()) {// Validate
																					// if
																					// the
																					// lenght
																					// of
																					// the
																					// pattern
																					// is
																					// valid
			throw new UnprocessableEntityException("Search pattern must be at least "
					+ RdapConfiguration.getMinimumSearchPatternLength() + " characters");
		}
		boolean partialSearch = false;
		partialSearch = pattern.contains("*");
		if (partialSearch && pattern.compareTo(IDN.toASCII(pattern)) != 0) {// Validate
			// if is
			// a
			// valid
			// partial
			// search
			throw new UnprocessableEntityException("Partial search must contain only ASCII values");
		}
	}

	/**
	 * Validates if IpAddress is valid
	 * 
	 * @param ipAddress
	 * @throws MalformedRequestException
	 */
	public static void validateIpAddress(String ipAddress) throws MalformedRequestException {
		// if the ipAddress contains ':' then InetAddress will try to parse it
		// like IPv6 address without doing a lookup to DNS.
		if (ipAddress.contains(":")) {
			try {
				InetAddress.getByName(ipAddress);
			} catch (UnknownHostException e) {
				throw new MalformedRequestException("Requested ip is invalid.");
			}
			return;
		}

		if (!IP4_PATTERN.matcher(ipAddress).matches()) {
			throw new MalformedRequestException("Requested ip is invalid.");
		}

		try {
			InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			throw new MalformedRequestException("Requested ip is invalid.");
		}

	}

	/**
	 * Get the max search results number allowed for the user
	 * 
	 * @return
	 */
	public static Integer getMaxNumberOfResultsForUser() {
		boolean isAuthenticatedUser = isAuthenticatedUser();
		if (isAuthenticatedUser) {
			Integer limit = getAuthenticatedUserMaxSearchResults();
			if (limit != null)
				return limit;
			else
				return RdapConfiguration.getMaxNumberOfResultsForAuthenticatedUser();
		}
		return RdapConfiguration.getMaxNumberOfResultsForUnauthenticatedUser();
	}

	/**
	 * Get the max search results number allowed for the authenticated user
	 * 
	 * @return
	 */
	private static Integer getAuthenticatedUserMaxSearchResults() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * True if the user is authenticated
	 * 
	 * @return
	 */
	public static boolean isAuthenticatedUser() {
		// TODO: get from request if the
		// user is authenticated
		return false;
	}

	/**
	 * Get the roles that can own a object
	 * 
	 * @return
	 */
	public static List<Rol> getConfiguratedOwnerRols() {
		List<Rol> rols = new ArrayList<Rol>();
		return rols;
	}
}
