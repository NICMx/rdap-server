package mx.nic.rdap.server.migration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.db.DatabaseSession;

/**
 * @author L00000185
 *
 */
public class MigrationBatch {

	private final static Logger logger = Logger.getLogger(MigrationBatch.class.getName());
	/** The queries, indexed by means of their names. */
	private HashMap<String, String> queries = new HashMap<>();

	public MigrationBatch() {
		MigrationInitializer.initDBConnection();
		try {
			readMigrationFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void migrate() throws SQLException {
		String query = queries.get("nameserver");
		try (PreparedStatement statement = DatabaseSession.getConnection().prepareStatement(query)) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			statement.executeQuery();
		}
	}

	private void readMigrationFile() throws IOException {
		String migrationFilePath = "META-INF/migration/migration.sql";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				MigrationBatchTest.class.getClassLoader().getResourceAsStream(migrationFilePath)))) {

			StringBuilder queryString = new StringBuilder();
			String objectName = null;
			String currentLine;

			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.startsWith("#")) {
					objectName = currentLine.substring(1).trim();
				} else {
					queryString.append(currentLine);
					if (currentLine.trim().endsWith(";")) {
						// If the query has no name, it will be ignored.
						if (objectName != null) {
							String oldQuery = queries.put(objectName, queryString.toString());
							if (oldQuery != null) {
								throw new IllegalArgumentException(
										"There is more than one '" + objectName + "' query.");
							}
						}
						queryString.setLength(0);
					}
				}
			}
		}
	}

}
