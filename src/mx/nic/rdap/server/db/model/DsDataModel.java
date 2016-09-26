package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 *
 */
public class DsDataModel {

	private final static Logger logger = Logger.getLogger(DsDataModel.class.getName());

	private final static String QUERY_GROUP = "DsData";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			DsDataModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group.");
		}
	}

	public static long storeToDatabase(DsData dsData, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"), // TODO
																												// QUERY
				Statement.RETURN_GENERATED_KEYS)) {
			((DsDataDAO) dsData).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();// TODO Validate if it was correct
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			Long dsDataId = resultSet.getLong(1);
			EventModel.storeDsDataEventsToDatabase(dsData.getEvents(), dsDataId, connection);
			LinkModel.storeDsDataLinksToDatabase(dsData.getLinks(), dsDataId, connection);
			return dsDataId;
		}
	}

	public static DsData getBySecureDnsId(Long secureDnsId, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getBySecureDns"));) {// TODO
																													// QUERY
			statement.setLong(1, secureDnsId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet resultSet = statement.executeQuery();// TODO Validate if
															// it was correct
			DsDataDAO dsData = new DsDataDAO(resultSet);
			dsData.setEvents(EventModel.getByDsDataId(dsData.getId(), connection));
			dsData.setLinks(LinkModel.getByDsDataId(dsData.getId(), connection));
			return dsData;
		}
	}
}
