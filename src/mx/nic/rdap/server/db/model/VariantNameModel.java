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

import mx.nic.rdap.core.db.VariantName;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.VariantNameDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the VariantName Object
 * 
 * @author evaldes
 *
 */
public class VariantNameModel {

	private final static Logger logger = Logger.getLogger(VariantNameModel.class.getName());

	private final static String QUERY_GROUP = "VariantName";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			SecureDNSModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Stores all VariantNames in a List
	 * 
	 * @param variantNames
	 * @param variantInsertedId
	 * @param connection
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void storeAllToDatabase(List<VariantName> variantNames, Long variantInsertedId, Connection connection)
			throws IOException, SQLException {
		for (VariantName variantName : variantNames) {
			variantName.setVariantId(variantInsertedId);
			VariantNameModel.storeToDatabase(variantName, connection);
		}
	}

	/**
	 * Store a VariantName into the database
	 * 
	 * @param variantName
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void storeToDatabase(VariantName variantName, Connection connection)
			throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"))) {
			((VariantNameDAO) variantName).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();
		}
	}

	/**
	 * Get all VariantNames from a Variant
	 * 
	 * @param id
	 * @return
	 * @throws SQLExceptiona
	 * @throws IOException
	 */
	public static List<VariantName> getByVariantId(Long variantId, Connection connection)
			throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByVariantId"))) {
			statement.setLong(1, variantId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}
			List<VariantName> variantNames = new ArrayList<VariantName>();
			do {
				VariantNameDAO variantName = new VariantNameDAO(resultSet);
				variantNames.add(variantName);
			} while (resultSet.next());
			return variantNames;
		}
	}

}
