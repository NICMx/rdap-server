package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Statement;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Registrar;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.RegistrarDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.Status;

/**
 * Model for the {@link RegistrarDAO} object.
 * 
 * @author dhfelix
 *
 */
public class RegistrarModel {

	private static final Logger logger = Logger.getLogger(RegistrarModel.class.getName());

	private static final String QUERY_GROUP = "Registrar";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error while loading query group on " + EntityModel.class.getName(), e);
		}
	}

	/**
	 * Store a Registrar with his relations in the database
	 * 
	 * @param registrar
	 *            The registrar to be stored.
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws RequiredValueNotFoundException
	 * @throws IOException
	 */
	public static long storeToDatabase(Registrar registrar, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		long registrarId;

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS);) {
			((RegistrarDAO) registrar).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			registrarId = resultSet.getLong(1);
			registrar.setId(registrarId);
		}

		for (VCard vCard : registrar.getvCardList()) {
			VCardModel.storeToDatabase(vCard, connection);
		}

		VCardModel.storeRegistrarContactToDatabase(registrar.getvCardList(), registrarId, connection);

		storeNestedObjects(registrar, connection);

		return registrarId;
	}

	/**
	 * Get a {@link Registrar} by its Id.
	 * 
	 * @param registrarId
	 *            id of the registrar to look.
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Registrar getById(Long registrarId, Connection connection) throws SQLException, IOException {
		Registrar registrarResult = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getById"));) {
			statement.setLong(1, registrarId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			registrarResult = processResultSet(resultSet, connection);
		}

		getRegistrarNestedObjects(registrarResult, connection);

		return registrarResult;
	}

	/**
	 * Get a {@link Registrar} by its handle id.
	 * 
	 * @param registrarHandle
	 *            handle id of the registrar to look
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Registrar getByHandle(String registrarHandle, Connection connection)
			throws SQLException, IOException {
		Registrar registrarResult = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByHandle"));) {
			statement.setString(1, registrarHandle);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			registrarResult = processResultSet(resultSet, connection);
		}

		getRegistrarNestedObjects(registrarResult, connection);

		return registrarResult;
	}

	/**
	 * Gets and sets the nested objects of a {@link Registrar}.
	 * 
	 * @param registrar
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void getRegistrarNestedObjects(Registrar registrar, Connection connection)
			throws SQLException, IOException {
		Long registrarId = registrar.getId();
		try {
			List<VCard> vCardList = VCardModel.getByRegistrarId(registrarId, connection);
			registrar.setvCardList(vCardList);
		} catch (ObjectNotFoundException e) {
			// Could not have a VCard.
		}

		List<Status> byRarID = StatusModel.getByRegistrarId(registrarId, connection);
		registrar.getStatus().addAll(byRarID);

		List<Link> byRarId2 = LinkModel.getByRegistrarId(registrarId, connection);
		registrar.getLinks().addAll(byRarId2);

		List<Remark> byRarId3 = RemarkModel.getByRegistrarId(registrarId, connection);
		registrar.getRemarks().addAll(byRarId3);

		List<Event> byRarId4 = EventModel.getByRegistrarId(registrarId, connection);
		registrar.getEvents().addAll(byRarId4);
	}

	/**
	 * Gets the id of the {@link Registrar} by its handle id
	 * 
	 * @param registrarHandle
	 *            handle id of the registrar id to look.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	public static Long getIdByHandle(String registrarHandle, Connection connection) throws SQLException {
		Long registrarId = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getRegistarIdByHandle"));) {
			statement.setString(1, registrarHandle);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Entity not found");
			}
			registrarId = resultSet.getLong("rar_id");
		}
		return registrarId;
	}

	/**
	 * Proccess the {@link ResultSet}
	 * 
	 * @param resultSet
	 *            {@link ResultSet} to proccess.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private static Registrar processResultSet(ResultSet resultSet, Connection connection) throws SQLException {
		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Entity not found");
		}

		RegistrarDAO registrar = new RegistrarDAO();
		registrar.loadFromDatabase(resultSet);

		return registrar;
	}

	/**
	 * Store the nested objects of a Registrar.
	 * 
	 * @param registrar
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	private static void storeNestedObjects(Registrar registrar, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		StatusModel.storeRegistrarStatusToDatabase(registrar.getStatus(), registrar.getId(), connection);
		RemarkModel.storeRegistrarRemarksToDatabase(registrar.getRemarks(), registrar.getId(), connection);
		LinkModel.storeRegistrarLinksToDatabase(registrar.getLinks(), registrar.getId(), connection);
		EventModel.storeRegistrarEventsToDatabase(registrar.getEvents(), registrar.getId(), connection);
	}
}
