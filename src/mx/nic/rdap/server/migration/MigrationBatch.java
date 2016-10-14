package mx.nic.rdap.server.migration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.DomainDAO;
import mx.nic.rdap.server.db.EntityDAO;
import mx.nic.rdap.server.db.NameserverDAO;
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.InvalidadDataStructure;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Main class for the migration batch
 * 
 * @author dalpuche
 *
 */
public class MigrationBatch extends TimerTask {

	private final static Logger logger = Logger.getLogger(MigrationBatch.class.getName());
	/** The queries, indexed by means of their names. */
	private HashMap<String, String> queries = new HashMap<>();

	public MigrationBatch() {
		try {
			readMigrationFile();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "******ERROR READING SQL FILE******");
			throw new RuntimeException(e);
		}
	}

	public void run() {
		logger.log(Level.INFO, "******INITIALIZING DATABASE CONNECTIONS******");
		MigrationInitializer.initOriginDBConnection();
		MigrationInitializer.initRDAPDBConnection();
		try {
			migrate();
			logger.log(Level.INFO, "******MIGRATION SUCCESS******");
			try (Connection rdapConnection = DatabaseSession.getConnection();) {
				rdapConnection.commit();
			} catch (SQLException e) {
				throw new RuntimeException("Error in the execution of a SQL Statement", e);
			}
		} catch (IOException | RequiredValueNotFoundException | InvalidValueException | InvalidadDataStructure e) {
			logger.log(Level.SEVERE, "******MIGRATION FAILED******");
			try (Connection rdapConnection = DatabaseSession.getConnection();) {
				rdapConnection.rollback();
			} catch (SQLException e1) {
				throw new RuntimeException(e);
			}
			throw new RuntimeException(e);
		} finally {
			logger.log(Level.INFO, "******CLOSING DATABASE CONNECTIONS******");
			MigrationInitializer.closeOriginDBConnection();
			MigrationInitializer.closeRDAPDBConnection();
		}
	}

	/**
	 * Migration process
	 * 
	 * @throws SQLException
	 * @throws InvalidadDataStructure
	 * @throws InvalidValueException
	 * @throws RequiredValueNotFoundException
	 * @throws IOException
	 */
	public void migrate()
			throws RequiredValueNotFoundException, InvalidValueException, InvalidadDataStructure, IOException {
		try {
			migrateEntities();
			migrateNameservers();
			migrateDomains();
		} catch (SQLException e) {
			throw new RuntimeException("Error in the execution of a SQL Statement", e);
		}

	}

	/**
	 * Execute the entities select stamements in the origin database and store
	 * them in the RDAP databse
	 * 
	 * @throws SQLException
	 * @throws InvalidadDataStructure
	 * @throws InvalidValueException
	 * @throws RequiredValueNotFoundException
	 * @throws IOException
	 */
	private void migrateEntities() throws SQLException, RequiredValueNotFoundException, InvalidValueException,
			InvalidadDataStructure, IOException {
		logger.log(Level.INFO, "******MIGRATING ENTITIES STARTING******");
		try (Connection originConnection = MigrationDatabaseSession.getConnection();
				PreparedStatement statement = originConnection.prepareStatement(queries.get("entity"));) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet entitiesResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing Entities resultset");
			List<EntityDAO> entities = EntityMigrator.getEntitiesFromResultSet(entitiesResultSet);
			logger.log(Level.INFO, "Done!\n Entities retrived:" + entities.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getConnection();) {
				EntityMigrator.storeEntitiesInRDAPDatabase(entities, rdapConnection);
			}

		}
		logger.log(Level.INFO, "******MIGRATING ENTITIES SUCCEEDED******");
	}

	/**
	 * Execute the namerserver select stamements in the origin database and
	 * store them in the RDAP databse
	 * 
	 * @throws SQLException
	 * @throws InvalidadDataStructure
	 * @throws InvalidValueException
	 * @throws RequiredValueNotFoundException
	 * @throws IOException
	 */
	private void migrateNameservers() throws SQLException, RequiredValueNotFoundException, InvalidValueException,
			InvalidadDataStructure, IOException {
		logger.log(Level.INFO, "******MIGRATING NAMESERVERS STARTING******");
		try (Connection originConnection = MigrationDatabaseSession.getConnection();
				PreparedStatement statement = originConnection.prepareStatement(queries.get("nameserver"));) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet nameserverResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing Nameservers resultset");
			List<NameserverDAO> nameservers = NameserverMigrator.getNameserversFromResultSet(nameserverResultSet);
			logger.log(Level.INFO, "Done!\n Nameservers retrived:" + nameservers.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getConnection();) {
				NameserverMigrator.storeNameserversInRDAPDatabase(nameservers, rdapConnection);
			}

		}
		logger.log(Level.INFO, "******MIGRATING NAMESERVERS SUCCEEDED******");
	}

	/**
	 * Execute the namerserver select stamements in the origin database and
	 * store them in the RDAP databse
	 * 
	 * @throws SQLException
	 * @throws InvalidadDataStructure
	 * @throws InvalidValueException
	 * @throws RequiredValueNotFoundException
	 * @throws IOException
	 */
	private void migrateDomains() throws SQLException, RequiredValueNotFoundException, InvalidValueException,
			InvalidadDataStructure, IOException {
		logger.log(Level.INFO, "******MIGRATING DOMAINS STARTING******");
		try (Connection originConnection = MigrationDatabaseSession.getConnection();
				PreparedStatement statement = originConnection.prepareStatement(queries.get("domain"));) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet domainResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing DOMAINS resultset");
			List<DomainDAO> domains = DomainMigrator.getDomainsFromResultSet(domainResultSet);
			logger.log(Level.INFO, "Done!\n DOMAINS retrived:" + domains.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getConnection();) {
				DomainMigrator.storeDomainsInRDAPDatabase(domains, rdapConnection);
			}

		}
		logger.log(Level.INFO, "******MIGRATING DOMAINS SUCCEEDED******");
	}

	/**
	 * Read the migration.sql file and store the queries in the queries hashmap
	 * 
	 * @throws IOException
	 */
	private void readMigrationFile() throws IOException {
		logger.log(Level.INFO, "******READING MIGRATION FILE******");
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
