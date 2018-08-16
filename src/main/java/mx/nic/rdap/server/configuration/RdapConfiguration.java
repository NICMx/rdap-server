package mx.nic.rdap.server.configuration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import mx.nic.rdap.core.catalog.Role;
import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.db.RdapUser;
import mx.nic.rdap.db.exception.InitializationException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.RdapUserDAO;
import mx.nic.rdap.server.catalog.PrivacyStatus;
import mx.nic.rdap.server.privacy.PrivacySettingsFactory;

/**
 * Class containing the configuration of the rdap server
 */
public class RdapConfiguration {
	private static Properties systemProperties;

	// property keys
	private static final String LANGUAGE_KEY = "language";
	private static final String MINIMUN_SEARCH_PATTERN_LENGTH_KEY = "minimum_search_pattern_length";
	private static final String MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER = "max_number_result_authenticated_user";
	private static final String MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER = "max_number_result_unauthenticated_user";
	private static final String OWNER_ROLES_IP_KEY = "owner_roles_ip";
	private static final String OWNER_ROLES_AUTNUM_KEY = "owner_roles_autnum";
	private static final String OWNER_ROLES_DOMAIN_KEY = "owner_roles_domain";
	private static final String OWNER_ROLES_NAMESERVER_KEY = "owner_roles_nameserver";
	private static final String ALLOW_MULTIPLE_WILDCARDS_KEY = "allow_multiple_search_wildcards";
	private static final String ALLOW_WILDCARD_ANYWHERE_KEY = "allow_search_wildcard_anywhere";
	private static final String ALLOW_REGEX_SEARCHES = "allow_regex_searches";
	private static final String USER_ROLES_KEY = "user_roles";
	private static final String NOTICES_TIMER_UPDATE_TIME_KEY = "notices_timer_update_time";
	private static final String EVENTS_TIMER_UPDATE_TIME_KEY = "events_timer_update_time";
	private static final String IS_DB_DATA_LIVE = "is_db_data_live";
	

	// Settings values
	private static String serverLanguage;
	private static Integer minimumSearchPatternLength;
	private static Integer maxNumberOfResultsForAuthenticatedUser;
	private static Integer maxNumberOfResultsForUnauthenticatedUser;
	private static Map<String, Set<Role>> objectOwnerRoles;
	private static boolean allowMultipleWildcards;
	private static boolean allowSearchWildcardAnywhere;
	private static boolean allowRegexSearches;
	private static Set<String> userRoles;
	private static boolean isNSSharingNameConformance;
	private static int noticesUpdateTime;
	private static int eventsUpdateTime;
	private static boolean isDbDataLive;

	private RdapConfiguration() {
		// no code.
	}

	/**
	 * Load the parameters defined in the configuration file
	 * 
	 * @param systemProperties
	 *            the systemProperties to set
	 */
	public static void loadSystemProperties(Properties systemProperties) {
		RdapConfiguration.systemProperties = systemProperties;
	}

	/**
	 * Loads all the roles that are owners of the <code>RdapObject</code>s, the configured owners
	 * are loaded from properties loaded at {@link RdapConfiguration}
	 * 
	 * @throws InitializationException if there's an error loading the values
	 */
	public static void loadConfiguredOwnerRoles() throws InitializationException {
		objectOwnerRoles = new HashMap<String, Set<Role>>();
		loadObjectOwnerRoles(OWNER_ROLES_IP_KEY, IpNetwork.class.getName(), objectOwnerRoles);
		loadObjectOwnerRoles(OWNER_ROLES_AUTNUM_KEY, Autnum.class.getName(), objectOwnerRoles);
		loadObjectOwnerRoles(OWNER_ROLES_DOMAIN_KEY, Domain.class.getName(), objectOwnerRoles);
		loadObjectOwnerRoles(OWNER_ROLES_NAMESERVER_KEY, Nameserver.class.getName(), objectOwnerRoles);
	}

	/**
	 * Check if the role sent is a configured owner of the object.
	 * 
	 * @param object
	 *            The object to verify its owner, should be any of: {@link IpNetwork}, {@link Autnum},
	 *            {@link Domain} or {@link Nameserver} 
	 * @param role
	 *            {@link Role} to verify if is owner of the object.
	 * @return <code>boolean</code> indicating if the role is an owner of the object
	 */
	public static boolean isRoleAnOwner(Object object, Role role) {
		if (object == null) {
			return false;
		}
		String className = null;
		if (object instanceof IpNetwork) {
			className = IpNetwork.class.getName();
		} else if (object instanceof Autnum) {
			className = Autnum.class.getName();
		} else if (object instanceof Domain) {
			className = Domain.class.getName();
		} else if (object instanceof Nameserver) {
			className = Nameserver.class.getName();
		} else {
			return false;
		}
		return objectOwnerRoles.get(className).contains(role);
	}

	/**
	 * Load configuration from previously loaded system properties
	 * 
	 * @throws InitializationException if any property is misconfigured
	 */
	public static void loadRdapConfiguration() throws InitializationException {
		List<String> invalidProperties = new ArrayList<>();
		List<Exception> exceptions = new ArrayList<>();

		if (isPropertyNullOrEmpty(LANGUAGE_KEY)) {
			invalidProperties.add(LANGUAGE_KEY);
		} else {
			serverLanguage = systemProperties.getProperty(LANGUAGE_KEY).trim();
		}

		if (isPropertyNullOrEmpty(MINIMUN_SEARCH_PATTERN_LENGTH_KEY)) {
			invalidProperties.add(MINIMUN_SEARCH_PATTERN_LENGTH_KEY);
		} else {
			try {
				minimumSearchPatternLength = Integer
						.parseInt(systemProperties.getProperty(MINIMUN_SEARCH_PATTERN_LENGTH_KEY).trim());
			} catch (NumberFormatException e) {
				invalidProperties.add(MINIMUN_SEARCH_PATTERN_LENGTH_KEY);
				exceptions.add(e);
			}
		}

		if (isPropertyNullOrEmpty(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER)) {
			invalidProperties.add(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER);
		} else {
			try {
				maxNumberOfResultsForAuthenticatedUser = Integer
						.parseInt(systemProperties.getProperty(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER).trim());
			} catch (NumberFormatException e) {
				invalidProperties.add(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER);
				exceptions.add(e);
			}
		}

		if (isPropertyNullOrEmpty(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER)) {
			invalidProperties.add(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER);
		} else {
			try {
				maxNumberOfResultsForUnauthenticatedUser = Integer
						.parseInt(systemProperties.getProperty(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER).trim());
			} catch (NumberFormatException e) {
				invalidProperties.add(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER);
				exceptions.add(e);
			}
		}

		if (isPropertyNullOrEmpty(OWNER_ROLES_IP_KEY)) {
			invalidProperties.add(OWNER_ROLES_IP_KEY);
		}

		if (isPropertyNullOrEmpty(OWNER_ROLES_AUTNUM_KEY)) {
			invalidProperties.add(OWNER_ROLES_AUTNUM_KEY);
		}

		if (isPropertyNullOrEmpty(OWNER_ROLES_DOMAIN_KEY)) {
			invalidProperties.add(OWNER_ROLES_DOMAIN_KEY);
		}

		if (isPropertyNullOrEmpty(OWNER_ROLES_NAMESERVER_KEY)) {
			invalidProperties.add(OWNER_ROLES_NAMESERVER_KEY);
		}

		if (isPropertyNullOrEmpty(ALLOW_MULTIPLE_WILDCARDS_KEY)) {
			invalidProperties.add(ALLOW_MULTIPLE_WILDCARDS_KEY);
		} else {
			String allowMultipleWildcardProperty = systemProperties.getProperty(ALLOW_MULTIPLE_WILDCARDS_KEY).trim();
			if (allowMultipleWildcardProperty.equalsIgnoreCase("true")) {
				allowMultipleWildcards = true;
			} else if (allowMultipleWildcardProperty.equalsIgnoreCase("false")) {
				allowMultipleWildcards = false;
			} else {
				invalidProperties.add(ALLOW_MULTIPLE_WILDCARDS_KEY);
			}
		}

		if (isPropertyNullOrEmpty(ALLOW_WILDCARD_ANYWHERE_KEY)) {
			invalidProperties.add(ALLOW_WILDCARD_ANYWHERE_KEY);
		} else {
			String allowWildcardProperty = systemProperties.getProperty(ALLOW_WILDCARD_ANYWHERE_KEY).trim();
			if (allowWildcardProperty.equalsIgnoreCase("true")) {
				allowSearchWildcardAnywhere = true;
			} else if (allowWildcardProperty.equalsIgnoreCase("false")) {
				allowSearchWildcardAnywhere = false;
			} else {
				invalidProperties.add(ALLOW_WILDCARD_ANYWHERE_KEY);
			}
		}

		if (isPropertyNullOrEmpty(ALLOW_REGEX_SEARCHES)) {
			invalidProperties.add(ALLOW_REGEX_SEARCHES);
		} else {
			String allowRegexSearchesProperty = systemProperties.getProperty(ALLOW_REGEX_SEARCHES).trim();
			if (allowRegexSearchesProperty.equalsIgnoreCase("true")) {
				allowRegexSearches = true;
			} else if (allowRegexSearchesProperty.equalsIgnoreCase("false")) {
				allowRegexSearches = false;
			} else {
				invalidProperties.add(ALLOW_REGEX_SEARCHES);
			}
		}

		// Optional property, no problem if it's null
		userRoles = new HashSet<String>();
		try {
			loadUserRoles(USER_ROLES_KEY, userRoles);
		} catch (InitializationException e) {
			invalidProperties.add(USER_ROLES_KEY);
			exceptions.add(e);
		}

		if (isPropertyNullOrEmpty(NOTICES_TIMER_UPDATE_TIME_KEY)) {
			invalidProperties.add(NOTICES_TIMER_UPDATE_TIME_KEY);
		} else {
			String noticesUpdTimeString = systemProperties.getProperty(NOTICES_TIMER_UPDATE_TIME_KEY).trim();
			try {
				noticesUpdateTime = Integer.parseInt(noticesUpdTimeString);
			} catch (NumberFormatException e) {
				invalidProperties.add(NOTICES_TIMER_UPDATE_TIME_KEY);
			}
		}

		if (isPropertyNullOrEmpty(EVENTS_TIMER_UPDATE_TIME_KEY)) {
			invalidProperties.add(EVENTS_TIMER_UPDATE_TIME_KEY);
		} else {
			String eventsUpdTimeString = systemProperties.getProperty(EVENTS_TIMER_UPDATE_TIME_KEY).trim();
			try {
				eventsUpdateTime = Integer.parseInt(eventsUpdTimeString);
			} catch (NumberFormatException e) {
				invalidProperties.add(EVENTS_TIMER_UPDATE_TIME_KEY);
			}
		}

		if (isPropertyNullOrEmpty(IS_DB_DATA_LIVE)) {
			invalidProperties.add(IS_DB_DATA_LIVE);
		} else {
			String isDbDataLiveProperty = systemProperties.getProperty(IS_DB_DATA_LIVE).trim();
			if (isDbDataLiveProperty.equalsIgnoreCase("true")) {
				isDbDataLive = true;
			} else if (isDbDataLiveProperty.equalsIgnoreCase("false")) {
				isDbDataLive = false;
			} else {
				invalidProperties.add(IS_DB_DATA_LIVE);
			}
		}

		if (!invalidProperties.isEmpty()) {
			InitializationException invalidValueException = new InitializationException(
					"The following required properties were not found or are invalid values in configuration file : "
							+ invalidProperties.toString());
			for (Exception exception : exceptions) {
				invalidValueException.addSuppressed(exception);
			}
			throw invalidValueException;
		}

		isNSSharingNameConformance = false;
		
		
		
	}

	/**
	 * Check if the property is null or empty
	 * 
	 * @param propertyKey
	 *            Key of the property validated
	 * @return <code>boolean</code> indicating if the property is null or empty
	 */
	private static boolean isPropertyNullOrEmpty(String propertyKey) {
		String systemProperty = systemProperties.getProperty(propertyKey);
		return systemProperty == null || systemProperty.trim().isEmpty();
	}

	/**
	 * @return the server language defined in the configuration file
	 */
	public static String getServerLanguage() {
		return serverLanguage;
	}

	/**
	 * @return the minimum search pattern length defined in configuration file
	 */
	public static int getMinimumSearchPatternLength() {
		return minimumSearchPatternLength;
	}

	/**
	 * @return the max number of results for the authenticated user
	 */
	private static int getMaxNumberOfResultsForAuthenticatedUser() {
		return maxNumberOfResultsForAuthenticatedUser;
	}

	/**
	 * Return the max number of results for the authenticated user
	 * 
	 */
	private static int getMaxNumberOfResultsForUnauthenticatedUser() {
		return maxNumberOfResultsForUnauthenticatedUser;
	}

	/**
	 * @return if the server supports multiple wildcards in each label at the search pattern
	 */
	public static boolean allowMultipleWildcards() {
		return allowMultipleWildcards;
	}

	/**
	 * @return if the server supports a wildcard at the end of the searches.
	 */
	public static boolean allowSearchWildcardAnywhere() {
		return allowSearchWildcardAnywhere;
	}

	/**
	 * @return if the server supports searches using regex (see more at
	 * https://tools.ietf.org/html/draft-fregly-regext-rdap-search-regex-01).
	 */
	public static boolean allowRegexSearches() {
		return allowRegexSearches;
	}

	/**
	 * Check if a user role is configured
	 * 
	 * @param userRole
	 *            role to check
	 * @return <code>true</code> if role is configured, <code>false</code> if not
	 */
	public static boolean isUserRoleConfigured(String userRole) {
		return userRoles.contains(userRole.toLowerCase());
	}

	/**
	 * Get the max search results number allowed for the user
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static int getMaxNumberOfResultsForUser(String username) throws RdapDataAccessException {
		if (username != null) {
			// Find if the user has a custom limit.
			Integer limit = null;

			RdapUserDAO dao = DataAccessService.getRdapUserDAO();
			if (dao != null) {
				RdapUser user = dao.getByUsername(username);
				if (user != null) {
					limit = user.getMaxSearchResults();
				}
			}

			if (limit != null && limit != 0) {
				return limit;
			} else {
				// return server configuration.
				return getMaxNumberOfResultsForAuthenticatedUser();
			}
		}
		return getMaxNumberOfResultsForUnauthenticatedUser();
	}

	/**
	 * Load the roles for a specific object.
	 * 
	 * @param rolesKey
	 *            Key of the property where the roles are going to be loaded
	 * @param className
	 *            Class name of the object, used as key in the <code>Map</code>
	 * @param objectRolesMap
	 *            <code>Map</code> where the configured object and roles will be loaded
	 * @throws InitializationException if there's an error loading the values
	 */
	private static void loadObjectOwnerRoles(String rolesKey, String className, Map<String, Set<Role>> objectRolesMap) 
			throws InitializationException {
		String ownerRoles = systemProperties.getProperty(rolesKey);
		if (ownerRoles == null || ownerRoles.trim().isEmpty()) {
			throw new InitializationException("property '" + rolesKey + "' is not configured");
		}

		String[] split = ownerRoles.split(",");
		Set<Role> loadedRoles = new HashSet<Role>();
		for (String role : split) {
			role = role.trim().toLowerCase();
			if (role.isEmpty()) {
				continue;
			}
			Role roleEnum = Role.getByName(role);
			if (roleEnum == null) {
				throw new InitializationException("unknown role in property '" + rolesKey + "': " + role);
			}
			loadedRoles.add(roleEnum);
		}
		if (loadedRoles.isEmpty()) {
			throw new InitializationException("property '" + rolesKey + "' is misconfigured");
		}
		objectRolesMap.put(className, loadedRoles);
	}

	/**
	 * Load the user roles that can be used.
	 * 
	 * @param userRolesKey
	 *            Key of the property where the roles are going to be loaded
	 * @param userRolesSet
	 *            <code>Set</code> where the configured user roles will be loaded 
	 * @throws InitializationException
	 */
	private static void loadUserRoles(String userRolesKey, Set<String> userRolesSet) throws InitializationException {
		String[] customRoles = null;
		if (!isPropertyNullOrEmpty(userRolesKey)) {
			String userRoles = systemProperties.getProperty(userRolesKey);
			String[] split = userRoles.split(",");
			for (String role : split) {
				role = role.trim().toLowerCase();
				if (role.isEmpty()) {
					continue;
				}
				// Reserved roles
				try {
					PrivacyStatus.valueOf(role.toUpperCase());
					throw new InitializationException("property '" + userRolesKey + "' can't use the reserved role '"
													+ role.toUpperCase() + "', try another value");
				} catch (IllegalArgumentException e) {
					// Ok, add role
					userRolesSet.add(role);
				}
			}
			if (!userRolesSet.isEmpty()) {
				customRoles = userRolesSet.toArray(new String[userRolesSet.size()]);
			}
		}
		PrivacySettingsFactory.initializePool(customRoles);
	}

	/**
	 * @return true if implements draft-lozano-rdap-nameserver-sharing-name, false
	 *         otherwise.
	 */
	public static boolean isNameserverSharingNameConformance() {
		return isNSSharingNameConformance;
	}

	public static void setNameserverSharingNameConformance(boolean isNameserverSharingNameConformance) {
		isNSSharingNameConformance = isNameserverSharingNameConformance;
	}
	
	
	public static int getEventsUpdateTime() {
		return eventsUpdateTime;
	}
	
	public static int getNoticesUpdateTime() {
		return noticesUpdateTime;
	}
	
	public static boolean isDbDataLive() {
		return isDbDataLive;
	}
}
