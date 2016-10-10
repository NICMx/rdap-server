package mx.nic.rdap.server.migration;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that runs tasks every 24 hours
 * 
 * @author evaldes
 *
 */

public class Scheduler {

	public static void main(String args[]) throws InterruptedException {

		Logger logger = Logger.getLogger(Scheduler.class.getName());

		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

		Calendar date = Calendar.getInstance();

		// set hour of the day in 24h format
		// task will run every day at 1 A.M. this way
		date.set(Calendar.HOUR_OF_DAY, 1);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);

		// Creates Timer which runs the tasks
		Timer timer = new Timer();

		// Create task from class ScheduledTask
		ScheduledTask task = new ScheduledTask();

		// Runs task at specified date then waits 24 hours to run again

		try {
			timer.schedule(task, date.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
		} catch (RuntimeException e) {
			logger.log(Level.INFO, e.getMessage());
			Scheduler.main(args);
		}

	}

}
