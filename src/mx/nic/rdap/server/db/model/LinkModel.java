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

import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.server.db.DatabaseSession;
import mx.nic.rdap.server.db.LinkDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;

/**
 * The model for the Link object
 * 
 * @author dalpuche
 *
 */
public class LinkModel {

	private final static Logger logger = Logger.getLogger(LinkModel.class.getName());

	private final static String QUERY_GROUP = "Link";

	protected static QueryGroup queryGroup = null;

	/**
	 * Store a Link in the Database
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Long storeToDatabase(Link link) throws SQLException, IOException {
		LinkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
						Statement.RETURN_GENERATED_KEYS)) {
			((LinkDAO) link).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();// TODO Validate if the
										// insert was correct
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			Long linkId = result.getLong(1);// The id of the link inserted
			return linkId;
		}
	}

	/**
	 * Store the nameserver links
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void storeNameserverLinksToDatabase(List<Link> links, Long nameserverId)
			throws SQLException, IOException {
		LinkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(queryGroup.getQuery("storeNameserverLinkToDatabase"))) {
			for (Link link : links) {
				Long linkId = LinkModel.storeToDatabase(link);
				statement.setLong(1, nameserverId);
				statement.setLong(2, linkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
				// insert was correct
			}
		}
	}

	/**
	 * Store the event links
	 * 
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void storeEventLinksToDatabase(List<Link> links, Long eventId) throws SQLException, IOException {
		LinkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection
						.prepareStatement(queryGroup.getQuery("storeEventLinksToDatabase"))) {
			for (Link link : links) {
				Long linkId = LinkModel.storeToDatabase(link);
				statement.setLong(1, eventId);
				statement.setLong(2, linkId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();// TODO Validate if the
				// insert was correct
			}
		}
	}

	/**
	 * Get all links for a Nameserver
	 * 
	 * @param nameserverId
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<Link> getByNameServerId(Long nameserverId) throws IOException, SQLException {
		LinkModel.queryGroup = new QueryGroup(QUERY_GROUP);
		try (Connection connection = DatabaseSession.getConnection();
				PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByNameServerId"))) {
			statement.setLong(1, nameserverId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO:
																			// Managae
																			// the
																			// exception
				}
				List<Link> links = new ArrayList<Link>();
				do {
					LinkDAO link = new LinkDAO(resultSet);
					links.add(link);
				} while (resultSet.next());
				return links;
			}
		}
	}
}
