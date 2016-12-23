package mx.nic.rdap.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.core.catalog.Rol;
import mx.nic.rdap.db.exception.InvalidValueException;
import mx.nic.rdap.db.exception.ObjectNotFoundException;
import mx.nic.rdap.db.model.CountryCodeModel;
import mx.nic.rdap.db.model.RdapUserModel;
import mx.nic.rdap.db.model.ZoneModel;
import mx.nic.rdap.server.catalog.OperationalProfile;
import mx.nic.rdap.server.db.DatabaseSession;

/**
 * Class containing the configuration of the rdap server
 */
public class RdapConfiguration {
	private final static Logger logger = Logger.getLogger(RdapConfiguration.class.getName());
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
	private static final String NAMESERVER_AS_DOMAIN_ATTRIBUTE_KEY = "nameserver_as_domain_attribute";

	// Settings values
	private static String serverLanguage;
	private static Integer minimumSearchPatternLength;
	private static Integer maxNumberOfResultsForAuthenticatedUser;
	private static Integer maxNumberOfResultsForUnauthenticatedUser;
	private static Set<Rol> objectOwnerRoles;
	private static OperationalProfile operationalProfile;
	private static Boolean nameserverAsDomainAttribute;

	private RdapConfiguration() {
		// no code.
	}

	static {
		try (Connection con = DatabaseSession.getRdapConnection()) {
			CountryCodeModel.loadAllFromDatabase(con);
			ZoneModel.loadAllFromDatabase(con);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

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
	public static List<String> getServerZones() {
		if (systemProperties.containsKey(ZONE_KEY)) {
			String zones[] = systemProperties.getProperty(ZONE_KEY).trim().split(",");
			List<String> trimmedZones = new ArrayList<String>();
			for (String zone : zones) {
				if (!zone.trim().isEmpty())
					trimmedZones.add(zone.trim());
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
		List<String> configuratedZones = RdapConfiguration.getServerZones();
		Map<Integer, String> zoneByIdForServer = new HashMap<Integer, String>();
		Map<String, Integer> idByZoneForServer = new HashMap<String, Integer>();

		// Configure reverse zones
		if (Boolean.parseBoolean(systemProperties.getProperty(IS_REVERSE_IPV4_ENABLED_KEY))) {
			String zone = ZoneModel.REVERSE_IP_V4;
			if (ZoneModel.getIdByZone().get(zone) != null) {
				zoneByIdForServer.put(ZoneModel.getIdByZone().get(zone), zone);
				idByZoneForServer.put(zone, ZoneModel.getIdByZoneName(zone));
			} else {
				logger.log(Level.WARNING, "Configurated zone not found in database : " + zone);
			}
		}
		configuratedZones.remove(ZoneModel.REVERSE_IP_V4);

		if (Boolean.parseBoolean(systemProperties.getProperty(IS_REVERSE_IPV6_ENABLED_KEY))) {
			String zone = ZoneModel.REVERSE_IP_V6;
			if (ZoneModel.getIdByZone().get(zone) != null) {
				zoneByIdForServer.put(ZoneModel.getIdByZone().get(zone), zone);
				idByZoneForServer.put(zone, ZoneModel.getIdByZoneName(zone));
			} else {
				logger.log(Level.WARNING, "Configurated zone not found in database : " + zone);
			}
		}
		configuratedZones.remove(ZoneModel.REVERSE_IP_V6);

		for (String zone : configuratedZones) {
			if (ZoneModel.getIdByZone().get(zone) == null) {
				logger.log(Level.SEVERE, "Configurated zone not found in database:" + zone);
				throw new ObjectNotFoundException("Configurated zone not found in database:" + zone);
			}
			zoneByIdForServer.put(ZoneModel.getIdByZone().get(zone), zone);
			idByZoneForServer.put(zone, ZoneModel.getIdByZoneName(zone));
		}

		// Ovewrite the hashmaps to only use the configurated zones
		ZoneModel.setZoneById(zoneByIdForServer);
		ZoneModel.setIdByZone(idByZoneForServer);
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

		if (systemProperties.getProperty(NAMESERVER_AS_DOMAIN_ATTRIBUTE_KEY) == null) {
			isValid = false;
			invalidProperties.add(NAMESERVER_AS_DOMAIN_ATTRIBUTE_KEY);
		} else {
			nameserverAsDomainAttribute = Boolean
					.parseBoolean(systemProperties.getProperty(NAMESERVER_AS_DOMAIN_ATTRIBUTE_KEY));
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
	 * Return the server language defined in the configuration file
	 * 
	 * @return
	 */
	public static String getServerLanguage() {
		return serverLanguage;
	}

	/**
	 * Return the minimum search pattern length defined in configuration file
	 * 
	 * @return
	 */
	public static int getMinimumSearchPatternLength() {
		return minimumSearchPatternLength;
	}

	/**
	 * Return the max number of results for the authenticated user
	 * 
	 * @return
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

	public static boolean useNameserverAsDomainAttribute() {
		return nameserverAsDomainAttribute;
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
			// Find if the user has a custom limit.
			Integer limit = RdapUserModel.getMaxSearchResultsForAuthenticatedUser(username, connection);
			if (limit != null && limit != 0) {
				return limit;
			} else {
				// return server configuration.
				return getMaxNumberOfResultsForAuthenticatedUser();
			}
		}
		return getMaxNumberOfResultsForUnauthenticatedUser();
	}
}
