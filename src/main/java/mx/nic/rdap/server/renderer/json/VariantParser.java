package mx.nic.rdap.server.renderer.json;

import java.net.IDN;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.catalog.VariantRelation;
import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.server.PrivacyStatus;
import mx.nic.rdap.server.PrivacyUtil;

public class VariantParser {
	public static JsonArray getJsonArray(List<Variant> variants, boolean isAuthenticated, boolean isOwner) {
		JsonArrayBuilder builder = Json.createArrayBuilder();

		for (Variant variant : variants) {
			getJsonObject(variant, isAuthenticated, isOwner);
		}

		return builder.build();
	}

	public static JsonObject getJsonObject(Variant variant, boolean isAuthenticated, boolean isOwner) {
		Map<String, PrivacyStatus> settings = PrivacyUtil.getDomainVariantsPrivacySettings();
		JsonObjectBuilder builder = Json.createObjectBuilder();

		String key = "relation";
		if (PrivacyUtil.isObjectVisible(variant.getRelations(), key, settings.get(key), isAuthenticated, isOwner)) {
			builder.add(key, getDomainRelationsJsonArray(variant.getRelations()));
		}

		key = "idnTable";
		String value = variant.getIdnTable();
		if (PrivacyUtil.isObjectVisible(value, key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, value);

		key = "variantNames";
		if (PrivacyUtil.isObjectVisible(variant.getVariantNames(), key, settings.get(key), isAuthenticated, isOwner))
			builder.add(key, getVariantNamesJsonArray(variant.getVariantNames()));

		return builder.build();
	}

	private static JsonArray getDomainRelationsJsonArray(List<VariantRelation> relations) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (VariantRelation relation : relations) {
			arrayBuilder.add(relation.getValue());
		}
		return arrayBuilder.build();
	}

	private static JsonArray getVariantNamesJsonArray(List<VariantName> variantNames) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

		for (VariantName variantName : variantNames) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("ldhName", variantName.getLdhName());
			builder.add("unicodeName", IDN.toUnicode(variantName.getLdhName()));
			arrayBuilder.add(builder);
		}

		return arrayBuilder.build();
	}

}
