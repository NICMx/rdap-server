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

import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.RemarkDescriptionDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the RemarkDescription object
 * 
 * @author dalpuche
 *
 */
public class RemarkDescriptionModel {

	private final static Logger logger = Logger.getLogger(RemarkDescriptionModel.class.getName());

	private final static String QUERY_GROUP = "RemarkDescription";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			RemarkDescriptionModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Store a list of RemarkDescriptions
	 * 
	 * @param descriptions
	 * @param remarkInsertedId
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void storeAllToDatabase(List<RemarkDescription> descriptions, Long remarkInsertedId,
			Connection connection) throws IOException, SQLException {
		for (RemarkDescription remarkDescription : descriptions) {
			remarkDescription.setRemarkId(remarkInsertedId);
			RemarkDescriptionModel.storeToDatabase(remarkDescription, connection);
		}
	}

	/**
	 * Store a RemarkDescription in the database
	 * 
	 * @param remark
	 * @return true if the insert was correct
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void storeToDatabase(RemarkDescription remarkDescription, Connection connection)
			throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"))) {
			((RemarkDescriptionDAO) remarkDescription).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();// TODO Validate if the
										// insert was correct
		}
	}

	/**
	 * Get all RemarkDescriptions from a Remark
	 * 
	 * @param id
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<RemarkDescription> findByRemarkId(Long id, Connection connection)
			throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByRemarkId"))) {
			statement.setLong(1, id);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO:
																			// Managae
																			// the
																			// exception
				}
				List<RemarkDescription> remarks = new ArrayList<RemarkDescription>();
				do {
					RemarkDescriptionDAO remarkDescription = new RemarkDescriptionDAO(resultSet);
					remarks.add(remarkDescription);
				} while (resultSet.next());
				return remarks;
			}
		}
	}

}
