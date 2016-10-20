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
import java.util.SortedSet;
import java.util.TimerTask;
import java.util.TreeSet;
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
	private HashMap<String, String> selectQueries = new HashMap<>();
	private HashMap<String, String> deleteQueries = new HashMap<>();
	private final String MIGRATION_STATEMENTS_FILEPATH = "META-INF/migration/migration.sql";
	private final String DELETE_STATEMENTS_FILEPATH = "META-INF/sql/Delete.sql";

	public MigrationBatch() {
		try {
			logger.log(Level.INFO, "******READING MIGRATION FILE******");
			readQueryFile(MIGRATION_STATEMENTS_FILEPATH, selectQueries);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "******ERROR READING SQL FILE******");
			throw new RuntimeException(e);
		}
	}

	public void run() {
		logger.log(Level.INFO, "******INITIALIZING DATABASE CONNECTIONS******");
		MigrationInitializer.initOriginDBConnection();
		MigrationInitializer.initRDAPDBConnection();
		try (Connection rdapConnection = DatabaseSession.getConnection();
				Connection originConnection = MigrationDatabaseSession.getConnection()) {
			cleanServerDatabase(rdapConnection);
			migrate(rdapConnection, originConnection);
			logger.log(Level.INFO, "******MIGRATION SUCCESS******");
			rdapConnection.commit();
		} catch (IOException | RequiredValueNotFoundException | InvalidValueException | InvalidadDataStructure
				| SQLException e) {
			logger.log(Level.SEVERE, "******MIGRATION FAILED******");
			logger.log(Level.SEVERE, e.getMessage());
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
	public void migrate(Connection rdapConnection, Connection originConnection) throws RequiredValueNotFoundException,
			InvalidValueException, InvalidadDataStructure, IOException, SQLException {
		migrateEntities(rdapConnection, originConnection);
		migrateNameservers(rdapConnection, originConnection);
		migrateDomains(rdapConnection, originConnection);

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
	private void migrateEntities(Connection rdapConnection, Connection originConnection) throws SQLException,
			RequiredValueNotFoundException, InvalidValueException, InvalidadDataStructure, IOException {
		logger.log(Level.INFO, "******STARTING ENTITIES MIGRATION******");
		try (PreparedStatement statement = originConnection.prepareStatement(selectQueries.get("entity"));) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet entitiesResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing Entities resultset");
			List<EntityDAO> entities = EntityMigrator.getEntitiesFromResultSet(entitiesResultSet);
			logger.log(Level.INFO, "Done!\n Entities retrived:" + entities.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			EntityMigrator.storeEntitiesInRDAPDatabase(entities, rdapConnection);

		}
		logger.log(Level.INFO, "******MIGRATING ENTITIES SUCCESSFUL******");
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
	private void migrateNameservers(Connection rdapConnection, Connection originConnection) throws SQLException,
			RequiredValueNotFoundException, InvalidValueException, InvalidadDataStructure, IOException {
		logger.log(Level.INFO, "******STARTING NAMESERVERS MIGRATION******");
		try (PreparedStatement statement = originConnection.prepareStatement(selectQueries.get("nameserver"));) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet nameserverResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing Nameservers resultset");
			List<NameserverDAO> nameservers = NameserverMigrator.getNameserversFromResultSet(nameserverResultSet);
			logger.log(Level.INFO, "Done!\n Nameservers retrived:" + nameservers.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			NameserverMigrator.storeNameserversInRDAPDatabase(nameservers, rdapConnection);

		}
		logger.log(Level.INFO, "******MIGRATING NAMESERVERS SUCCESSFUL******");
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
	private void migrateDomains(Connection rdapConnection, Connection originConnection) throws SQLException,
			RequiredValueNotFoundException, InvalidValueException, InvalidadDataStructure, IOException {
		logger.log(Level.INFO, "******STARTING DOMAINS MIGRATION******");
		try (PreparedStatement statement = originConnection.prepareStatement(selectQueries.get("domain"));) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet domainResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing DOMAINS resultset");
			List<DomainDAO> domains = DomainMigrator.getDomainsFromResultSet(domainResultSet);
			logger.log(Level.INFO, "Done!\n DOMAINS retrived:" + domains.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			DomainMigrator.storeDomainsInRDAPDatabase(domains, rdapConnection);

		}
		logger.log(Level.INFO, "******MIGRATING DOMAINS SUCCESSFUL******");
	}

	/**
	 * Clean the database of the server
	 * 
	 * @param rdapConnection
	 * @throws IOException
	 * @throws SQLException
	 */
	public void cleanServerDatabase(Connection rdapConnection) throws IOException, SQLException {
		logger.log(Level.INFO, "******CLEANING RDAP DATABASE******");
		readQueryFile(DELETE_STATEMENTS_FILEPATH, deleteQueries);
		SortedSet<String> keys = new TreeSet<String>(deleteQueries.keySet());// Order
																				// the
																				// querys
		for (String queryName : keys) {
			try (PreparedStatement statement = rdapConnection.prepareStatement(deleteQueries.get(queryName));) {
				logger.log(Level.INFO, "Excuting QUERY" + queryName);
				statement.executeUpdate();
			}

		}
		logger.log(Level.INFO, "******CLEANING RDAP DATABASE SUCCESSFUL******");
	}

	/**
	 * Read the querys file and store the statements in the queries hashmap
	 * 
	 * @throws IOException
	 */
	private void readQueryFile(String filePath, HashMap<String, String> queryMap) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(MigrationBatchTest.class.getClassLoader().getResourceAsStream(filePath)))) {

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
							String oldQuery = queryMap.put(objectName, queryString.toString());
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
