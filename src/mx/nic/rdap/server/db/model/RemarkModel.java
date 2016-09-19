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

import com.mysql.jdbc.Statement;

import mx.nic.rdap.core.db.Remark;
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

	private final static Logger logger = Logger.getLogger(RemarkModel.class.getName());

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
	public static long storeToDatabase(Remark remark, Connection connection) throws IOException, SQLException {
		RemarkModel.queryGroup = new QueryGroup(QUERY_GROUP);

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS)) {// The Remark's id is
													// autoincremental,
													// Statement.RETURN_GENERATED_KEYS
													// give us the id
													// generated for the
													// object stored
			((RemarkDAO) remark).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long remarkInsertedId = result.getLong(1);// The id of the remark
														// inserted
			RemarkDescriptionModel.storeAllToDatabase(remark.getDescriptions(), remarkInsertedId, connection);
			return remarkInsertedId;
		}
	}

	/**
	 * Store the nameserver remarks
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void storeNameserverRemarksToDatabase(List<Remark> remarks, Long nameserverId, Connection connection)
			throws SQLException, IOException {
		LinkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeNameserverRemarksToDatabase"))) {
			for (Remark remark : remarks) {
				Long remarkId = RemarkModel.storeToDatabase(remark, connection);
				statement.setLong(1, nameserverId);
				statement.setLong(2, remarkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
				// insert was correct
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
	public static List<Remark> getByNameserverId(Long nameserverId, Connection connection)
			throws IOException, SQLException {
		RemarkModel.queryGroup = new QueryGroup(QUERY_GROUP);

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByNameserverId"))) {
			statement.setLong(1, nameserverId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery();) {
				return processResultSet(resultSet, connection);
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
	public static List<Remark> getAll(Connection connection) throws IOException, SQLException {
		RemarkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getAll"));
				ResultSet resultSet = statement.executeQuery();) {
			return processResultSet(resultSet, connection);
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
	private static List<Remark> processResultSet(ResultSet resultSet, Connection connection)
			throws SQLException, ObjectNotFoundException {
		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Object not found.");
		}
		List<Remark> remarks = new ArrayList<Remark>();
		do {
			RemarkDAO remark = new RemarkDAO(resultSet, connection);
			remarks.add(remark);
		} while (resultSet.next());
		return remarks;
	}
}
