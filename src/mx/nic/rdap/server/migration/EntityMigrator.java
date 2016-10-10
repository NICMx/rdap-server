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

import mx.nic.rdap.server.db.EntityDAO;
import mx.nic.rdap.server.db.VCardDAO;
import mx.nic.rdap.server.db.VCardPostalInfoDAO;
import mx.nic.rdap.server.db.model.EntityModel;
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.InvalidadDataStructure;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Class that process the entities from the client's database and store them in
 * the RDAP database
 * 
 * @author dalpuche
 *
 */
public class EntityMigrator {

	private final static Logger logger = Logger.getLogger(EntityMigrator.class.getName());

	/**
	 * Process the resultSet of the select statement and return a list of
	 * Entities
	 * 
	 * @param entitiesResultSet
	 * @return
	 * @throws SQLException
	 * @throws RequiredValueNotFoundException
	 * @throws InvalidValueException
	 * @throws InvalidadDataStructure
	 */
	public static List<EntityDAO> getEntitiesFromResultSet(ResultSet result)
			throws SQLException, RequiredValueNotFoundException, InvalidValueException, InvalidadDataStructure {
		List<EntityDAO> entities = new ArrayList<EntityDAO>();
		while (result.next()) {
			EntityDAO entity = new EntityDAO();
			try {
				if (MigrationUtil.isResultSetValueValid(result.getString("handle")))
					entity.setHandle(result.getString("handle").trim());
				else {
					throw new RequiredValueNotFoundException("Handle", "Entity");
				}
			} catch (SQLException e) {
				throw new RequiredValueNotFoundException("Handle", "Entity");
			}
			try {
				if (MigrationUtil.isResultSetValueValid(result.getString("port43")))
					entity.setPort43(result.getString("port43").trim());
				else {
					throw new RequiredValueNotFoundException("Port43", "Entity");
				}
			} catch (SQLException e) {
				throw new RequiredValueNotFoundException("Port43", "Entity");
			}
			try {
				if (MigrationUtil.isResultSetValueValid(result.getString("rdap_status"))) {
					entity.setStatus(MigrationUtil.getRDAPStatusFromResultSet(result.getString("rdap_status")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "rdap_status column not found");// Not
																			// a
																			// required
																			// value
			}
			try {
				if (MigrationUtil.isResultSetValueValid(result.getString("epp_status"))) {
					entity.getStatus()
							.addAll(MigrationUtil.getRDAPStatusFromEPPStatusResultSet(result.getString("epp_status")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "epp_status column not found");// Not
																			// a
																			// required
																			// value
			}
			try {
				if (MigrationUtil.isResultSetValueValid(result.getString("events"))) {
					entity.setEvents(MigrationUtil.getEventsFromResultSet(result.getString("events")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "events column not found");// Not a
																		// required
																		// value
			}
			try {
				if (MigrationUtil.isResultSetValueValid(result.getString("entities"))) {
					entity.setEntities(MigrationUtil.getEntityAndRolesFromResultSet(result.getString("entities")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "entities column not found");// Not a
																		// required
																		// value
			}
			try {
				if (MigrationUtil.isResultSetValueValid(result.getString("public_ids"))) {
					entity.setPublicIds(MigrationUtil.getPublicIdsFromResultSet(result.getString("public_ids")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "public_ids column not found");// Not
																			// a
																			// required
																			// value

			}
			try {
				if (MigrationUtil.isResultSetValueValid(result.getString("vcard"))) {
					entity.getVCardList().add(getVCardFromResultSet(result.getString("vcard")));
				}
			} catch (SQLException e) {
				logger.log(Level.WARNING, "vcard column not found");// Not a
																	// required
																	// value
			}
			entities.add(entity);
		}
		return entities;
	}

	/**
	 * @param entities
	 * @param rdapConnection
	 */
	public static void storeEntitiesInRDAPDatabase(List<EntityDAO> entities, Connection con) {
		for (EntityDAO entity : entities) {
			try {
				EntityModel.storeToDatabase(entity, con);
			} catch (IOException | SQLException | RequiredValueNotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
		}

	}

	/**
	 * Split the resultSet String to a Vcard
	 * 
	 * @param result
	 *            must have the form “name | companyName | companyUrl | email |
	 *            voice | cellphone | fax | jobTitle | addressType | country |
	 *            city | state | street1 | street2 | street3 | postalCode”
	 * @return
	 * @throws InvalidadDataStructure
	 * @throws RequiredValueNotFoundException
	 */
	private static VCardDAO getVCardFromResultSet(String result)
			throws InvalidadDataStructure, RequiredValueNotFoundException {
		VCardDAO vcard = new VCardDAO();
		List<String> dataList = Arrays.asList(result.split("\\|"));
		if (dataList.size() == 16) {// Verify the size of the list, if contains
									// all the attributes
			if (MigrationUtil.isResultSetValueValid(dataList.get(0))) {
				vcard.setName(dataList.get(0).trim());
			} else {
				throw new RequiredValueNotFoundException("Name", "VCard");
			}
			if (MigrationUtil.isResultSetValueValid(dataList.get(1)))
				vcard.setCompanyName(dataList.get(1).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(2)))
				vcard.setCompanyURL(dataList.get(2).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(3)))
				vcard.setEmail(dataList.get(3).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(4)))
				vcard.setVoice(dataList.get(4).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(5)))
				vcard.setCellphone(dataList.get(5).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(6)))
				vcard.setFax(dataList.get(6).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(7)))
				vcard.setJobTitle(dataList.get(7).trim());

			VCardPostalInfoDAO postalInfo = new VCardPostalInfoDAO();

			if (MigrationUtil.isResultSetValueValid(dataList.get(8)))
				postalInfo.setType(dataList.get(8).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(9)))
				postalInfo.setCountry(dataList.get(9).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(10)))
				postalInfo.setCity(dataList.get(10).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(11)))
				postalInfo.setState(dataList.get(11).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(12)))
				postalInfo.setStreet1(dataList.get(12).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(13)))
				postalInfo.setStreet2(dataList.get(13).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(14)))
				postalInfo.setStreet3(dataList.get(14).trim());
			if (MigrationUtil.isResultSetValueValid(dataList.get(15)))
				postalInfo.setPostalCode(dataList.get(15).trim());

			vcard.getPostalInfo().add(postalInfo);

		} else {
			throw new InvalidadDataStructure("VCardData",
					"name | companyName | companyUrl | email |voice | cellphone | fax | jobTitle | addressType | country |  city | state | street1 | street2 | street3 | postalCode");
		}

		return vcard;
	}

}
