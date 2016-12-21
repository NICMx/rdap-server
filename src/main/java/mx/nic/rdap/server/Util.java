package mx.nic.rdap.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.IDN;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.core.catalog.RemarkType;
import mx.nic.rdap.core.catalog.Rol;
import mx.nic.rdap.core.catalog.Status;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.core.exception.UnprocessableEntityException;
import mx.nic.rdap.db.RemarkDAO;
import mx.nic.rdap.db.RemarkDescriptionDAO;
import mx.nic.rdap.db.exception.InvalidadDataStructure;
import mx.nic.rdap.db.model.RdapUserModel;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.exception.MalformedRequestException;
import mx.nic.rdap.server.exception.RequestHandleException;

/**
 * Random miscellaneous functions useful anywhere.
 */
public class Util {

	//
	/**
	 * This regex string match with ###.###.###.n or ###.###.n or #.n or n,
	 * where ###. is 000 or 0 to 255, and n is any integer number
	 */
	private static String IP4_GENERIC_REGEX = "(((0|1)?[0-9]{0,2}|2[0-4][0-9]|25[0-5])\\.){0,3}\\d*[^\\.]";

	/**
	 * Compiled pattern of <code>IP4_GENERIC_REGEX<code>
	 */
	private static Pattern IP4_GENERIC_PATTERN = Pattern.compile(IP4_GENERIC_REGEX);

	private static final BigInteger FIRST_OCTECT_LIMIT = new BigInteger("4294967295"); // 0xFFFF_FFFF
	private static final BigInteger SECOND_OCTECT_LIMIT = new BigInteger(0xFF_FFFF + "");// 16777215
	private static final BigInteger THIRD_OCTECT_LIMIT = new BigInteger(0xFFFF + "");// 65535
	private static final BigInteger FOURTH_OCTECT_LIMIT = new BigInteger(0xFF + ""); // 255
	private static final int IP_ADDRESS_ARRAY_SIZE = 4;

	private Integer authenticatedMaxUserResultLimit = null;

	/**
	 * Regular expression to validate an incoming partial search request.
	 */
	private static final String PARTIAL_DOMAIN_SEARCH_REGEX = "([\\w-]*\\*?\\.)*([\\w-]*\\*?\\.?)";

	/**
	 * Compiled pattern of <code>PARTIAL_SEARCH_REGEX</code>.
	 */
	private static final Pattern PARTIAL_DOMAIN_SEARCH_PATTERN = Pattern.compile(PARTIAL_DOMAIN_SEARCH_REGEX);

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

	/**
	 * Validates if IpAddress is valid
	 * 
	 * @param ipAddress
	 * @return
	 * @throws MalformedRequestException
	 */
	public static InetAddress validateIpAddress(String ipAddress) throws MalformedRequestException {
		// if the ipAddress contains ':' then InetAddress will try to parse it
		// like IPv6 address without doing a lookup to DNS.
		if (ipAddress.contains(":")) {
			try {
				return InetAddress.getByName(ipAddress);
			} catch (UnknownHostException e) {
				throw new MalformedRequestException("Requested ip is invalid.");
			}
		}

		if (ipAddress.startsWith(".") || !IP4_GENERIC_PATTERN.matcher(ipAddress).matches()) {
			throw new MalformedRequestException("Requested ip is invalid.");
		}

		String[] split = ipAddress.split("\\.");

		int arraySize = split.length;
		if (arraySize > IP_ADDRESS_ARRAY_SIZE) {
			throw new MalformedRequestException("Requested ip is invalid.");
		}

		BigInteger finalOctectValue;
		try {
			finalOctectValue = new BigInteger(split[arraySize - 1]);
		} catch (NumberFormatException e) {
			throw new MalformedRequestException("Requested ip is invalid.");
		}

		BigInteger limitValue = null;
		switch (arraySize) {
		case 1:
			limitValue = FIRST_OCTECT_LIMIT;
			break;
		case 2:
			limitValue = SECOND_OCTECT_LIMIT;
			break;
		case 3:
			limitValue = THIRD_OCTECT_LIMIT;
			break;
		case 4:
			limitValue = FOURTH_OCTECT_LIMIT;
			break;
		}

		if (limitValue.compareTo(finalOctectValue) < 0) {
			throw new MalformedRequestException("Requested ip is invalid.");
		}

		try {
			return InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			throw new MalformedRequestException("Requested ip is invalid.");
		}

	}

	/**
	 * Get the max search results number allowed for the user
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Integer getMaxNumberOfResultsForUser(String username, Connection connection)
			throws IOException, SQLException {
		if (username != null) {
			Integer limit = RdapUserModel.getMaxSearchResultsForAuthenticatedUser(username, connection);
			if (limit != null && limit != 0)
				return limit;

			else
				return RdapConfiguration.getMaxNumberOfResultsForAuthenticatedUser();
		}
		return RdapConfiguration.getMaxNumberOfResultsForUnauthenticatedUser();
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

	/**
	 * @return the authenticatedMaxUserResultLimit
	 */
	public Integer getAuthenticatedMaxUserResultLimit() {
		return authenticatedMaxUserResultLimit;
	}

	/**
	 * @param authenticatedMaxUserResultLimit
	 *            the authenticatedMaxUserResultLimit to set
	 */
	public void setAuthenticatedMaxUserResultLimit(Integer authenticatedMaxUserResultLimit) {
		this.authenticatedMaxUserResultLimit = authenticatedMaxUserResultLimit;
	}

	// TODO new easier to use class, maybe-----------

	@SuppressWarnings("unchecked")
	public static List<Remark> readNoticesFromFiles(String filePath) throws FileNotFoundException {
		List<Remark> notices = new ArrayList<Remark>();
		List<List<Object>> noticesData = readFiles(filePath);
		for (List<Object> noticeData : noticesData) {
			Remark notice = new Remark();
			List<String> descriptions = (List<String>) noticeData.get(1);
			notice.setTitle((String) noticeData.get(0));
			for (String descriptionString : descriptions) {
				RemarkDescription description = new RemarkDescription();
				description.setDescription(descriptionString);
				notice.getDescriptions().add(description);
			}

			List<Link> links = new ArrayList<Link>();
			List<String> linksData = (List<String>) noticeData.get(2);
			for (String linkData : linksData) {
				Link link;
				try {
					link = parseLink(linkData);
					notice.setLinks(links);
					links.add(link);
				} catch (InvalidadDataStructure e) {
					e.printStackTrace();
				}
			}

			notices.add(notice);
		}

		return notices;

	}

	private static Link parseLink(String linkData) throws InvalidadDataStructure {
		Link link = new Link();
		if (linkData != null && !linkData.trim().isEmpty()) {
			List<String> linkList = Arrays.asList(linkData.split("\\|"));
			if (linkList.size() == 7) {
				link.setValue(linkList.get(0).trim());
				link.setRel(linkList.get(1).trim());
				link.setHref(linkList.get(2).trim());
				link.setHreflag(linkList.get(3).trim());
				link.setTitle(linkList.get(4).trim());
				link.setMedia(linkList.get(5).trim());
				link.setType(linkList.get(6).trim());
			} else {
				throw new InvalidadDataStructure();
			}
		}
		return link;
	}

	private static List<List<Object>> readFiles(String folderPath) {

		File folder = new File(folderPath);

		List<File> txtList = Arrays.asList(folder.listFiles());
		Collections.sort(txtList);
		List<List<Object>> noticesData = new ArrayList<List<Object>>();

		if (txtList.size() != 0) {

			for (File file : txtList) {
				List<Object> notice = readFileContent(file);
				noticesData.add(notice);
			}
		}

		else {
			throw new RuntimeException("There are no text files on folder " + folderPath);
		}

		return noticesData;
	}

	private static List<Object> readFileContent(File file) {
		boolean descriptionChecker = false;
		List<Object> notice = new ArrayList<Object>();
		String title = "";
		List<String> descriptions = new ArrayList<String>();
		List<String> links = new ArrayList<String>();
		String description = "";

		try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
			Iterator<String> iterator = stream.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				if (line.trim().startsWith("title")) {
					title = line.substring(line.indexOf("=") + 1).trim();
				}
				if (line.trim().startsWith("description")) {
					line = line.substring(line.indexOf("=") + 1).trim();
					descriptionChecker = true;
				}
				if (descriptionChecker) {
					do {
						do {
							description = description.trim() + " " + line;
							line = iterator.next().trim();
							description = description.trim();
						} while (!line.isEmpty() && !line.startsWith("link"));
						if (!description.equals(" ") || !description.isEmpty()) {
							descriptions.add(description);
							description = "";
						}
					} while (!line.startsWith("link"));
				}
				if (line.trim().startsWith("link")) {
					descriptionChecker = false;
				}
				if (line.trim().startsWith("link")) {
					links.add(line.substring(line.indexOf("=") + 1).trim());
				}
			}
			descriptions.removeAll(Arrays.asList(" ", "", null));
			notice.add(title);
			notice.add(descriptions);
			notice.add(links);
			return notice;
		}

		catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Remark getOperationalProfileRemark() {
		RemarkDAO remark = new RemarkDAO();
		RemarkDescriptionDAO description = new RemarkDescriptionDAO();
		description.setDescription(
				"This response conforms to the RDAP Operational Profile for gTLD Registries and Registrars version 1.0");
		remark.getDescriptions().add(description);
		return remark;
	}

	public static Remark getEppInformationRemark() {
		RemarkDAO remark = new RemarkDAO();
		remark.setTitle("EPP Status Codes");
		RemarkDescriptionDAO description = new RemarkDescriptionDAO();
		description.setDescription("For more information on domain status codes, please visit https://icann.org/epp");
		remark.getDescriptions().add(description);
		Link link = new Link();
		link.setHref("https://icann.org/epp");
		remark.getLinks().add(link);
		return remark;
	}

	public static Remark getWhoisInaccuracyComplaintFormRemark() {
		RemarkDAO remark = new RemarkDAO();
		RemarkDescriptionDAO description = new RemarkDescriptionDAO();
		remark.setTitle("Whois Inaccuracy Complaint Form");
		description.setDescription("URL of the ICANN Whois Inaccuracy Complaint Form: https://www.icann.org/wicf");
		remark.getDescriptions().add(description);
		Link link = new Link();
		link.setHref("https://www.icann.org/wicf");
		remark.getLinks().add(link);
		return remark;
	}

	/**
	 * Return the privacy status with most priority.something like:
	 * none>owner>authenticate>any
	 */
	public static PrivacyStatus getPriorityPrivacyStatus(boolean isAuthenticated, boolean isOwner,
			Map<String, PrivacyStatus> privacySettings) {
		// First check if all the privacys settings are in "Any"
		if (!privacySettings.containsValue(PrivacyStatus.AUTHENTICATED)
				&& !privacySettings.containsValue(PrivacyStatus.OWNER)
				&& !privacySettings.containsValue(PrivacyStatus.NONE)) {
			return PrivacyStatus.ANY;
		} // Then, validate if all the privacy is
		else if (privacySettings.containsValue(PrivacyStatus.NONE)) {
			return PrivacyStatus.NONE;
		} else if (privacySettings.containsValue(PrivacyStatus.OWNER) && !isOwner) {
			return PrivacyStatus.OWNER;
		} else if (privacySettings.containsValue(PrivacyStatus.AUTHENTICATED) && !isAuthenticated) {
			return PrivacyStatus.AUTHENTICATED;
		} else
			return PrivacyStatus.ANY;
	}

	public static Status getObjectStatusFromPrivacy(boolean isAuthenticated, boolean isOwner,
			PrivacyStatus priorityStatus) {
		if (priorityStatus.equals(PrivacyStatus.ANY)) {
			return null;
		} else if (priorityStatus.equals(PrivacyStatus.NONE)) {
			return Status.REMOVED;
		} else if (priorityStatus.equals(PrivacyStatus.OWNER)) {
			return Status.PRIVATE;
		} else if (priorityStatus.equals(PrivacyStatus.AUTHENTICATED)) {
			return Status.PRIVATE;
		} else
			return null;
	}

	public static Remark getObjectRemarkFromPrivacy(boolean isAuthenticated, boolean isOwner,
			PrivacyStatus priorityStatus) {
		if (priorityStatus.equals(PrivacyStatus.ANY)) {
			return null;
		} else if (priorityStatus.equals(PrivacyStatus.NONE)) {
			return new Remark(RemarkType.OBJECT_AUTHORIZATION);
		} else if (priorityStatus.equals(PrivacyStatus.OWNER)) {
			return new Remark(RemarkType.OBJECT_AUTHORIZATION);
		} else if (priorityStatus.equals(PrivacyStatus.AUTHENTICATED)) {
			return new Remark(RemarkType.OBJECT_AUTHORIZATION);
		} else
			return new Remark(RemarkType.OBJECT_UNEXPLAINABLE);
	}

	public static Remark getTermsOfServiceNotice(String filePath) throws FileNotFoundException {
		return Util.readNoticesFromFiles(filePath).get(0);
	}
}
