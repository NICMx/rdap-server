package mx.nic.rdap.server.migration;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.Util;

/**
 * Class that runs tasks every 24 hours
 * 
 * @author evaldes
 *
 */

public class Scheduler {

	/** File from which we will load the migration batch configuration . */
	private static final String MIGRATION_BATCH_CONFIGURATION = "migration/configuration";
	// Keys for the batch configuration file
	private static final String FIRST_TIME_EXECUTION_KEY = "first.time.execution";
	private static final String TIME_BETWEEN_EXECUTION_KEY = "time.between.execution";

	public static void main(String args[]) throws InterruptedException {

		Logger logger = Logger.getLogger(Scheduler.class.getName());
		Properties configuration;

		try {
			configuration = Util.loadProperties(MIGRATION_BATCH_CONFIGURATION);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error reading migration batch configuration");
			throw new RuntimeException(e);
		}

		Date firstTimeExecutionDate;
		String firsTimeExecutionDateString = configuration.getProperty(FIRST_TIME_EXECUTION_KEY);
		if (firsTimeExecutionDateString != null && !firsTimeExecutionDateString.trim().isEmpty()) {
			try {
				SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				firstTimeExecutionDate = format.parse(configuration.getProperty(FIRST_TIME_EXECUTION_KEY));
				if (firstTimeExecutionDate.before(Calendar.getInstance().getTime())) {
					logger.log(Level.INFO,
							"First time execution date is in the past. The migration will be starting now");
				}
				logger.log(Level.INFO, "The migration will be starting the " + firstTimeExecutionDate);
			} catch (ParseException e) {
				throw new RuntimeException("Invalid value for first time execution date in configuration.properties");
			}
		} else {
			logger.log(Level.WARNING, "First time execution date not found.The migration will be starting now");
			firstTimeExecutionDate = Calendar.getInstance().getTime();
		}

		Long timeBetweenExecution;
		String timeBetweenExecutionString = configuration.getProperty(TIME_BETWEEN_EXECUTION_KEY);
		if (timeBetweenExecutionString != null && !timeBetweenExecutionString.trim().isEmpty()) {
			try {
				timeBetweenExecution = Long.decode(configuration.getProperty(TIME_BETWEEN_EXECUTION_KEY));
			} catch (NumberFormatException e) {
				throw new RuntimeException("Invalid value for time bewtween execution in configuration.properties");
			}
		} else {
			throw new RuntimeException("Time bewtween execution not found in configuration.properties");
		}
		// Creates Timer which runs the tasks
		Timer timer = new Timer();

		// Create task from class ScheduledTask
		MigrationBatch task = new MigrationBatch();

		try {
			timer.scheduleAtFixedRate(task, firstTimeExecutionDate, timeBetweenExecution);
		} catch (RuntimeException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

	}

}
