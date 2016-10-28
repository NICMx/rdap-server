package mx.nic.rdap.server.migration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Class containing the configuration of the migration batch
 * 
 * @author dalpuche
 *
 */
public class MigratorConfiguration {

	private final static Logger logger = Logger.getLogger(MigratorConfiguration.class.getName());
	private static Properties systemProperties;
	/** File from which we will load the migration batch configuration . */
	private static final String MIGRATION_BATCH_CONFIGURATION = "migration/configuration";
	// Keys for the batch configuration file
	private static final String FIRST_TIME_EXECUTION_KEY = "first.time.execution";
	private static final String TIME_BETWEEN_EXECUTION_KEY = "time.between.execution";
	private static final String MIGRATE_USERS_KEY = "migrate.users";

	static {
		try {
			loadSystemProperties(Util.loadProperties(MIGRATION_BATCH_CONFIGURATION));
		} catch (IOException | ObjectNotFoundException e) {
			throw new RuntimeException("Error loading migration configuration file");
		}
	}

	public MigratorConfiguration() {

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
	private static void loadSystemProperties(Properties systemProperties) throws ObjectNotFoundException {
		MigratorConfiguration.systemProperties = systemProperties;
	}

	/**
	 * Return the first time execution date defined in the configuration file
	 * 
	 * @return
	 */
	public static Date getFirstTimeExecutionDate() {
		Date firstTimeExecutionDate = new Date();
		String firsTimeExecutionDateString = systemProperties.getProperty(FIRST_TIME_EXECUTION_KEY);
		if (firsTimeExecutionDateString != null && !firsTimeExecutionDateString.trim().isEmpty()) {
			try {
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				firstTimeExecutionDate = format.parse(firsTimeExecutionDateString.trim());
				if (firstTimeExecutionDate.before(Calendar.getInstance().getTime())) {
					logger.log(Level.INFO,
							"First time execution date is in the past. The migration will be starting now");
					firstTimeExecutionDate = Calendar.getInstance().getTime();
				} else {
					logger.log(Level.INFO, "The migration will be starting the " + firstTimeExecutionDate);
				}

			} catch (ParseException e) {
				logger.log(Level.WARNING, "First time execution date invalid value.The migration will be starting now");
				firstTimeExecutionDate = Calendar.getInstance().getTime();
			}
		} else {
			logger.log(Level.WARNING, "First time execution date not found.The migration will be starting now");
			firstTimeExecutionDate = Calendar.getInstance().getTime();
		}
		return firstTimeExecutionDate;
	}

	/**
	 * Return the time between execution defined in the configuration file
	 * 
	 * @return
	 */
	public static Long getTimeBetweenExecution() {
		Long timeBetweenExecution;
		String timeBetweenExecutionString = systemProperties.getProperty(TIME_BETWEEN_EXECUTION_KEY);
		if (timeBetweenExecutionString != null && !timeBetweenExecutionString.trim().isEmpty()) {
			try {
				timeBetweenExecution = Long.decode(timeBetweenExecutionString.trim());
			} catch (NumberFormatException e) {
				throw new RuntimeException("Invalid value for time bewtween execution in configuration.properties");
			}
		} else {
			throw new RuntimeException("Time between execution not found in configuration.properties");
		}
		return timeBetweenExecution;
	}

	/**
	 * Return the flag migrate users defined in the configuration file
	 * 
	 * @return
	 */
	public static Boolean migrateUsers() {
		Boolean migrateUsers = false;
		String migrateUsersString = systemProperties.getProperty(MIGRATE_USERS_KEY);
		if (migrateUsersString != null && !migrateUsersString.trim().isEmpty()) {
			try {
				migrateUsers = Boolean.valueOf(migrateUsersString.trim());
			} catch (NumberFormatException e) {
				throw new RuntimeException("Invalid value for migrate.users in configuration.properties");
			}
		} else {
			logger.log(Level.WARNING, "Migrate users not found. The users will not be migrated");
		}
		return migrateUsers;
	}
}
