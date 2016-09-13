package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.server.db.DatabaseSession;
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
	private final static String QUERY_GROUP = "RemarkDescription";

	protected static QueryGroup queryGroup = null;

	/**
	 * UNUSED:Store a RemarkDescription in the database
	 * 
	 * @param remark
	 * @return true if the insert was correct
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean storeToDatabase(RemarkDescription remarkDescription) throws IOException, SQLException {
		RemarkDescriptionModel.queryGroup = new QueryGroup(QUERY_GROUP);
		Connection connection = DatabaseSession.getConnection();
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"))) {
			((RemarkDescriptionDAO)remarkDescription).storeToDatabase(statement);
			return (statement.executeUpdate() == 1);// TODO Validate if the
													// insert was correct
			// connection.commit();//TODO: autocommit=?
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
	public static List<RemarkDescription> findByRemarkId(Long id) throws IOException, SQLException {
		RemarkDescriptionModel.queryGroup = new QueryGroup(QUERY_GROUP);
		Connection connection = DatabaseSession.getConnection();
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByRemarkId"))) {
			statement.setLong(1, id);
			ResultSet resultSet = statement.executeQuery();
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
