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

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.NameserverDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Model for the Nameserver Object
 * 
 * @author dalpuche
 *
 */
public class NameserverModel {

	private final static Logger logger = Logger.getLogger(NameserverModel.class.getName());

	private final static String QUERY_GROUP = "Nameserver";

	protected static QueryGroup queryGroup = null;

	/**
	 * Validate the required attributes for the nameserver
	 * 
	 * @param nameserver
	 * @throws RequiredValueNotFoundException
	 */
	private static void isValidForStore(Nameserver nameserver) throws RequiredValueNotFoundException {
		if (nameserver.getPunycodeName() == null || nameserver.getPunycodeName().isEmpty())
			throw new RequiredValueNotFoundException("ldhName", "Nameserver");
		if (nameserver.getRarId() == null) {// Never has to result true
			throw new RequiredValueNotFoundException("rarId", "Nameserver");

		}
	}

	/**
	 * Store a namerserver in the database
	 * 
	 * @param nameserver
	 * @throws IOException
	 * @throws SQLException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeToDatabase(Nameserver nameserver)
			throws IOException, SQLException, RequiredValueNotFoundException {
		isValidForStore(nameserver);
		NameserverModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
						Statement.RETURN_GENERATED_KEYS)) {
			((NameserverDAO) nameserver).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();// TODO Validate if the
										// insert was correct
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long nameserverId = result.getLong(1);// The id of the nameserver
													// inserted
			IpAddressModel.storeToDatabase(nameserver.getIpAddresses(), nameserverId, connection);
			StatusModel.storeNameserverStatusToDatabase(nameserver.getStatus(), nameserverId, connection);
			RemarkModel.storeNameserverRemarksToDatabase(nameserver.getRemarks(), nameserverId, connection);
			LinkModel.storeNameserverLinksToDatabase(nameserver.getLinks(), nameserverId, connection);
			EventModel.storeNameserverEventsToDatabase(nameserver.getEvents(), nameserverId, connection);
		}

	}

	public static void storeDomainNameserversToDatabase(List<Nameserver> nameservers, Long domainId,
			Connection connection) throws SQLException, IOException, RequiredValueNotFoundException {
		try (PreparedStatement statement = connection
				.prepareStatement(queryGroup.getQuery("storeNameserverLinksToDatabase"))) {
			for (Nameserver nameserver : nameservers) {
				NameserverModel.storeToDatabase(nameserver);// TODO
				// Check
				// connection,
				// connection);
				statement.setLong(1, domainId);
				statement.setLong(2, nameserver.getId());
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
				// insert was correct
			}
		}
	}

	/**
	 * Find a nameserver object by it's name
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Nameserver findByName(String name) throws IOException, SQLException {
		NameserverModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("findByName"))) {
			statement.setString(1, name);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO:
																			// Managae
																			// the
																			// exception
				}
				Nameserver nameserver = new NameserverDAO(resultSet);
				NameserverModel.loadNestedObjects(nameserver, connection);
				return nameserver;
			}
		}
	}

	public static List<Nameserver> getByDomainId(Long domainId, Connection connection)
			throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDomainId"))) {
			statement.setLong(1, domainId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					// TODO a domain can not have nameservers?
					// throw new ObjectNotFoundException("Object not found.");
					return Collections.emptyList();

				}
				List<Nameserver> nameservers = new ArrayList<Nameserver>();
				do {
					Nameserver nameserver = new NameserverDAO(resultSet);
					NameserverModel.loadNestedObjects(nameserver, connection);
					nameservers.add(nameserver);
				} while (resultSet.next());
				return nameservers;
			}
		}
	}

	/**
	 * verify if a nameserver exist by it's name
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean existNameserverByName(String name) throws IOException, SQLException {
		NameserverModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("existByName"))) {
			statement.setString(1, name);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return false;
				}
				return true;
			}
		}
	}

	/**
	 * Load the nested object of the nameserver
	 * 
	 * @param nameserver
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void loadNestedObjects(Nameserver nameserver, Connection connection)
			throws IOException, SQLException {

		// Retrieve the ipAddress
		nameserver.setIpAddresses(IpAddressModel.getIpAddressStructByNameserverId(nameserver.getId(), connection));
		// Retrieve the entities
		nameserver.setEntities(null);
		// Retrieve the status
		nameserver.setStatus(StatusModel.getByNameServerId(nameserver.getId(), connection));
		// Retrieve the remarks
		nameserver.setRemarks(RemarkModel.getByNameserverId(nameserver.getId(), connection));
		// Retrieve the links
		nameserver.setLinks(LinkModel.getByNameServerId(nameserver.getId(), connection));
		// Retrieve the events
		nameserver.setEvents(EventModel.getByNameServerId(nameserver.getId(), connection));

	}
}
