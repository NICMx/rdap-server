package mx.nic.rdap.server.migration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import mx.nic.rdap.server.Util;

/**
 * @author L00000185
 *
 */
public class MigrationInitializer {

	public static void initDBConnection() {
		try {
			MigrationDatabaseSession.init(readProperties());
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	private static Properties readProperties() throws IOException {
		String propertiesFilePath = "META-INF/migration/database.properties";
		Properties result = new Properties();
		try (InputStream configStream = Util.class.getClassLoader().getResourceAsStream(propertiesFilePath)) {
			result.load(configStream);
		}
		return result;
	}

	public static void closeDBConnection() {
		try {
			MigrationDatabaseSession.close();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}
