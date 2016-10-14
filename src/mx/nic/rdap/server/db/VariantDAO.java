package mx.nic.rdap.server.db;

import java.net.IDN;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.server.renderer.json.JsonParser;
import mx.nix.rdap.core.catalog.VariantRelation;

/**
 * Data access class for the Variant object.
 * 
 * @author evaldes
 *
 */
public class VariantDAO extends Variant implements DatabaseObject, JsonParser {

	/**
	 * Default constructor
	 */
	public VariantDAO() {
		super();
	}

	/**
	 * Constructor with result set
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	public VariantDAO(ResultSet resultSet) throws SQLException {
		super();
		loadFromDatabase(resultSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.server.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getLong("var_id"));
		this.setIdnTable(resultSet.getString("var_idn_table"));
		this.setDomainId(resultSet.getLong("dom_id"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setString(1, this.getIdnTable());
		preparedStatement.setLong(2, this.getDomainId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.json.JsonParser#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		if (this.getRelations() != null && !this.getRelations().isEmpty()) {
			builder.add("relation", this.getRelationsJson(this.getRelations()));
		}
		if (this.getIdnTable() != null && this.getIdnTable() != "") {
			builder.add("idnTable", this.getIdnTable());
		}
		if (this.getVariantNames() != null && !this.getVariantNames().isEmpty()) {
			builder.add("variantNames", this.getVariantNamesJson(this.getVariantNames()));
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
