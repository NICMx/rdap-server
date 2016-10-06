package mx.nic.rdap.server.migration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DatabaseSession;

/**
 * Class that manage the database connection for the migrator
 * 
 * @author dalpuche
 *
 */
public class MigrationInitializer {

	/** File from which we will load the database connection. */
	private static final String RDAP_DATABASE_FILE = "database";
	/** File from which we will load the database connection. */
	private static final String ORIGIN_DATABASE_FILE = "migration/database";

	/**
	 * Initialize the origin db connection
	 */
	public static void initOriginDBConnection() {
		try {
			MigrationDatabaseSession.init(readProperties(ORIGIN_DATABASE_FILE));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Initialize the rdap db connection
	 */
	public static void initRDAPDBConnection() {
		try {
			DatabaseSession.init(readProperties(RDAP_DATABASE_FILE));
		} catch (Exception e) {
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

	/**
	 * Read the database properties for the migration
	 * 
	 * @return
	 * @throws IOException
	 */
	private static Properties readProperties(String propertiesFile) throws IOException {
		String propertiesFilePath = "META-INF/" + propertiesFile + ".properties";
		Properties result = new Properties();
		try (InputStream configStream = Util.class.getClassLoader().getResourceAsStream(propertiesFilePath)) {
			result.load(configStream);
		}
		return result;
	}

}
