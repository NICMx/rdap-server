package mx.nic.rdap.server.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.DomainDAO;
import mx.nic.rdap.server.db.EntityDAO;
import mx.nic.rdap.server.db.NameserverDAO;

/**
 * Test for the class MigrationBatch
 * 
 * @author dalpuche
 *
 */
public class MigrationBatchTest {

	private final static Logger logger = Logger.getLogger(MigrationBatchTest.class.getName());

	private String entityQuery = "SELECT \"XXXX2\" AS handle ,\"whois.example.net\" AS port43 ,\"active,validated\" AS rdap_status  ,\"registration| 2011-12-31T23:59:59Z| XXX1, reregistration| 2012-12-01T23:59:59Z | XXX1\" AS events ,\"XXX1 |registrar, XXX2 |reseller\" AS entities ,\"1|IANA Registrar ID, 2|NIC ID \" AS public_ids ,\"Joe Jobs |Orange |www.orange.mx |jj@orange.mx |81 8818181 |81 8181818181 |248.697.0908 |Engineer |local |Mexico |Monterrey |Nuevo Leon |Altavista |100 |Av. Luis Elizondo |64000\" AS vcard FROM dual;";
	private String nameserverQuery = "SELECT   'XXXX' AS handle,     'ns1.xn--fo-5ja.example' AS ldh_name,     'whois.example.net' AS port43,     'active,validated' AS rdap_status,     'linked,ok' AS epp_status,     'registration| 2011-12-31T23:59:59Z| XXX1, reregistration| 2012-12-01T23:59:59Z| XXX1' AS events,     '4| 192.0.2.1, 6| 2001:db8::2:1' AS ip_addresses,     'XXX1|registrar, XXX2|reseller' AS entities FROM DUAL UNION ALL SELECT     'XXX2' AS handle,     'ns2.xn--fo-5ja.example' AS ldh_name,     'whois.example.net' AS port43,     'active,validated' AS rdap_status,     'linked' AS epp_status,     'registration| 2011-12-31T23:59:59Z|' AS events,     '4| 192.0.2.1, 6| 2001:db8::2:1' AS ip_addresses,     'XXX3|registrar' AS entities FROM DUAL;";
	private String domainQuery = "SELECT \"XXXX\" AS handle,\"0.2.192.in-addr.arpa\" AS ldh_name, \"whois.example.net\" AS port43,\"active,validated\" AS rdap_status,  \"registration| 2011-12-31T23:59:59Z | XXX1, reregistration| 2012-12-01T23:59:59Z| XXX1\" AS events, \"XXX1|registrar, XXX2|reseller\" AS entities, \".EXAMPLE Spanish |{xn--fo-cka.example, xn--fo-fka.example} |{unregistered, registration restricted}\" AS variants,  \"XXX1,XXX2\" AS namerservers, \"true|true|12345\" AS secureDNS, \"12345|3|49FD46E6C4B45C55D4AC|1\" AS dsData, \"1|IANA Registrar ID, 2|NIC ID \" AS publicIds FROM dual;";

	@Test
	public void migrationTest() {
		MigrationBatch batch = new MigrationBatch();
		batch.run();
	}

	@Test
	public void migrateEntitiesTest() {
		MigrationInitializer.initOriginDBConnection();
		MigrationInitializer.initRDAPDBConnection();
		logger.log(Level.INFO, "******MIGRATING ENTITIES STARTING******");
		try (Connection originConnection = MigrationDatabaseSession.getConnection();
				PreparedStatement statement = originConnection.prepareStatement(entityQuery);) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet entitiesResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing Entities resultset");
			List<EntityDAO> entities = EntityMigrator.getEntitiesFromResultSet(entitiesResultSet);
			logger.log(Level.INFO, "Done!\n Entities retrived:" + entities.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getConnection();) {
				EntityMigrator.storeEntitiesInRDAPDatabase(entities, rdapConnection);
			}
			logger.log(Level.INFO, "******MIGRATING ENTITIES SUCCEEDED******");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			MigrationInitializer.closeOriginDBConnection();
			MigrationInitializer.closeRDAPDBConnection();
		}

	}

	@Test
	public void migrateNameserversTest() {
		MigrationInitializer.initOriginDBConnection();
		MigrationInitializer.initRDAPDBConnection();
		logger.log(Level.INFO, "******MIGRATING NAMESERVERS STARTING******");
		try (Connection originConnection = MigrationDatabaseSession.getConnection();
				PreparedStatement statement = originConnection.prepareStatement(nameserverQuery);) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet nameserverResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing Nameservers resultset");
			List<NameserverDAO> nameservers = NameserverMigrator.getNameserversFromResultSet(nameserverResultSet);
			logger.log(Level.INFO, "Done!\n Nameservers retrived:" + nameservers.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getConnection();) {
				NameserverMigrator.storeNameserversInRDAPDatabase(nameservers, rdapConnection);
			}
			logger.log(Level.INFO, "******MIGRATING NAMESERVERS SUCCEEDED******");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			MigrationInitializer.closeOriginDBConnection();
			MigrationInitializer.closeRDAPDBConnection();
		}

	}

	@Test
	public void migrateDomainsTest() {
		MigrationInitializer.initOriginDBConnection();
		MigrationInitializer.initRDAPDBConnection();
		logger.log(Level.INFO, "******MIGRATING DOMAINS STARTING******");
		try (Connection originConnection = MigrationDatabaseSession.getConnection();
				PreparedStatement statement = originConnection.prepareStatement(domainQuery);) {
			logger.log(Level.INFO, "Excuting QUERY:" + statement.toString());
			ResultSet domainResultSet = statement.executeQuery();
			logger.log(Level.INFO, "Done!\n Processing DOMAINS resultset");
			List<DomainDAO> nameservers = DomainMigrator.getDomainsFromResultSet(domainResultSet);
			logger.log(Level.INFO, "Done!\n DOMAINS retrived:" + nameservers.size()
					+ "\n Starting to save in RDAP Database. Good luck :)");
			try (Connection rdapConnection = DatabaseSession.getConnection();) {
				DomainMigrator.storeDomainsInRDAPDatabase(nameservers, rdapConnection);
			}
			logger.log(Level.INFO, "******MIGRATING DOMAINS SUCCEEDED******");
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			MigrationInitializer.closeOriginDBConnection();
			MigrationInitializer.closeRDAPDBConnection();
		}

	}

}
