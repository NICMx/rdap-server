package mx.nic.rdap.server.renderer.json;

import java.net.IDN;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.db.VariantDAO;
import mx.nix.rdap.core.catalog.VariantRelation;

/**
 * Data access class for the Variant object.
 * 
 * @author evaldes
 *
 */
public class VariantParser implements JsonParser {
  
	private VariantDAO variant;
	/**
	 * Default constructor
	 */
	public VariantParser(VariantDAO variant) {
 this.variant=variant;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.json.JsonParser#toJson()
	 */
	@Override
	public JsonObject getJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		if (variant.getRelations() != null && !variant.getRelations().isEmpty()) {
			builder.add("relation", this.getRelationsJson(variant.getRelations()));
		}
		if (variant.getIdnTable() != null && variant.getIdnTable() != "") {
			builder.add("idnTable", variant.getIdnTable());
		}
		if (variant.getVariantNames() != null && !variant.getVariantNames().isEmpty()) {
			builder.add("variantNames", this.getVariantNamesJson(variant.getVariantNames()));
		}
		return builder.build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.json.JsonParser#toJson()
	 */
	private JsonValue getVariantNamesJson(List<VariantName> variantNames) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

		for (VariantName variantName : variantNames) {
			JsonObjectBuilder builder = Json.createObjectBuilder();
			builder.add("ldhName", variantName.getLdhName());
			builder.add("unicodeName", IDN.toUnicode(variantName.getLdhName()));
			arrayBuilder.add(builder);
		}
		return arrayBuilder.build();
	}

	private JsonValue getRelationsJson(List<VariantRelation> relations) {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		for (VariantRelation relation : relations) {
			arrayBuilder.add(relation.getValue());
		}
		return arrayBuilder.build();
	}

}
