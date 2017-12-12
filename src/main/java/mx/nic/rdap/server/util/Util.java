package mx.nic.rdap.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.subject.Subject;

import mx.nic.rdap.db.exception.http.BadRequestException;
import mx.nic.rdap.db.exception.http.HttpException;
import mx.nic.rdap.db.exception.http.NotFoundException;

/**
 * Random miscellaneous functions useful anywhere.
 */
public class Util {

	/**
	 * If the request's URI is /rdap/ip/192.0.2.0/24, then this returns
	 * ["192.0.2.0", "24"].
	 * 
	 * @param request
	 *            request you want the arguments from.
	 * @param maxParamsExpected
	 *            maximum number of parameters expected, negative value means indefinite
	 * @return request arguments.
	 * @throws HttpException
	 *             <code>request</code> is not a valid RDAP URI.
	 */
	public static String[] getRequestParams(HttpServletRequest request, int maxParamsExpected) throws HttpException {
		try {
			URLDecoder.decode(request.getRequestURI(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new BadRequestException("The request does not appear to be UTF-8 encoded.", e);
		}

		String pathInfo = request.getPathInfo();
		if (pathInfo == null || pathInfo.equals("/")) {
			throw new NotFoundException("The request does not appear to be a valid RDAP URI. " //
					+ "I might need more arguments than that.");
		}
		// Ignores the first "/"
		String[] requestParams = pathInfo.substring(1).split("/");
		// If maxParamsExpected is sent then validate against its value
		if (maxParamsExpected >= 0 && requestParams.length > maxParamsExpected) {
			throw new NotFoundException(request.getRequestURI());
		}
		return requestParams;
	}

	/**
	 * Return Server's URL considering that the server isn't at the "front" (eg. uses a proxy, or a balancer)
	 * 
	 * @param httpRequest
	 *            Request received
	 * @return Server's URL
	 */
	public static String getServerUrl(HttpServletRequest httpRequest) {
		String serverUrl = "{0}://" + httpRequest.getHeader("Host");
		// First check by optional header "Forwarded" and parameter "proto"
		// See https://tools.ietf.org/html/rfc7239#section-5.4 
		if (!isStringEmpty(httpRequest.getHeader("Forwarded"))) {
			String tmpString = httpRequest.getHeader("Forwarded").replaceAll("\\s+", "");
			String[] parameters = tmpString.split(";");
			for (String parameter : parameters) {
				if (parameter.matches("(?i:^(proto=).+)")) {
					return MessageFormat.format(serverUrl, parameter.split("=")[1]);
				}
			}
		}
		// "Forwarded: proto" not present, check for "X-Forwarded-Proto"
		if (!isStringEmpty(httpRequest.getHeader("X-Forwarded-Proto"))) {
			return MessageFormat.format(serverUrl, httpRequest.getHeader("X-Forwarded-Proto"));
		}
		// Headers not present, use servlet function
		return MessageFormat.format(serverUrl, httpRequest.getScheme());
	}

	/**
	 * Get the username if it's authenticated
	 * 
	 * @param subject
	 *            subject from the session
	 * @return the authenticated username, null if there's no user authenticated
	 */
	public static String getUsername(Subject subject) {
		return subject.isAuthenticated() ? subject.getPrincipal().toString() : null;
	}

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
			if (configStream != null) {
				result.load(configStream);
			}
		}
		return result;
	}

	/**
	 * Checks whether the String is null or empty (uses the trim() function)
	 * 
	 * @param str
	 *            String to check
	 * @return <code>true</code> if String is empty, <code>false</code> otherwise
	 */
	public static boolean isStringEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
}
