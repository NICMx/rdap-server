package mx.nic.rdap.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;
import mx.nic.rdap.server.exception.UnprocessableEntityException;

/**
 * Random miscellaneous functions useful anywhere.
 *
 * @author aleiva
 */
public class Util {

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
	 * Validate if the search parameters are valid
	 * 
	 * @param request
	 * @throws UnprocessableEntityException
	 */
	public static void validateSearchRequestParameters(HttpServletRequest request, String... params)
			throws UnprocessableEntityException {
		// Only accept one parameter in the request
		if (request.getParameterMap().size() != 1) {
			throw new UnprocessableEntityException("The request must contain one parameter");
		}
		// Validating if is a partial search and if it is, only can contain
		// ASCII
		String parameter = request.getParameterNames().nextElement();
		String value = request.getParameter(parameter);
		if (value.length() < RdapConfiguration.getMinimumSearchPatternLength()) {// Validate
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
		partialSearch = value.contains("*");
		if (partialSearch && value.compareTo(IDN.toASCII(value)) != 0) {// Validate
																		// if is
																		// a
																		// valid
																		// partial
																		// search
			throw new UnprocessableEntityException("Partial search must contain only ASCII values");
		}
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
	 * Validates if an ip address is valid
	 * 
	 * @param addr
	 * @return
	 * @throws MalformedRequestException
	 *             Throws error 400 Bad request
	 */
	public static void validateIpAddress(String addr) throws MalformedRequestException {
		try {
			InetAddress address = InetAddress.getByName(addr);
			if (address instanceof Inet6Address) {
				System.out.println("Is  v6");
			} else if (address instanceof Inet4Address) {
				System.out.println("Is  v4");
			}
		} catch (UnknownHostException e) {
			throw new MalformedRequestException("Requested ip is invalid.");
		}
	}
}
