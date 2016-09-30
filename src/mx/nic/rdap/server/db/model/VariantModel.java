package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.VariantDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nix.rdap.core.catalog.VariantRelation;

/**
 * Model for the Variant Object
 * 
 * @author evaldes
 * @author dhfelix
 *
 */
public class VariantModel {

	private final static Logger logger = Logger.getLogger(VariantModel.class.getName());

	private final static String QUERY_GROUP = "Variant";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Store all variants from a domain into the database
	 * 
	 * @param variants
	 * @param domainId
	 * @param connection
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void storeAllToDatabase(List<Variant> variants, Long domainId, Connection connection)
			throws IOException, SQLException {
		for (Variant variant : variants) {
			variant.setDomainId(domainId);
			VariantModel.storeToDatabase(variant, connection);
		}
	}

	/**
	 * Store a variant into the database
	 * 
	 * @param variant
	 * @return true if the insert was successful
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Long storeToDatabase(Variant variant, Connection connection) throws IOException, SQLException {
		Long variantInsertedId = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS)) {
			((VariantDAO) variant).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();// TODO Validate if it was correct
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			variantInsertedId = resultSet.getLong(1);
			variant.setId(variantInsertedId);
		}

		storeVariantNames(variant, connection);
		storeVariantRelations(variant, connection);

		return variantInsertedId;
	}

	/**
	 * Get all Variants from a domain
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<Variant> getByDomainId(Long domainId, Connection connection) throws SQLException, IOException {
		List<Variant> variants = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDomainId"))) {
			statement.setLong(1, domainId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				// A domain can have no variants.
				return Collections.emptyList();
			}

			variants = new ArrayList<>();
			do {
				VariantDAO variant = new VariantDAO(resultSet);
				variants.add(variant);
			} while (resultSet.next());
		}

		for (Variant variant : variants) {
			setVariantNames(variant, connection);
			setVariantRelations(variant, connection);
		}

		return variants;
	}

	public static Variant getById(Long variantId, Connection connection) throws SQLException, IOException {
		Variant result = null;
		String query = queryGroup.getQuery("getById");
		try (PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setLong(1, variantId);
			logger.log(Level.INFO, "Executing QUERY" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object Not found");
			}

			result = new VariantDAO(resultSet);
		}

		setVariantNames(result, connection);
		setVariantRelations(result, connection);

		return result;
	}

	/**
	 * Gets and set all variant relations from a variant
	 * 
	 * @param variantId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	private static void setVariantRelations(Variant variant, Connection connection) throws IOException, SQLException {
		Long variantId = variant.getId();
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("getVariantRelationsByVariantId"))) {
			statement.setLong(1, variantId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				// Validate results
				if (!resultSet.next()) {
					// TODO is there any reason not to have variantRelations?
					throw new ObjectNotFoundException("Object not found.");
				}
				List<VariantRelation> relations = variant.getRelations();
				do {
					relations.add(VariantRelation.getById(resultSet.getInt("rel_id")));
				} while (resultSet.next());
				return;
			}
		}
	}

	/**
	 * Inserts variant's relation to database
	 * 
	 * @param relations
	 * @param variantId
	 * @param connection
	 * @throws SQLException
	 */
	private static void storeVariantRelations(Variant variant, Connection connection) throws SQLException {
		if (variant.getRelations().isEmpty())
			return;

		Long variantId = variant.getId();
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeVariantRelation"))) {
			for (VariantRelation relation : variant.getRelations()) {
				statement.setInt(1, relation.getId());
				statement.setLong(2, variantId);
				logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
				statement.executeUpdate();
			}
		}
	}

	/**
	 * Store a Variant's variantNames into the database
	 * 
	 * @param variantName
	 * @throws IOException
	 * @throws SQLException
	 */
	private static void storeVariantNames(Variant variant, Connection connection) throws IOException, SQLException {
		if (variant.getVariantNames().isEmpty())
			return;

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeVariantNames"))) {
			Long variantId = variant.getId();
			for (VariantName variantName : variant.getVariantNames()) {
				statement.setString(1, variantName.getPunycode());
				statement.setLong(2, variantId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();
			}
		}
	}

	/**
	 * Get and set all VariantNames from a Variant
	 * 
	 * @param variant
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void setVariantNames(Variant variant, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("getVariantNamesByVariantId"))) {
			statement.setLong(1, variant.getId());
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				// TODO is there any reason not to have variantsName?
				throw new ObjectNotFoundException("Object not found.");
			}
			List<VariantName> variantNames = variant.getVariantNames();
			do {
				VariantName variantName = new VariantName();
				variantName.setLdhName(resultSet.getString("vna_ldh_name"));
				variantNames.add(variantName);
			} while (resultSet.next());
		}
	}

}
