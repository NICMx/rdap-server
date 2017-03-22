package mx.nic.rdap.server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import mx.nic.rdap.core.catalog.Rol;
import mx.nic.rdap.db.RdapUser;
import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.exception.ObjectNotFoundException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.service.DataAccessService;
import mx.nic.rdap.db.spi.RdapUserDAO;
import mx.nic.rdap.server.catalog.OperationalProfile;

/**
 * Class containing the configuration of the rdap server
 */
public class RdapConfiguration {
	private static Properties systemProperties;

	// property keys
	private static final String LANGUAGE_KEY = "language";
	private static final String ZONE_KEY = "zones";
	private static final String MINIMUN_SEARCH_PATTERN_LENGTH_KEY = "minimum_search_pattern_length";
	private static final String MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER = "max_number_result_authenticated_user";
	private static final String MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER = "max_number_result_unauthenticated_user";
	private static final String OWNER_ROLES_KEY = "owner_roles";
	private static final String IS_REVERSE_IPV4_ENABLED_KEY = "is_reverse_ipv4_enabled";
	private static final String IS_REVERSE_IPV6_ENABLED_KEY = "is_reverse_ipv6_enabled";
	private static final String OPERATIONAL_PROFILE_KEY = "operational_profile";
	private static final String ANONYMOUS_USERNAME_KEY = "anonymous_username";

	// Settings values
	private static String serverLanguage;
	private static Integer minimumSearchPatternLength;
	private static Integer maxNumberOfResultsForAuthenticatedUser;
	private static Integer maxNumberOfResultsForUnauthenticatedUser;
	private static Set<Rol> objectOwnerRoles;
	private static OperationalProfile operationalProfile;
	private static String anonymousUsername;
	private static Set<String> validZones;

	public static String REVERSE_IP_V4 = "in-addr.arpa";
	public static String REVERSE_IP_V6 = "ip6.arpa";

	private RdapConfiguration() {
		// no code.
	}

	/**
	 * Load the parameters defined in the configuration file
	 * 
	 * @param systemProperties
	 *            the systemProperties to set
	 * @throws ObjectNotFoundException
	 */
	public static void loadSystemProperties(Properties systemProperties) throws ObjectNotFoundException {
		RdapConfiguration.systemProperties = systemProperties;
	}

	/**
	 * Return the list of zones defined in the configuration file
	 * 
	 * @return
	 */
	private static List<String> getServerZones() {
		if (systemProperties.containsKey(ZONE_KEY)) {
			String zones[] = systemProperties.getProperty(ZONE_KEY).trim().split(",");
			List<String> trimmedZones = new ArrayList<String>();
			for (String zone : zones) {
				zone = zone.trim();
				if (zone.isEmpty())
					continue;
				if (zone.endsWith("."))
					zone = zone.substring(0, zone.length() - 1);
				if (zone.startsWith("."))
					zone = zone.substring(1);
				trimmedZones.add(zone);
			}
			return trimmedZones;
		}

		return Collections.emptyList();
	}

	/**
	 * Validate if the configurated zones are in the database
	 * 
	 * @throws ObjectNotFoundException
	 */
	public static void validateConfiguratedZones() throws ObjectNotFoundException {
		List<String> propertiesZone = RdapConfiguration.getServerZones();

		validZones = new HashSet<>(propertiesZone);
		// Configure reverse zones
		if (Boolean.parseBoolean(systemProperties.getProperty(IS_REVERSE_IPV4_ENABLED_KEY))) {
			validZones.add(REVERSE_IP_V4);
		} else {
			validZones.remove(REVERSE_IP_V4);
		}

		if (Boolean.parseBoolean(systemProperties.getProperty(IS_REVERSE_IPV6_ENABLED_KEY))) {
			validZones.add(REVERSE_IP_V6);
		} else {
			validZones.remove(REVERSE_IP_V6);
		}

	}

	public static void validateConfiguratedRoles() throws InvalidValueException {
		String ownerRoles = systemProperties.getProperty(OWNER_ROLES_KEY);
		if (ownerRoles == null) {
			throw new InvalidValueException("property '" + OWNER_ROLES_KEY + "' is not configured");
		}

		String[] split = ownerRoles.split(",");
		objectOwnerRoles = new HashSet<Rol>();

		for (String rol : split) {
			rol = rol.trim();
			if (rol.isEmpty())
				continue;

			Rol rolEnum = Rol.getByName(rol);
			if (rolEnum == null) {
				throw new InvalidValueException("unknown rol in property '" + OWNER_ROLES_KEY + "': " + rol);
			}

			objectOwnerRoles.add(rolEnum);
		}

	}

	public static boolean isRolAnOwner(Rol rol) {
		return objectOwnerRoles.contains(rol);
	}

	public static void validateRdapConfiguration() throws InvalidValueException {
		boolean isValid = true;
		List<String> invalidProperties = new ArrayList<>();
		List<Exception> exceptions = new ArrayList<>();

		if (systemProperties.getProperty(LANGUAGE_KEY) == null
				|| systemProperties.getProperty(LANGUAGE_KEY).trim().isEmpty()) {
			isValid = false;
			invalidProperties.add(LANGUAGE_KEY);
		}
		serverLanguage = systemProperties.getProperty(LANGUAGE_KEY).trim();

		if (systemProperties.getProperty(MINIMUN_SEARCH_PATTERN_LENGTH_KEY) == null) {
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

		if (systemProperties.getProperty(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER) == null) {
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

		if (systemProperties.getProperty(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER) == null) {
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

		if (systemProperties.getProperty(OWNER_ROLES_KEY) == null) {
			isValid = false;
			invalidProperties.add(OWNER_ROLES_KEY);
		}

		if (systemProperties.getProperty(OPERATIONAL_PROFILE_KEY) != null) {
			String property = systemProperties.getProperty(OPERATIONAL_PROFILE_KEY).trim();

			try {
				operationalProfile = OperationalProfile.valueOf(property.toUpperCase());
				switch (operationalProfile) {
				case REGISTRAR:
					break;
				case REGISTRY:
					break;
				case NONE:
					break;
				default:
					isValid = false;
					invalidProperties.add(OPERATIONAL_PROFILE_KEY);
				}
			} catch (IllegalArgumentException iae) {
				isValid = false;
				invalidProperties.add(OPERATIONAL_PROFILE_KEY);
			}

		}

		if (systemProperties.getProperty(ANONYMOUS_USERNAME_KEY) == null) {
			isValid = false;
			invalidProperties.add(ANONYMOUS_USERNAME_KEY);
		} else {
			anonymousUsername = systemProperties.getProperty(ANONYMOUS_USERNAME_KEY).trim();
		}

		if (!isValid) {
			InvalidValueException invalidValueException = new InvalidValueException(
					"The following required properties were not found or are invalid values in configuration file : "
							+ invalidProperties.toString());
			for (Exception exception : exceptions) {
				invalidValueException.addSuppressed(exception);
			}
			throw invalidValueException;
		}
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
	public static int getMaxNumberOfResultsForAuthenticatedUser() {
		return maxNumberOfResultsForAuthenticatedUser;
	}

	/**
	 * Return the max number of results for the authenticated user
	 * 
	 */
	public static int getMaxNumberOfResultsForUnauthenticatedUser() {
		return maxNumberOfResultsForUnauthenticatedUser;
	}

	/**
	 * Return the profile configurated for the server
	 */
	public static OperationalProfile getServerProfile() {
		return operationalProfile;
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

	public static boolean isValidZone(String domain) {
		if (isReverseAddress(domain))
			return isValidReverseAddress(domain);
		String[] split = domain.split("\\.", 2);
		String zone = split[1].trim();
		if (zone.endsWith(".")) {
			zone = zone.substring(0, zone.length() - 1);
		}
		return validZones.contains(zone);
	}

	// Validates if a reverse address is a valid zone
	private static boolean isValidReverseAddress(String domain) {
		if (domain.endsWith(REVERSE_IP_V4))
			return validZones.contains(REVERSE_IP_V4);
		if (domain.endsWith(REVERSE_IP_V6))
			return validZones.contains(REVERSE_IP_V6);

		return false;
	}

	/**
	 * validate if a address is in reverse lookup
	 * 
	 */
	private static boolean isReverseAddress(String address) {
		return address.trim().endsWith(REVERSE_IP_V4) || address.trim().endsWith(REVERSE_IP_V6);
	}

	public static boolean hasZoneConfigured() {
		if (validZones == null || validZones.isEmpty()) {
			return false;
		}

		return true;
	}

	public static Properties getServerProperties() {
		return systemProperties;
	}
}
