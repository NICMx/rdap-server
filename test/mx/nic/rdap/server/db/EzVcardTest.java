package mx.nic.rdap.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.parameter.AddressType;
import ezvcard.parameter.EmailType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import mx.nic.rdap.core.db.VCardPostalInfo;

public class EzVcardTest extends DatabaseTest {

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
	 * Creates a Json with the VCard Object from ezVcard
	 */
	@Test
	public void generateJson() {

		Random random = new Random();
		int randomInt = random.nextInt();

		// generates nic VCard Object
		mx.nic.rdap.core.db.VCard nicVCard = VCardTest.createVCardDao(null, "mi nombre" + randomInt,
				"company" + randomInt, "www.companytest" + randomInt + ".com", "correo" + randomInt + "@correo.com",
				"818282569" + randomInt, "520448114561234" + randomInt, null, null);

		// generates VCard address using nic Classes
		List<VCardPostalInfo> postalInfoList = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			postalInfoList.add(VCardTest.createVCardPostalInfo(null, null, "mytype" + random.nextInt(), "MX",
					"monterrey", "Luis Elizondo", null, null, "NL", "66666"));
		}

		VCard vcard = generateVCard(nicVCard, postalInfoList);

		String json = Ezvcard.writeJson(vcard).go();

		System.out.println(json);

	}

	/**
	 * Generates an object VCard using EzVcard Libraries
	 * 
	 * @param nicVCard
	 * @param postalInfoList
	 * @return
	 */
	public VCard generateVCard(mx.nic.rdap.core.db.VCard nicVCard, List<VCardPostalInfo> postalInfoList) {

		VCard vcard = new VCard();

		vcard.setFormattedName(nicVCard.getName());
		vcard.setOrganization(nicVCard.getCompanyName());
		vcard.addUrl(nicVCard.getCompanyURL());
		vcard.addEmail(nicVCard.getEmail(), EmailType.WORK);
		vcard.addTelephoneNumber(nicVCard.getVoice(), TelephoneType.VOICE);
		vcard.addTelephoneNumber(nicVCard.getCellphone(), TelephoneType.CELL);
		vcard.addTelephoneNumber(nicVCard.getFax(), TelephoneType.FAX);
		vcard.addTitle(nicVCard.getJobTitle());

		for (VCardPostalInfo adr : postalInfoList) {
			Address address = new Address();
			address.getTypes().add(AddressType.WORK);

			address.getStreetAddresses().add(adr.getStreet1());
			address.getStreetAddresses().add(adr.getStreet2());
			address.getStreetAddresses().add(adr.getStreet3());
			address.setPostalCode(adr.getPostalCode());
			address.setRegion(adr.getState());
			address.setLocality(adr.getCity());
			address.setCountry(adr.getCountry());
			vcard.addAddress(address);
		}
		return vcard;

	}

	/**
	 * VCardDao(Long id, String name, String companyName, String companyURL,
	 * String email, String voice, String cellphone, String fax, String
	 * jobTitle)
	 * 
	 * public static VCardPostalInfo VCardPostalInfo(Long id, Long vCardId,
	 * String type, String country, String city, String street1, String street2,
	 * String street3, String state, String postalCode)
	 */

}
