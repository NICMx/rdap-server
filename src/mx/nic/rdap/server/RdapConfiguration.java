package mx.nic.rdap.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.db.model.ZoneModel;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Class containing the configuration of the rdap server
 * 
 * @author dalpuche
 *
 */
public class RdapConfiguration {
	private final static Logger logger = Logger.getLogger(RdapConfiguration.class.getName());
	private static Properties systemProperties;
	private static final String LANGUAGE_KEY = "language";
	private static final String ZONE_KEY = "zones";
	private static final String MINIMUN_SEARCH_PATTERN_LENGTH = "minimum.search.pattern.length";

	public RdapConfiguration() {
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
		validateConfiguratedZones();
	}

	/**
	 * Return the server language defined in the configuration file
	 * 
	 * @return
	 */
	public static String getServerLanguage() {
		String lang = "en";// defualt langua English
		if (systemProperties.containsKey(LANGUAGE_KEY))
			lang = systemProperties.getProperty(LANGUAGE_KEY);
		else {
			logger.log(Level.WARNING, "Language not found in configuration file. Using default: English");
		}
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
				trimmedZones.add(zone.trim());
			}
			return trimmedZones;
		} else {
			logger.log(Level.SEVERE, "Zones not found in configuration file");
			throw new RuntimeException("MUST defined zones in configuration file");
		}
	}

	/**
	 * Return the minimum search pattern length defined in configuration file
	 * 
	 * @return
	 */
	public static int getMinimumSearchPatternLength() {
		int length = 5;// defualt 5
		if (systemProperties.containsKey(MINIMUN_SEARCH_PATTERN_LENGTH))
			try {
				length = Integer.parseInt(systemProperties.getProperty(MINIMUN_SEARCH_PATTERN_LENGTH).trim());
			} catch (NumberFormatException nfe) {
				logger.log(Level.WARNING,
						"Minimum search pattern length not found in configuration file. Using default: 5");
			}
		return length;
	}

	/**
	 * Validate if the configurated zones are in the database
	 * 
	 * @throws ObjectNotFoundException
	 */
	public static void validateConfiguratedZones() throws ObjectNotFoundException {
		ZoneModel.validateConfiguratedZones();
	}

}
