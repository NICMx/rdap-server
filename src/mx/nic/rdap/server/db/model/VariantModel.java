package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
	public static void storeToDatabase(Variant variant, Connection connection) throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS)) {
			((VariantDAO) variant).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();// TODO Validate if it was correct
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			Long variantInsertedId = resultSet.getLong(1);
			List<VariantName> variantNames = variant.getVariantNames();
			VariantNameModel.storeAllToDatabase(variantNames, variantInsertedId, connection);
			List<VariantRelation> relations = variant.getRelations();
			VariantRelationModel.storeVariantRelations(relations, variantInsertedId, connection);
			variant.setId(variantInsertedId);
		}
	}

	/**
	 * Get all Variants from a domain
	 * 
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<Variant> getByDomainId(Long domainId, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDomainId"))) {
			statement.setLong(1, domainId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}
			List<Variant> variants = new ArrayList<Variant>();
			do {
				VariantDAO variant = new VariantDAO(resultSet);
				System.out.println("" + variant.getId() + variant.getIdnTable() + variant.getDomainId());
				variant.setVariantNames(VariantNameModel.getByVariantId(variant.getId(), connection));
				variant.setRelations(VariantRelationModel.getByVariantId(variant.getId(), connection));
				variants.add(variant);
			} while (resultSet.next());
			return variants;
		}
	}

}
