/**
 * 
 */
package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nix.rdap.core.catalog.VariantRelation;

/**
 * Model for the Relation Object
 * 
 * @author evaldes
 *
 */
public class VariantRelationModel {

	private final static Logger logger = Logger.getLogger(VariantRelationModel.class.getName());

	private final static String QUERY_GROUP = "VariantRelation";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			SecureDNSModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Gets all variant's relations from a variant
	 * 
	 * @param variantId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<VariantRelation> getByVariantId(Long variantId, Connection connection)
			throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByVariantId"))) {
			statement.setLong(1, variantId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {// Validate
																	// results
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");
				}
				List<VariantRelation> relations = new ArrayList<VariantRelation>();
				do {
					relations.add(VariantRelation.getById(resultSet.getInt("rel_id")));
				} while (resultSet.next());
				return relations;
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
	public static void storeVariantRelations(List<VariantRelation> relations, Long variantId, Connection connection)
			throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeVariantRelation"))) {
			for (VariantRelation relation : relations) {
				statement.setLong(1, variantId);
				statement.setInt(2, relation.getId());
				logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			}
		}
	}
}
