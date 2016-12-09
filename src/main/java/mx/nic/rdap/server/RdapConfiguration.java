package mx.nic.rdap.server;

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
import mx.nic.rdap.db.model.ZoneModel;
import mx.nic.rdap.server.db.DatabaseSession;

/**
 * Class containing the configuration of the rdap server
 */
public class RdapConfiguration {
	private final static Logger logger = Logger.getLogger(RdapConfiguration.class.getName());
	private static Properties systemProperties;
	private static final String LANGUAGE_KEY = "language";
	private static final String ZONE_KEY = "zones";
	private static final String MINIMUN_SEARCH_PATTERN_LENGTH_KEY = "minimum_search_pattern_length";
	private static final String MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER = "max_number_result_authenticated_user";
	private static final String MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER = "max_number_result_unauthenticated_user";
	private static final String OWNER_ROLES_KEY = "owner_roles";
	private static final String IS_REVERSE_IPV4_ENABLED_KEY = "is_reverse_ipv4_enabled";
	private static final String IS_REVERSE_IPV6_ENABLED_KEY = "is_reverse_ipv6_enabled";
	private static Set<Rol> objectOwnerRoles;

	public RdapConfiguration() {
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
	 * @return the systemProperties
	 */
	public static Properties getSystemProperties() {
		return systemProperties;
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
	 * Return the server language defined in the configuration file
	 * 
	 * @return
	 */
	public static String getServerLanguage() {
		String lang = systemProperties.getProperty(LANGUAGE_KEY);
		return lang.trim();
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
	 * Return the minimum search pattern length defined in configuration file
	 * 
	 * @return
	 */
	public static int getMinimumSearchPatternLength() {
		return Integer.parseInt(systemProperties.getProperty(MINIMUN_SEARCH_PATTERN_LENGTH_KEY).trim());
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

	/**
	 * Return the max number of results for the authenticated user
	 * 
	 * @return
	 */
	public static int getMaxNumberOfResultsForAuthenticatedUser() {
		return Integer.parseInt(systemProperties.getProperty(MAX_NUMBER_OF_RESULTS_FOR_AUTHENTICATED_USER).trim());
	}

	/**
	 * Return the max number of results for the authenticated user
	 * 
	 * @return
	 */
	public static int getMaxNumberOfResultsForUnauthenticatedUser() {
		return Integer.parseInt(systemProperties.getProperty(MAX_NUMBER_OF_RESULTS_FOR_UNAUTHENTICATED_USER).trim());
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

		if (systemProperties.getProperty(MINIMUN_SEARCH_PATTERN_LENGTH_KEY) == null) {
			isValid = false;
			invalidProperties.add(MINIMUN_SEARCH_PATTERN_LENGTH_KEY);
		} else {
			try {
				getMinimumSearchPatternLength();
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
				getMaxNumberOfResultsForAuthenticatedUser();
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
				getMaxNumberOfResultsForUnauthenticatedUser();
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
}
