package mx.nic.rdap.server.migration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.core.db.DsData;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.server.db.DomainDAO;
import mx.nic.rdap.server.db.DsDataDAO;
import mx.nic.rdap.server.db.NameserverDAO;
import mx.nic.rdap.server.db.SecureDNSDAO;
import mx.nic.rdap.server.db.VariantDAO;
import mx.nic.rdap.server.db.model.DomainModel;
import mx.nic.rdap.server.db.model.ZoneModel;
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.InvalidadDataStructure;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.VariantRelation;

/**
 * Class used to process the domains from the client´s database and stores them
 * into the RDAP's database
 * 
 * @author evaldes
 *
 */
public class DomainMigrator {

	static Logger logger = Logger.getLogger(DomainMigrator.class.getName());

	/**
	 * Process the resultSet of the select statement and returns a list of
	 * Domains
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 * @throws InvalidValueException
	 * @throws InvalidadDataStructure
	 * @throws RequiredValueNotFoundException
	 */
	public static List<DomainDAO> getDomainsFromResultSet(ResultSet resultSet)
			throws SQLException, InvalidValueException, RequiredValueNotFoundException, InvalidadDataStructure {
		List<DomainDAO> domains = new ArrayList<DomainDAO>();
		while (resultSet.next()) {
			DomainDAO domain = new DomainDAO();

			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("handle"))) {
					domain.setHandle(resultSet.getString("handle").trim());
				} else {
					throw new RequiredValueNotFoundException("Handle", "Domain");
				}
			} catch (SQLException e) {
				throw new RequiredValueNotFoundException("Handle", "Domain");
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("ldh_name"))) {
					domain.setLdhName(resultSet.getString("ldh_name").trim());
				} else {
					throw new RequiredValueNotFoundException("LDHName", "Domain");
				}
			} catch (SQLException e) {
				throw new RequiredValueNotFoundException("LDHName", "Domain");
			}
			String[] domainData = domain.getLdhName().split("\\.");
			try {
				String domainZone = domain.getLdhName().substring(domainData[0].length() + 1);
				if (MigrationUtil.isResultSetValueValid(domainZone)) {
					domain.setZoneId(ZoneModel.getIdByZoneName(domainZone));
				} else {
					throw new RequiredValueNotFoundException("Zone", "Domain");
				}
				if (domain.getZoneId() == null) {
					throw new InvalidValueException("Zone", "Domain", domainZone);
				}
			} catch (IndexOutOfBoundsException iobe) {
				throw new RequiredValueNotFoundException("Zone", "Domain");
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("port43"))) {
					domain.setPort43(resultSet.getString("port43").trim());
				} else {
					throw new RequiredValueNotFoundException("port43", "Domain");
				}
			} catch (SQLException e) {
				throw new RequiredValueNotFoundException("port43", "Domain");
			}

			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("rdap_status"))) {
					domain.setStatus(MigrationUtil.getRDAPStatusFromResultSet(resultSet.getString("rdap_status")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "rdap_status column not found");// Not
																			// a
																			// required
																			// value.
			}

			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("epp_status"))) {
					domain.getStatus().addAll(
							MigrationUtil.getRDAPStatusFromEPPStatusResultSet(resultSet.getString("epp_status")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "epp_status column not found");// Not
																			// a
																			// required
																			// value.
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("events"))) {
					domain.setEvents(MigrationUtil.getEventsFromResultSet(resultSet.getString("events")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "events column not found");// Not
																		// a
																		// required
																		// value.
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("entities"))) {
					domain.setEntities(MigrationUtil.getEntityAndRolesFromResultSet(resultSet.getString("entities")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "entities column not found");// Not
																		// a
																		// required
																		// value.
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("variants"))) {
					domain.setVariants(getVariantsFromResultSet(resultSet.getString("variants")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "variants column not found");// Not
																		// a
																		// required
																		// value.
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("nameservers"))) {
					domain.setNameServers(getNameserversFromResultSet(resultSet.getString("nameservers")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "nameservers column not found");// Not
																			// a
																			// required
																			// value.
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("secure_dns"))) {
					domain.setSecureDNS(getSecureDnsFromResultSet(resultSet.getString("secure_dns"),
							resultSet.getString("dsData")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "secure_dns column not found");// Not
																			// a
																			// required
																			// value.
			}
			try {
				if (MigrationUtil.isResultSetValueValid(resultSet.getString("public_ids"))) {
					domain.setPublicIds(MigrationUtil.getPublicIdsFromResultSet(resultSet.getString("public_ids")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "public_ids column not found");// Not
																			// a
																			// required
																			// value.
			}
			domains.add(domain);
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
	 * @throws InvalidValueException
	 * @throws InvalidadDataStructure
	 */
	private static SecureDNS getSecureDnsFromResultSet(String stringSecureDns, String stringDsData)
			throws InvalidadDataStructure, InvalidValueException {
		SecureDNSDAO secureDns = new SecureDNSDAO();

		List<String> secureDnsData = Arrays.asList(stringSecureDns.trim().split("\\|"));

		if (secureDnsData.size() != 3) {
			throw new InvalidadDataStructure();
		}

		String zoneSigned = secureDnsData.get(0);
		String delegationSigned = secureDnsData.get(1);
		String maxSigLife = secureDnsData.get(2);

		if (MigrationUtil.isResultSetValueValid(zoneSigned)) {
			try {
				secureDns.setZoneSigned(Boolean.parseBoolean(zoneSigned.trim()));
			} catch (NumberFormatException e) {
				throw new InvalidValueException("Keytag", "DsData", zoneSigned);
			}
		} else {
			secureDns.setZoneSigned(false);
		}

		if (MigrationUtil.isResultSetValueValid(delegationSigned)) {
			try {
				secureDns.setDelegationSigned(Boolean.parseBoolean(delegationSigned.trim()));
			} catch (NumberFormatException e) {
				throw new InvalidValueException("Keytag", "DsData", delegationSigned);
			}
		} else {
			secureDns.setDelegationSigned(false);
		}

		if (MigrationUtil.isResultSetValueValid(maxSigLife)) {
			try {
				secureDns.setMaxSigLife(Integer.parseInt(maxSigLife.trim()));
			} catch (NumberFormatException e) {
				throw new InvalidValueException("Keytag", "DsData", maxSigLife);
			}
		}

		secureDns.setZoneSigned(Boolean.parseBoolean(zoneSigned.trim()));
		secureDns.setDelegationSigned(Boolean.parseBoolean(delegationSigned.trim()));
		secureDns.setMaxSigLife(Integer.parseInt(maxSigLife.trim()));

		if (MigrationUtil.isResultSetValueValid(stringDsData)) {
			secureDns.setDsData(getDsDataFromResultSet(stringDsData));
		}

		return secureDns;
	}

	/**
	 * Process the DsData structure and gets all of it's values
	 * 
	 * @param stringDsData
	 *            must have the form “keyTag|algorithm|digest|digestType”
	 * @return
	 * @throws InvalidadDataStructure
	 * @throws InvalidValueException
	 */
	private static List<DsData> getDsDataFromResultSet(String stringDsData)
			throws InvalidadDataStructure, InvalidValueException {

		List<DsData> dsDatas = new ArrayList<DsData>();
		stringDsData = stringDsData + "";// Add value if last character was a
											// pipe
		DsDataDAO dsData = new DsDataDAO();

		List<String> dsDataData = Arrays.asList(stringDsData.trim().split("\\|"));
		if (dsDataData.size() != 4) {
			throw new InvalidadDataStructure();
		}
		String keytag = dsDataData.get(0);
		String algorithm = dsDataData.get(1);
		String digest = dsDataData.get(2);
		String digestType = dsDataData.get(3);

		if (MigrationUtil.isResultSetValueValid(keytag)) {
			try {
				dsData.setKeytag(Integer.parseInt(keytag.trim()));
			} catch (NumberFormatException e) {
				throw new InvalidValueException("Keytag", "DsData", keytag);
			}
		}

		if (MigrationUtil.isResultSetValueValid(algorithm)) {
			try {
				dsData.setAlgorithm(Integer.parseInt(algorithm.trim()));
			} catch (NumberFormatException e) {
				throw new InvalidValueException("Algorithm", "DsData", algorithm);
			}
		}

		if (MigrationUtil.isResultSetValueValid(digest)) {
			dsData.setDigest(digest.trim());
		}

		if (MigrationUtil.isResultSetValueValid(digestType)) {
			try {
				dsData.setAlgorithm(Integer.parseInt(digestType.trim()));
			} catch (NumberFormatException e) {
				throw new InvalidValueException("DigestType", "DsData", digestType);
			}
		}
		dsDatas.add(dsData);
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
			nameserver.setHandle(handle.trim());
			return nameservers;
		}
		return nameservers;
	}

	/**
	 * Process a ResultSet of Variants and gets all of it's values
	 * 
	 * @param string
	 *            String containing the Variants list, the structure must have
	 *            the form: "idnTable | {variantName1,variantName2}|
	 *            {variantRelation1,variantRelation2}"
	 * @return
	 * @throws InvalidadDataStructure
	 * @throws RequiredValueNotFoundException
	 * @throws InvalidValueException
	 */
	private static List<Variant> getVariantsFromResultSet(String string)
			throws InvalidadDataStructure, RequiredValueNotFoundException, InvalidValueException {
		List<Variant> variants = new ArrayList<Variant>();
		string = string.replaceAll("} ", "}") + " "; // make
														// sures
														// all
														// variants
														// will
														// split
		if (string != null && !string.trim().isEmpty()) {
			List<String> variantsData = Arrays.asList(string.split("\\},"));
			for (String variantDataString : variantsData) {
				VariantDAO variant = new VariantDAO();
				List<String> variantData = Arrays.asList(variantDataString.split("\\|"));
				if (variantData.size() != 3) {
					throw new InvalidadDataStructure("variantData",
							"idnTable | {variantName1,variantName2}| {variantRelation1,variantRelation2}");
				}

				String idnTable = variantData.get(0);
				String variantNamesData = variantData.get(1);

				VariantName variantName = new VariantName();
				variantNamesData = variantNamesData.replaceAll("\\{", "").replaceAll("\\}", "");// removes
				// braces
				String variantRelationsData = variantData.get(2);
				variantRelationsData = variantRelationsData.replaceAll("\\{", "").replaceAll("\\}", "");// removes
				// braces

				if (MigrationUtil.isResultSetValueValid(idnTable)) {
					variant.setIdnTable(idnTable.trim());
				} else {
					throw new RequiredValueNotFoundException("IdnTable", "Variant");
				}

				if (MigrationUtil.isResultSetValueValid(variantNamesData)) {
					List<String> variantNames = Arrays.asList(variantNamesData.split(","));

					for (String variantNameString : variantNames) {
						if (MigrationUtil.isResultSetValueValid(variantNameString)) {
							variantName.setLdhName(variantNameString.trim());
							variant.getVariantNames().add(variantName);
						} else {
							throw new RequiredValueNotFoundException("VariantName", "Variant");
						}
					}

				} else {
					throw new RequiredValueNotFoundException("VariantNames", "Variant");
				}

				List<String> variantRelations = Arrays.asList(variantRelationsData.split(","));
				for (String relationData : variantRelations) {
					if (MigrationUtil.isResultSetValueValid(variantRelationsData)) {
						VariantRelation relation = VariantRelation.getByName(relationData.trim());
						if (relation != null) {
							variant.getRelations().add(relation);
						} else {
							throw new InvalidValueException("Relation", "Variant", relationData);
						}
					} else {
						throw new RequiredValueNotFoundException("VariantRelation", "Variant");
					}
				}

			}

		}
		return variants;

	}

	/**
	 * Store the domains in the RDAP database
	 * 
	 * @param domains
	 * @param con
	 * @throws RequiredValueNotFoundException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void storeDomainsInRDAPDatabase(List<DomainDAO> domains, Connection con)
			throws IOException, SQLException, RequiredValueNotFoundException {
		for (DomainDAO domain : domains) {
			DomainModel.storeToDatabase(domain, con);
		}
	}

}
