package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Statement;

import mx.nic.rdap.core.db.DsData;
import mx.nic.rdap.server.db.DsDataDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Model for the DsData Object
 * 
 * @author evaldes
 * @author dhfelix
 *
 */
public class DsDataModel {

	private final static Logger logger = Logger.getLogger(DsDataModel.class.getName());

	private final static String QUERY_GROUP = "DsData";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group.");
		}
	}

	/**
	 * Stores a DsData Object to the database
	 * 
	 * @param dsData
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static long storeToDatabase(DsData dsData, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		String query = queryGroup.getQuery("storeToDatabase");
		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			((DsDataDAO) dsData).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			Long dsDataId = resultSet.getLong(1);

			dsData.setId(dsDataId);
		}

		EventModel.storeDsDataEventsToDatabase(dsData.getEvents(), dsData.getId(), connection);
		LinkModel.storeDsDataLinksToDatabase(dsData.getLinks(), dsData.getId(), connection);

		return dsData.getId();
	}

	public static void storeAllToDatabase(List<DsData> dsDataList, Long secureDnsId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		if (dsDataList.isEmpty()) {
			return;
		}

		for (DsData dsData : dsDataList) {
			dsData.setSecureDNSId(secureDnsId);
			storeToDatabase(dsData, connection);
		}

	}

	/**
	 * Finds a SercureDns´s DsData
	 * 
	 * @param secureDnsId
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<DsData> getBySecureDnsId(Long secureDnsId, Connection connection)
			throws SQLException, IOException {
		String query = queryGroup.getQuery("getBySecureDns");
		List<DsData> resultList = null;

		try (PreparedStatement statement = connection.prepareStatement(query);) { // QUERY
			statement.setLong(1, secureDnsId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				return Collections.emptyList();
			}

			resultList = new ArrayList<>();

			do {
				DsDataDAO dsData = new DsDataDAO(resultSet);
				dsData.setEvents(EventModel.getByDsDataId(dsData.getId(), connection));
				dsData.setLinks(LinkModel.getByDsDataId(dsData.getId(), connection));
				resultList.add(dsData);
			} while (resultSet.next());
		}

		return resultList;
	}
}
