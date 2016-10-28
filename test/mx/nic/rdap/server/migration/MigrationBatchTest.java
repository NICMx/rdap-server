package mx.nic.rdap.server.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.DatabaseTest;
import mx.nic.rdap.server.db.DomainDAO;
import mx.nic.rdap.server.db.EntityDAO;
import mx.nic.rdap.server.db.NameserverDAO;
import mx.nic.rdap.server.db.RdapUserDAO;

/**
 * Test for the class MigrationBatch
 * 
 * @author dalpuche
 *
 */
public class MigrationBatchTest extends DatabaseTest {

	private final static Logger logger = Logger.getLogger(MigrationBatchTest.class.getName());

	private String entityQuery = "SELECT \"XXXX2\" AS handle ,\"whois.example.net\" AS port43 ,\"active,validated\" AS rdap_status  ,\"registration| 2011-12-31T23:59:59Z| XXXX, reregistration| 2012-12-01T23:59:59Z | XXXX\" AS events ,\"XXXX |registrar, XXX2 |reseller\" AS entities ,\"1|IANA Registrar ID, 2|NIC ID \" AS public_ids ,\"Joe Jobs |Orange |www.orange.mx |jj@orange.mx |81 8818181 |81 8181818181 |248.697.0908 |Engineer |local |Mexico |Monterrey |Nuevo Leon |Altavista |100 |Av. Luis Elizondo |64000\" AS vcard FROM dual;";
	private String nameserverQuery = "SELECT   'XXXX' AS handle,     'ns1.xn--fo-5ja.example' AS ldh_name,     'whois.example.net' AS port43,     'active,validated' AS rdap_status,     'registration| 2011-12-31T23:59:59Z| XXXX, reregistration| 2012-12-01T23:59:59Z| XXXX' AS events,     '4| 192.0.2.1, 6| 2001:db8::2:1' AS ip_addresses,     'XXXX|registrar, XXX2|reseller' AS entities FROM DUAL UNION ALL SELECT     'XXX2' AS handle,     'ns2.xn--fo-5ja.example' AS ldh_name,     'whois.example.net' AS port43,     'active,validated' AS rdap_status,  'registration| 2011-12-31T23:59:59Z|' AS events,     '4| 192.0.2.1, 6| 2001:db8::2:1' AS ip_addresses,     'XXXX2|registrar' AS entities FROM DUAL;";
	private String domainQuery = "SELECT \"XXXX8\" AS handle,\"1.12.129.ip6.arpa\" AS ldh_name, \"whois.example.net\" AS port43,\"active,validated\" AS rdap_status,  \"registration| 2011-12-31T23:59:59Z | XXXX, reregistration| 2012-12-01T23:59:59Z| XXXX\" AS events, \"XXXX|registrar\" AS entities, \".EXAMPLE Spanish |{xn--fo-cka.example, xn--fo-fka.example} |{unregistered, registration restricted}\" AS variants,  \"XXXX\" AS namerservers, \"true|true|12345\" AS secureDNS, \"12345|3|49FD46E6C4B45C55D4AC|1\" AS dsData, \"1|IANA Registrar ID, 2|NIC ID \" AS publicIds FROM dual;";
	private String userQuery = "SELECT 'dalpuche' AS rus_name, 'password1' AS pass, 'AUTHENTICATED' AS rur_name FROM dual;";

	@Test
	public void migrationTest() {
		MigrationBatch batch = new MigrationBatch();
		batch.run();
	}

	@Test
	public void migrateEntitiesTest() {
		logger.log(Level.INFO, "******MIGRATING ENTITIES STARTING******");
		try (Connection originConnection = DatabaseSession.getMigrationConnection();
				PreparedStatement statement = originConnection.prepareStatement(entityQuery)) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet entitiesResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing Entities resultset");
			List<EntityDAO> entities = EntityMigrator.getEntitiesFromResultSet(entitiesResultSet);
			logger.log(Level.INFO, "Done!\n Entities retrived:" + entities.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getRdapConnection();) {
				EntityMigrator.storeEntitiesInRDAPDatabase(entities, rdapConnection);
				rdapConnection.commit();
			}
			logger.log(Level.INFO, "******MIGRATING ENTITIES SUCCEEDED******");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	@Test
	public void migrateNameserversTest() {
		logger.log(Level.INFO, "******MIGRATING NAMESERVERS STARTING******");
		try (Connection originConnection = DatabaseSession.getMigrationConnection();
				PreparedStatement statement = originConnection.prepareStatement(nameserverQuery)) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet nameserverResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing Nameservers resultset");
			List<NameserverDAO> nameservers = NameserverMigrator.getNameserversFromResultSet(nameserverResultSet);
			logger.log(Level.INFO, "Done!\n Nameservers retrived:" + nameservers.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getRdapConnection()) {
				NameserverMigrator.storeNameserversInRDAPDatabase(nameservers, rdapConnection);
				rdapConnection.commit();
			}
			logger.log(Level.INFO, "******MIGRATING NAMESERVERS SUCCEEDED******");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	@Test
	public void migrateDomainsTest() {
		logger.log(Level.INFO, "******MIGRATING DOMAINS STARTING******");
		try (Connection originConnection = DatabaseSession.getMigrationConnection();
				PreparedStatement statement = originConnection.prepareStatement(domainQuery)) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet domainResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing DOMAINS resultset");
			List<DomainDAO> nameservers = DomainMigrator.getDomainsFromResultSet(domainResultSet);
			logger.log(Level.INFO, "Done!\n DOMAINS retrived:" + nameservers.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getRdapConnection()) {
				DomainMigrator.storeDomainsInRDAPDatabase(nameservers, rdapConnection);
				rdapConnection.commit();
			}

			logger.log(Level.INFO, "******MIGRATING DOMAINS SUCCEEDED******");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	public void migrateUsersTest() {
		logger.log(Level.INFO, "******STARTING USERS MIGRATION******");
		try (Connection originConnection = DatabaseSession.getMigrationConnection();
				PreparedStatement statement = originConnection.prepareStatement(userQuery)) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet entitiesResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing Users resultset");
			List<RdapUserDAO> users = UserMigrator.getUsersFromResultSet(entitiesResultSet);
			logger.log(Level.INFO,
					"Done!\n Users retrived:" + users.size() + "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getRdapConnection()) {
				UserMigrator.storeDomainsInRDAPDatabase(users, rdapConnection);
				rdapConnection.commit();
			}
			logger.log(Level.INFO, "******MIGRATING DOMAINS SUCCEEDED******");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	public void cleanRdapDbTest() {
		try (Connection rdapConnection = DatabaseSession.getRdapConnection()) {
			MigrationBatch batch = new MigrationBatch();
			batch.cleanServerDatabase(rdapConnection);
			rdapConnection.commit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
