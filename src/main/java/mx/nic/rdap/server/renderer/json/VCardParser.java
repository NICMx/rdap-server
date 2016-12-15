package mx.nic.rdap.server.renderer.json;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import ezvcard.parameter.AddressType;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.server.PrivacyUtil;
import mx.nic.rdap.server.catalog.PrivacyStatus;

public class VCardParser {

	public static JsonArray getJson(VCard vCard, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = PrivacyUtil.getVCardPrivacySettings();

		JsonArrayBuilder jCard = Json.createArrayBuilder();
		jCard.add("vcard");
		JsonArrayBuilder attributesArray = Json.createArrayBuilder();
		attributesArray.add(getVersion("4.0"));

		String key = "name";
		String value = vCard.getName();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			attributesArray.add(getFN(value));

		key = "companyName";
		value = vCard.getCompanyName();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			attributesArray.add(getOrg(value));

		key = "companyUrl";
		value = vCard.getCompanyURL();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			attributesArray.add(getUrl(value));

		key = "mail";
		value = vCard.getEmail();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			attributesArray.add(getEmail(value));

		key = "voice";
		value = vCard.getVoice();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			attributesArray.add(getVoice(value));

		key = "cellphone";
		value = vCard.getCellphone();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			attributesArray.add(getCellphone(value));

		key = "fax";
		value = vCard.getFax();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			attributesArray.add(getFax(value));

		key = "jobTitle";
		value = vCard.getJobTitle();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			attributesArray.add(getTitle(value));

		key = "postalInfo";
		if (PrivacyUtil.isObjectVisible(vCard.getPostalInfo(), key, settings.get(key), isAuthenticated, isOwner)) {
			for (VCardPostalInfo postalInfo : vCard.getPostalInfo()) {
				JsonArray vCardAddressArray = getVCardAddressArray(postalInfo, settings, isAuthenticated, isOwner);
				attributesArray.add(vCardAddressArray);
			}
		}

		jCard.add(attributesArray.build());

		return jCard.build();
	}

	private static JsonArray getVersion(String version) {
		return getVCardAttributeArray("version", "text", version);
	}

	private static JsonArray getFN(String fn) {
		return getVCardAttributeArray("fn", "text", fn);
	}

	private static JsonArray getOrg(String org) {
		return getVCardAttributeArray("org", "text", org);
	}

	private static JsonArray getUrl(String url) {
		return getVCardAttributeArray("url", "uri", url);
	}

	private static JsonArray getEmail(String email) {
		return getVCardAttributeArray("email", "text", email);
	}

	private static JsonArray getVoice(String voiceNumber) {
		return getVCardAttributeArray("tel", Json.createObjectBuilder().add("type", "voice").build(), "text",
				voiceNumber);
	}

	private static JsonArray getCellphone(String cellphone) {
		return getVCardAttributeArray("tel", Json.createObjectBuilder().add("type", "cell").build(), "text", cellphone);
	}

	private static JsonArray getFax(String fax) {
		return getVCardAttributeArray("tel", Json.createObjectBuilder().add("type", "fax").build(), "text", fax);
	}

	private static JsonArray getTitle(String title) {
		return getVCardAttributeArray("title", "text", title);
	}

	private static JsonArray getVCardAttributeArray(String attributeName, String type, String value) {
		return getVCardAttributeArray(attributeName, Json.createObjectBuilder().build(), type, value);
	}

	private static JsonArray getVCardAttributeArray(String attributeName, JsonObject jsonObject, String type,
			String value) {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		builder.add(attributeName);
		builder.add(jsonObject);
		builder.add(type);
		builder.add(value);
		return builder.build();
	}

	private static JsonArray getVCardAddressArray(VCardPostalInfo postalInfo, Map<String, PrivacyStatus> settings,
			boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder attributeArray = Json.createArrayBuilder();
		attributeArray.add("adr");

		String key = "type";
		String value = postalInfo.getType();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			attributeArray.add(Json.createObjectBuilder().add(key, value).build());
		else
			attributeArray.add(Json.createObjectBuilder().build());

		attributeArray.add("text");

		// postal info
		JsonArrayBuilder postalInfoArray = Json.createArrayBuilder();

		postalInfoArray.add("");
		postalInfoArray.add("");

		JsonArrayBuilder streetBuilder = Json.createArrayBuilder();
		key = "street1";
		value = postalInfo.getStreet1();
		int streetCounter = 0;
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner)) {
			streetCounter++;
			streetBuilder.add(value);
		} else {
			streetBuilder.add("");
		}

		key = "street2";
		value = postalInfo.getStreet2();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner)) {
			streetCounter++;
			streetBuilder.add(value);
		} else {
			streetBuilder.add("");
		}

		key = "street3";
		value = postalInfo.getStreet3();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner)) {
			streetCounter++;
			streetBuilder.add(value);
		} else {
			streetBuilder.add("");
		}

		if (streetCounter > 0) {
			postalInfoArray.add(streetBuilder.build());
		} else {
			postalInfoArray.add("");
		}

		key = "city";
		value = postalInfo.getCity();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			postalInfoArray.add(value);
		else
			postalInfoArray.add("");

		key = "state";
		value = postalInfo.getState();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			postalInfoArray.add(value);
		else
			postalInfoArray.add("");

		key = "postalCode";
		value = postalInfo.getPostalCode();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			postalInfoArray.add(value);
		else
			postalInfoArray.add("");

		key = "country";
		value = postalInfo.getCountry();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			postalInfoArray.add(value);
		else
			postalInfoArray.add("");

		attributeArray.add(postalInfoArray.build());

		return attributeArray.build();
	}

	public static String getJsonString(VCard vCard, boolean isAuthenticated, boolean isOwner) {

		Map<String, PrivacyStatus> settings = PrivacyUtil.getVCardPrivacySettings();

		ezvcard.VCard zvcard = new ezvcard.VCard();

		String key = "name";
		String value = vCard.getName();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			zvcard.setFormattedName(value);

		key = "companyName";
		value = vCard.getCompanyName();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			zvcard.setOrganization(value);

		key = "companyUrl";
		value = vCard.getCompanyURL();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			zvcard.addUrl(vCard.getCompanyURL());

		key = "mail";
		value = vCard.getEmail();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			zvcard.addEmail(vCard.getEmail());

		key = "voice";
		value = vCard.getVoice();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			zvcard.addTelephoneNumber(vCard.getVoice(), TelephoneType.VOICE);

		key = "cellphone";
		value = vCard.getCellphone();
		zvcard.addTelephoneNumber(vCard.getCellphone(), TelephoneType.CELL);

		key = "fax";
		value = vCard.getFax();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			zvcard.addTelephoneNumber(vCard.getFax(), TelephoneType.FAX);

		key = "jobTitle";
		value = vCard.getJobTitle();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			zvcard.addTitle(vCard.getJobTitle());

		key = "postalInfo";
		if (PrivacyUtil.isObjectVisible(vCard.getPostalInfo(), key, settings.get(key), isAuthenticated, isOwner)) {
			for (VCardPostalInfo postalInfo : vCard.getPostalInfo()) {
				Address address = new Address();

				key = "type";
				value = postalInfo.getType();
				address.getTypes().add(AddressType.WORK);

				key = "street1";
				value = postalInfo.getStreet1();
				if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
					address.getStreetAddresses().add(value);

				key = "street2";
				value = postalInfo.getStreet2();
				if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
					address.getStreetAddresses().add(value);

				key = "street3";
				value = postalInfo.getStreet3();
				if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
					address.getStreetAddresses().add(value);

				key = "city";
				value = postalInfo.getCity();
				if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
					address.setLocality(postalInfo.getCity());

				key = "state";
				value = postalInfo.getState();
				if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
					address.setRegion(postalInfo.getState());

				key = "postalCode";
				value = postalInfo.getPostalCode();
				if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
					address.setPostalCode(postalInfo.getPostalCode());

				key = "country";
				value = postalInfo.getCountry();
				if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
					address.setCountry(postalInfo.getCountry());

				zvcard.addAddress(address);
			}
		}
		return zvcard.writeJson();
	}

}
