package mx.nic.rdap.server.db;

import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.server.db.model.VCardModel;

/**
 * Tests for the {@link VCardModel}
 * 
 * @author dhfelix
 *
 */
public class VCardTest extends DatabaseTest {

	/**
	 * Connection for this tests
	 */
	private static Connection connection = null;

	@Before
	public void before() throws SQLException {
		connection = DatabaseSession.getRdapConnection();
	}

	@After
	public void after() throws SQLException {
		connection.rollback();
		connection.close();
	}

	/**
	 * Creates a new instance with no objects nested to it and stores it in the
	 * database, then get an instance with the id generated and compares it to
	 * see if they match.
	 */
	@Test
	public void insertAndGetSimpleVCard() {
		Random random = new Random();
		int randomInt = random.nextInt();
		VCard vCard = createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		Long vCardId = null;
		try {
			vCardId = VCardModel.storeToDatabase(vCard, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		VCard byId = null;
		try {
			byId = VCardModel.getById(vCardId, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		Assert.assertTrue("The object created does not match the value returned by the database object.",
				vCard.equals(byId));
	}

	/**
	 * Creates a new instance with objects nested to it and stores it in the
	 * database, then get an instance with the id generated and compares it to
	 * see if they match.
	 */
	@Test
	public void insertAndGetVCardWithPostarlInfo() {
		Random random = new Random();
		int randomInt = random.nextInt();
		VCard vCard = createVCardDao(null, "mi nombre" + randomInt, "company" + randomInt,
				"www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com", "818282569" + randomInt,
				"520448114561234" + randomInt, null, null);

		List<VCardPostalInfo> postalInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoList.add(createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX", "monterrey",
					"Luis Elizondo", null, null, "NL", "66666"));
		}

		vCard.setPostalInfo(postalInfoList);

		Long vCardId = null;
		try {
			vCardId = VCardModel.storeToDatabase(vCard, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		VCard byId = null;
		try {
			byId = VCardModel.getById(vCardId, connection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		Assert.assertTrue("The object created does not match the value returned by the database object.",
				vCard.equals(byId));

	}

	/**
	 * Creates a new instance of {@link VCardDAO} with the incoming attributes.
	 * 
	 * @param id
	 * @param name
	 * @param companyName
	 * @param companyURL
	 * @param email
	 * @param voice
	 * @param cellphone
	 * @param fax
	 * @param jobTitle
	 * @return
	 */
	public static VCardDAO createVCardDao(Long id, String name, String companyName, String companyURL, String email,
			String voice, String cellphone, String fax, String jobTitle) {
		VCardDAO vCard = new VCardDAO();
		vCard.setId(id);
		vCard.setName(name);
		vCard.setCompanyName(companyName);
		vCard.setCompanyURL(companyURL);
		vCard.setEmail(email);
		vCard.setVoice(voice);
		vCard.setCellphone(cellphone);
		vCard.setFax(fax);
		vCard.setJobTitle(jobTitle);
		return vCard;
	}

	/**
	 * Creates a new instance of {@link VCardPostalInfoDAO} with the incoming
	 * attributes.
	 * 
	 * @param id
	 * @param vCardId
	 * @param type
	 * @param country
	 * @param city
	 * @param street1
	 * @param street2
	 * @param street3
	 * @param state
	 * @param postalCode
	 * @return
	 */
	public static VCardPostalInfo createVCardPostalInfo(Long id, Long vCardId, String type, String country, String city,
			String street1, String street2, String street3, String state, String postalCode) {
		VCardPostalInfoDAO postalInfo = new VCardPostalInfoDAO();
		postalInfo.setId(id);
		postalInfo.setVCardId(vCardId);
		postalInfo.setType(type);
		postalInfo.setCountry(country);
		postalInfo.setCity(city);
		postalInfo.setStreet1(street1);
		postalInfo.setStreet2(street2);
		postalInfo.setStreet3(street3);
		postalInfo.setState(state);
		postalInfo.setPostalCode(postalCode);

		return postalInfo;
	}
}
