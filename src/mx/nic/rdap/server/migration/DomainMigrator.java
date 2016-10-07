package mx.nic.rdap.server.migration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx.nic.rdap.core.db.DsData;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.server.db.DomainDAO;
import mx.nic.rdap.server.db.DsDataDAO;
import mx.nic.rdap.server.db.NameserverDAO;
import mx.nic.rdap.server.db.SecureDNSDAO;

/**
 * Class used to process the domains from the client´s database and stores them
 * into the RDAP's database
 * 
 * @author evaldes
 *
 */
public class DomainMigrator {

	/**
	 * Process the resultSet of the select statement and returns a list of
	 * Domains
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	public static List<DomainDAO> getDomainsFromResultSet(ResultSet resultSet) throws SQLException {
		List<DomainDAO> domains = new ArrayList<DomainDAO>();
		while (resultSet.next()) {
			DomainDAO domain = new DomainDAO();

			if (MigrationUtil.isResultSetValueValid(resultSet.getString("handle"))) {
				domain.setHandle(resultSet.getString("handle").trim());
			} else {
				throw new RuntimeException("Domain's hanle can't be null.");
			}
			if (MigrationUtil.isResultSetValueValid("ldh_name")) {
				domain.setLdhName(resultSet.getString("ldh_name").trim());
			} else {
				throw new RuntimeException("Domain's ldh_name can't be null.");
			}
			if (MigrationUtil.isResultSetValueValid(resultSet.getString("port43"))) {
				domain.setPort43(resultSet.getString("port43").trim());
			} else {
				throw new RuntimeException("Domain´s port43 can't be null.");
			}
			if (MigrationUtil.isResultSetValueValid(resultSet.getString("rdap_status"))) {
				domain.setStatus(MigrationUtil.getRDAPStatusFromResultSet(resultSet.getString("rdap_status")));
			}
			if (MigrationUtil.isResultSetValueValid(resultSet.getString("epp_status"))) {
				domain.getStatus().addAll(MigrationUtil.getRDAPStatusFromResultSet(resultSet.getString("epp_status")));
			}
			if (MigrationUtil.isResultSetValueValid(resultSet.getString("events"))) {
				domain.setEvents(MigrationUtil.getEventsFromResultSet(resultSet.getString("events")));
			}
			if (MigrationUtil.isResultSetValueValid(resultSet.getString("entities"))) {
				domain.setEntities(MigrationUtil.getEntityAndRolesFromResultSet(resultSet.getString("entities")));
			}
			if (MigrationUtil.isResultSetValueValid(resultSet.getString("variants"))) {
				domain.setVariants(getVariantsFromResultSet(resultSet.getString("variants")));
			}
			if (MigrationUtil.isResultSetValueValid(resultSet.getString("nameservers"))) {
				domain.setNameServers(getNameserversFromResultSet(resultSet.getString("nameservers")));
			}
			if (MigrationUtil.isResultSetValueValid(resultSet.getString("secure_dns"))) {
				domain.setSecureDNS(
						getSecureDnsFromResultSet(resultSet.getString("secure_dns"), resultSet.getString("dsData")));
			}
			if (MigrationUtil.isResultSetValueValid(resultSet.getString("public_ids"))) {
				domain.setPublicIds(MigrationUtil.getPublicIdsFromResultSet(resultSet.getString("public_ids")));
			}
		}
		return domains;
	}

	/**
	 * Process the SecureDns structure and gets all of it's values
	 * 
	 * @param stringSecureDns
	 *            must have the form “zoneSigned|delegationSigned|maxSigLife”
	 *            where zoneSigned and delegationSigned are true or false, also
	 *            maxSigLife must be an int type value
	 * @param stringDsData
	 *            must have the form “keyTag|algorithm|digest|digestType”
	 * @return
	 */
	private static SecureDNS getSecureDnsFromResultSet(String stringSecureDns, String stringDsData) {
		SecureDNSDAO secureDns = new SecureDNSDAO();
		if (countPipes(stringSecureDns) == 2) {

			List<String> secureDnsData = Arrays.asList(stringSecureDns.trim().split("|"));
			String zoneSigned = secureDnsData.get(0);
			String delegationSigned = secureDnsData.get(1);
			String maxSigLife = secureDnsData.get(2);

			// TODO validate parse
			secureDns.setZoneSigned(Boolean.parseBoolean(zoneSigned));
			secureDns.setDelegationSigned(Boolean.parseBoolean(delegationSigned));
			secureDns.setMaxSigLife(Integer.parseInt(maxSigLife));

			if (MigrationUtil.isResultSetValueValid(stringDsData)) {
				secureDns.setDsData(getDsDataFromResultSet(stringDsData));
			}
		} else {
			throw new RuntimeException("Required value not found.");
		}

		return secureDns;
	}

	/**
	 * Process the DsData structure and gets all of it's values
	 * 
	 * @param stringDsData
	 *            must have the form “keyTag|algorithm|digest|digestType”
	 * @return
	 */
	private static List<DsData> getDsDataFromResultSet(String stringDsData) {
		List<DsData> dsDatas = new ArrayList<DsData>();
		if (countPipes(stringDsData) == 3) {
			DsDataDAO dsData = new DsDataDAO();
			List<String> dsDataData = Arrays.asList(stringDsData.trim().split("|"));
			String keytag = dsDataData.get(0);
			String algorithm = dsDataData.get(1);
			String digest = dsDataData.get(2);
			String digestType = dsDataData.get(3);
			// TODO validate parseInt
			dsData.setKeytag(Integer.parseInt(keytag));
			dsData.setAlgorithm(Integer.parseInt(algorithm));
			dsData.setDigest(digest);
			dsData.setDigestType(Integer.parseInt(digestType));

		} else {
			throw new RuntimeException("Required value not found.");
		}
		return dsDatas;
	}

	/**
	 * Process a ResultSet and gets all of it-s nameserver handlers
	 * 
	 * @param stringNameserver
	 *            String containing the nameserver list of the domain. The
	 *            structure must have the form: “nameserver1Handle,
	 *            nameserver2Handle”
	 * 
	 * @return
	 */
	private static List<Nameserver> getNameserversFromResultSet(String stringNameserver) {
		List<Nameserver> nameservers = new ArrayList<Nameserver>();
		List<String> handlers = Arrays.asList(stringNameserver.trim().split(","));
		for (String handle : handlers) {
			NameserverDAO nameserver = new NameserverDAO();
			nameserver.setHandle(handle);
			return nameservers;
		}
		return nameservers;
	}

	private static List<Variant> getVariantsFromResultSet(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Counts pipes ("|") in a string, used to validate if the result set had
	 * the correct string format
	 * 
	 * @param string
	 * @return
	 */
	private static Integer countPipes(String string) {
		Integer count = string.length() - string.replaceAll("|", "").length();
		return count;
	}
}
