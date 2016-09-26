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

import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.VCardPostalInfoDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the {@link VCardPostalInfo}.
 * 
 * @author dhfelix
 *
 */
public class VCardPostalInfoModel {
	private static final Logger logger = Logger.getLogger(VCardPostalInfoModel.class.getName());

	private static final String QUERY_GROUP = "VCardPostalInfo";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error while loading query group on " + VCardPostalInfoModel.class.getName(), e);
		}
	}

	/**
	 * Store a VCardPostalInfo
	 * 
	 * @param vCardPostalInfo
	 *            The vCardPostalInfo to be stored.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static long storeToDatabase(VCardPostalInfo vCardPostalInfo, Connection connection) throws SQLException {
		long insertedId;

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS);) {
			((VCardPostalInfoDAO) vCardPostalInfo).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			insertedId = resultSet.getLong(1);
			vCardPostalInfo.setId(insertedId);
		}

		return insertedId;
	}

	/**
	 * Get a {@link VCardPostalInfo} by its id.
	 * 
	 * @param vCardPostalInfoId
	 *            Id of the {@link VCardPostalInfo} to look.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static VCardPostalInfo getById(Long vCardPostalInfoId, Connection connection) throws SQLException {
		VCardPostalInfo vCardPostalInfoResult = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getById"));) {
			statement.setLong(1, vCardPostalInfoId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			vCardPostalInfoResult = processResultSet(resultSet, connection);
		}
		return vCardPostalInfoResult;
	}

	/**
	 * Gets a {@link List} of {@link VCardPostalInfo} by a VCardId
	 * 
	 * @param vCardId
	 *            Id of the {@link VCard} to get the {@link VCardPostalInfo}.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static List<VCardPostalInfo> getByVCardId(Long vCardId, Connection connection) throws SQLException {
		List<VCardPostalInfo> vCardPostalInfoResult = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByVCardId"));) {
			statement.setLong(1, vCardId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			vCardPostalInfoResult = processListResultSet(resultSet, connection);
		}
		return vCardPostalInfoResult;
	}

	/**
	 * Process a {@link ResultSet} and return one {@link VCardPostalInfo}.
	 * 
	 * @param resultSet
	 *            {@link ResultSet} to proccess.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private static VCardPostalInfo processResultSet(ResultSet resultSet, Connection connection) throws SQLException {
		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Entity not found");
		}
		VCardPostalInfoDAO vCardPostalInfo = new VCardPostalInfoDAO();
		vCardPostalInfo.loadFromDatabase(resultSet);

		return vCardPostalInfo;
	}

	/**
	 * Process a {@link ResultSet} and return a {@link List} of
	 * {@link VCardPostalInfo}.
	 * 
	 * @param resultSet
	 *            {@link ResultSet} to proccess.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private static List<VCardPostalInfo> processListResultSet(ResultSet resultSet, Connection connection)
			throws SQLException {
		List<VCardPostalInfo> result = new ArrayList<>();

		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Entity not found");
		}
		do {
			VCardPostalInfoDAO vCardPostalInfo = new VCardPostalInfoDAO();
			vCardPostalInfo.loadFromDatabase(resultSet);
			result.add(vCardPostalInfo);
		} while (resultSet.next());

		return result;
	}

}
