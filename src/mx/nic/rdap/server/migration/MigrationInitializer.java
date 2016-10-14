package mx.nic.rdap.server.migration;

import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;

/**
 * Class that manage the database connection for the migrator
 * 
 * @author dalpuche
 *
 */
public class MigrationInitializer {

	private final static Logger logger = Logger.getLogger(MigrationInitializer.class.getName());
	/** File from which we will load the database connection. */
	private static final String RDAP_DATABASE_FILE = "database";
	/** File from which we will load the database connection. */
	private static final String ORIGIN_DATABASE_FILE = "migration/database";

	/**
	 * Initialize the origin db connection
	 */
	public static void initOriginDBConnection() {
		try {
			MigrationDatabaseSession.init(Util.loadProperties(ORIGIN_DATABASE_FILE));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "******ERROR INITIALIZING ORIGIN DATABASE CONNECTION******");
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Initialize the rdap db connection
	 */
	public static void initRDAPDBConnection() {
		try {
			DatabaseSession.init(Util.loadProperties(RDAP_DATABASE_FILE));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "******ERROR INITIALIZING RDAP DATABASE CONNECTION******");

			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Close the DB connection
	 */
	public static void closeOriginDBConnection() {
		try {
			MigrationDatabaseSession.close();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Close the DB connection
	 */
	public static void closeRDAPDBConnection() {
		try {
			DatabaseSession.close();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}
