package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Statement;

import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.RemarkDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the Remark Object
 * 
 * @author dalpuche
 *
 */
public class RemarkModel {
	private final static String QUERY_GROUP = "Remark";

	protected static QueryGroup queryGroup = null;

	/**
	 * Store a Remark in the database
	 * 
	 * @param remark
	 * @return true if the insert was correct
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void storeToDatabase(Remark remark) throws IOException, SQLException {
		RemarkModel.queryGroup = new QueryGroup(QUERY_GROUP);

		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
						Statement.RETURN_GENERATED_KEYS)) {// The Remark's id is
															// autoincremental,
															// Statement.RETURN_GENERATED_KEYS
															// give us the id
															// generated for the
															// object stored
			((RemarkDAO)remark).storeToDatabase(statement);
			statement.executeUpdate();
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long remardInsertedId = result.getLong(1);
			for (RemarkDescription remarkDescription : remark.getDescriptions()) {
				remarkDescription.setRemarkId(remardInsertedId);
				RemarkDescriptionModel.storeToDatabase(remarkDescription);
			}
		}
	}

	/**
	 * Get all remarks for the namemeserver
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Remark> getByNameserverId(Long nameserverId) throws IOException, SQLException {
		RemarkModel.queryGroup = new QueryGroup(QUERY_GROUP);

		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByNameserverId"))) {
			statement.setLong(1, nameserverId);
			try (ResultSet resultSet = statement.executeQuery();) {
				return processResultSet(resultSet);
			}
		}
	}

	/**
	 * Unused. Get all Remarks from DB
	 * 
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Remark> getAll() throws IOException, SQLException {
		RemarkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getAll"));
				ResultSet resultSet = statement.executeQuery();) {
			return processResultSet(resultSet);
		}
	}

	/**
	 * Process the resulset of the query
	 * 
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 * @throws ObjectNotFoundException
	 */
	private static List<Remark> processResultSet(ResultSet resultSet) throws SQLException, ObjectNotFoundException {
		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Object not found.");
		}
		List<Remark> remarks = new ArrayList<Remark>();
		do {
			RemarkDAO remark = new RemarkDAO(resultSet);
			remarks.add(remark);
		} while (resultSet.next());
		return remarks;
	}
}
