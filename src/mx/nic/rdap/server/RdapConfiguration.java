package mx.nic.rdap.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import mx.nic.rdap.server.db.model.ZoneModel;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Class containing the configuration of the rdap server
 * 
 * @author dalpuche
 *
 */
public class RdapConfiguration {

	private static Properties systemProperties;
	private static final String LANGUAGE_KEY = "language";
	private static final String ZONE_KEY = "zones";

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
			systemProperties.getProperty(LANGUAGE_KEY);
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
		}
		return null;
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
