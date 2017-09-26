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
	private static final String ANONYMOUS_USERNAME_KEY = "anonymous_username";
	private static final String ALLOW_WILDCARDS_KEY = "allow_search_wildcards_anywhere";

	// Settings values
	private static String serverLanguage;
	private static Integer minimumSearchPatternLength;
	private static Integer maxNumberOfResultsForAuthenticatedUser;
	private static Integer maxNumberOfResultsForUnauthenticatedUser;
	private static Map<String, Set<Role>> objectOwnerRoles;
	private static String anonymousUsername;
	private static boolean allowSearchWilcardsAnywhere;

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
	public static void loadConfiguredRoles() throws InitializationException {
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

	public static void validateRdapConfiguration() throws InitializationException {
		boolean isValid = true;
		List<String> invalidProperties = new ArrayList<>();
		List<Exception> exceptions = new ArrayList<>();

		if (isPropertyNullOrEmpty(LANGUAGE_KEY)) {
			isValid = false;
			invalidProperties.add(LANGUAGE_KEY);
		} else {
			serverLanguage = systemProperties.getProperty(LANGUAGE_KEY).trim();
		}

		if (isPropertyNullOrEmpty(MINIMUN_SEARCH_PATTERN_LENGTH_KEY)) {
			isValid = false;
			invalidProperties.add(MINIMUN_SEARCH_PATTERN_LENGTH_KEY);
		} else {
			try {
				minimumSearchPatternLength = Integer
						.parseInt(systemProperties.getProperty(MINIMUN_SEARCH_PATTERN_LENGTH_KEY).trim());
			} catch (NumberFormatException e) {
				isValid = false;
				invalidProperties.add(MINIMUN_SEARCH_PATTERN_LENGTH_KEY);
				exceptions.add(e);
			}
		}

		if (isPropertyNullOrEmpty(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER)) {
			isValid = false;
			invalidProperties.add(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER);
		} else {
			try {
				maxNumberOfResultsForAuthenticatedUser = Integer
						.parseInt(systemProperties.getProperty(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER).trim());
			} catch (NumberFormatException e) {
				isValid = false;
				invalidProperties.add(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER);
				exceptions.add(e);
			}
		}

		if (isPropertyNullOrEmpty(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER)) {
			isValid = false;
			invalidProperties.add(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER);
		} else {
			try {
				maxNumberOfResultsForUnauthenticatedUser = Integer
						.parseInt(systemProperties.getProperty(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER).trim());
			} catch (NumberFormatException e) {
				isValid = false;
				invalidProperties.add(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER);
				exceptions.add(e);
			}
		}

		if (isPropertyNullOrEmpty(OWNER_ROLES_IP_KEY)) {
			isValid = false;
			invalidProperties.add(OWNER_ROLES_IP_KEY);
		}

		if (isPropertyNullOrEmpty(OWNER_ROLES_AUTNUM_KEY)) {
			isValid = false;
			invalidProperties.add(OWNER_ROLES_AUTNUM_KEY);
		}

		if (isPropertyNullOrEmpty(OWNER_ROLES_DOMAIN_KEY)) {
			isValid = false;
			invalidProperties.add(OWNER_ROLES_DOMAIN_KEY);
		}

		if (isPropertyNullOrEmpty(OWNER_ROLES_NAMESERVER_KEY)) {
			isValid = false;
			invalidProperties.add(OWNER_ROLES_NAMESERVER_KEY);
		}

		if (isPropertyNullOrEmpty(ANONYMOUS_USERNAME_KEY)) {
			isValid = false;
			invalidProperties.add(ANONYMOUS_USERNAME_KEY);
		} else {
			anonymousUsername = systemProperties.getProperty(ANONYMOUS_USERNAME_KEY).trim();
		}

		if (isPropertyNullOrEmpty(ALLOW_WILDCARDS_KEY)) {
			isValid = false;
			invalidProperties.add(ALLOW_WILDCARDS_KEY);
		} else {
			String allowWildcardProperty = systemProperties.getProperty(ALLOW_WILDCARDS_KEY).trim();
			if (allowWildcardProperty.equalsIgnoreCase("true")) {
				allowSearchWilcardsAnywhere = true;
			} else if (allowWildcardProperty.equalsIgnoreCase("false")) {
				allowSearchWilcardsAnywhere = false;
			} else {
				isValid = false;
				invalidProperties.add(ALLOW_WILDCARDS_KEY);
			}
		}

		if (!isValid) {
			InitializationException invalidValueException = new InitializationException(
					"The following required properties were not found or are invalid values in configuration file : "
							+ invalidProperties.toString());
			for (Exception exception : exceptions) {
				invalidValueException.addSuppressed(exception);
			}
			throw invalidValueException;
		}
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
	 * Return if the server supports wildcards in the end of the searches.
	 */
	public static boolean allowSearchWildcardsAnywhere() {
		return allowSearchWilcardsAnywhere;
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
	 * @param username
	 *            username to evaluate if it is an anonymous username
	 * @return <code>true</code> if username is anonymous or null, otherwise
	 *         <code>false</code>
	 */
	public static boolean isAnonymousUsername(String username) {
		return (username == null || anonymousUsername.equalsIgnoreCase(username));
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
			role = role.trim();
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

}
