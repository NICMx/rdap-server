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

import mx.nic.rdap.core.db.Registrar;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.VCardDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * Model for the {@link VCard}
 * 
 * @author dhfelix
 *
 */
public class VCardModel {

	private static final Logger logger = Logger.getLogger(VCardModel.class.getName());

	private static final String QUERY_GROUP = "VCard";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error while loading query group on " + VCardModel.class.getName(), e);
		}
	}

	/**
	 * Store a VCard
	 * 
	 * @param vCard
	 *            The vCard to be stored.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static long storeToDatabase(VCard vCard, Connection connection) throws SQLException {
		long vCardId;

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS);) {
			((VCardDAO) vCard).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			vCardId = resultSet.getLong(1);
			vCard.setId(vCardId);
		}

		for (VCardPostalInfo postalInfo : vCard.getPostalInfo()) {
			postalInfo.setVCardId(vCardId);
			VCardPostalInfoModel.storeToDatabase(postalInfo, connection);
		}

		return vCardId;
	}

	public static void storeRegistrarContactToDatabase(List<VCard> vCardList, Long registrarId, Connection connection)
			throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeRegistrarContact"),
				Statement.RETURN_GENERATED_KEYS);) {
			for (VCard vCard : vCardList) {
				statement.setLong(1, registrarId);
				statement.setLong(2, vCard.getId());
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();
			}
		}
	}

	/**
	 * Get a {@link VCard} by its Id.
	 * 
	 * @param vCardId
	 *            Id of the VCard to look.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static VCard getById(Long vCardId, Connection connection) throws SQLException {
		VCard vCardResult = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getById"));) {
			statement.setLong(1, vCardId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			vCardResult = processResultSet(resultSet, connection);
		}

		setSonObjects(vCardResult, connection);

		return vCardResult;
	}

	/**
	 * Get a {@link List} of {@link VCard} belonging to a {@link Registrar} by
	 * the registrar Id.
	 * 
	 * @param registrarId
	 *            Id of the Registrar to look for its {@link VCard}s
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static List<VCard> getByRegistrarId(Long registrarId, Connection connection) throws SQLException {
		List<VCard> vCardResults = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByRegistrarId"));) {
			statement.setLong(1, registrarId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();

			vCardResults = processListResultSet(resultSet, connection);
		}

		for (VCard vCard : vCardResults) {
			setSonObjects(vCard, connection);
		}

		return vCardResults;
	}

	/**
	 * Get and Set the nested objects of the {@link VCard}.
	 * 
	 * @param vCard
	 * @param connection
	 * @throws SQLException
	 */
	private static void setSonObjects(VCard vCard, Connection connection) throws SQLException {
		try {
			List<VCardPostalInfo> postalInfoList = VCardPostalInfoModel.getByVCardId(vCard.getId(), connection);
			vCard.setPostalInfo(postalInfoList);
		} catch (ObjectNotFoundException e) {
			// TODO: a VCard couldn't hava postal info ?
		}
	}

	/**
	 * Proccess a resultSet and return one {@link VCard}.
	 * 
	 * @param resultSet
	 *            {@link ResultSet} to proccess.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private static VCard processResultSet(ResultSet resultSet, Connection connection) throws SQLException {
		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Entity not found");
		}
		VCardDAO vCard = new VCardDAO();
		vCard.loadFromDatabase(resultSet);

		return vCard;
	}

	/**
	 * Process a {@link ResultSet} and return a {@link List} of {@link VCard}s.
	 * 
	 * @param resultSet
	 *            {@link ResultSet} to proccess.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private static List<VCard> processListResultSet(ResultSet resultSet, Connection connection) throws SQLException {
		List<VCard> result = new ArrayList<>();
		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Entity not found");
		}
		do {
			VCardDAO vCard = new VCardDAO();
			vCard.loadFromDatabase(resultSet);
			result.add(vCard);
		} while (resultSet.next());

		return result;
	}

}
