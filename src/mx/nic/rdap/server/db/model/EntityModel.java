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

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Registrar;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.server.db.EntityDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.Status;

/**
 * Model for the Entity Object
 * 
 * @author dhfelix
 *
 */
public class EntityModel {

	private final static Logger logger = Logger.getLogger(EntityModel.class.getName());

	private final static String QUERY_GROUP = "Entity";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error while loading query group on " + EntityModel.class.getName(), e);
		}
	}

	/**
	 * Store an Entity with his relations in the database
	 * 
	 * @param entity
	 *            The entity to be stored.
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws RequiredValueNotFoundException
	 * @throws IOException
	 */
	public static long storeToDatabase(Entity entity, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		long entityId;

		VCard vCard = entity.getVCard();
		if (vCard != null) {
			long vCardResultId = 0;
			if (vCard.getId() == null) {
				vCardResultId = VCardModel.storeToDatabase(vCard, connection);
			}
			entity.setVCardId(vCardResultId);
		}

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"),
				Statement.RETURN_GENERATED_KEYS);) {
			((EntityDAO) entity).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			entityId = resultSet.getLong(1);
			entity.setId(entityId);
		}

		storeNestedObjects(entity, connection);

		return entityId;
	}

	/**
	 * Store the nested objects of the entity.
	 * 
	 * @param entity
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	private static void storeNestedObjects(Entity entity, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		StatusModel.storeEntityStatusToDatabase(entity.getStatus(), entity.getId(), connection);
		RemarkModel.storeEntityRemarksToDatabase(entity.getRemarks(), entity.getId(), connection);
		LinkModel.storeEntityLinksToDatabase(entity.getLinks(), entity.getId(), connection);
		EventModel.storeEntityEventsToDatabase(entity.getEvents(), entity.getId(), connection);
	}

	/**
	 * Get an entity from the database by its ID.
	 * 
	 * @param entityId
	 *            id of the entity to look.
	 * @param connection
	 *            connection use to query the object.
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Entity getById(Long entityId, Connection connection) throws SQLException, IOException {
		Entity entResult = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getById"));) {
			statement.setLong(1, entityId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			entResult = processResultSet(resultSet, connection);
		}

		getNestedObjects(entResult, connection);
		return entResult;
	}

	/**
	 * Get an entity from the database by its handle id
	 * 
	 * @param entityHandle
	 *            Handle of the entity to look.
	 * @param connection
	 *            connection use to query the object.
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Entity getByHandle(String entityHandle, Connection connection) throws SQLException, IOException {
		Entity entResult = null;
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByHandle"));) {
			statement.setString(1, entityHandle);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			entResult = processResultSet(resultSet, connection);
		}

		getNestedObjects(entResult, connection);
		return entResult;
	}

	/**
	 * Get an entity by its domainId
	 * 
	 * @param domainId
	 *            Unique identifier of a domain.
	 * @param connection
	 *            Connection used to query the object.
	 * @return
	 * @throws SQLException
	 */
	public static List<Entity> getByDomainId(Long domainId, Connection connection) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDomain"))) {// TODO
																												// query
			statement.setLong(1, domainId);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO
																			// manage
																			// exception
				}
				List<Entity> entities = new ArrayList<Entity>();
				do {
					Entity entity = processResultSet(resultSet, connection);// TODO
																			// validate
																			// output
					entities.add(entity);
				} while (resultSet.next());
				return entities;
			}
		}
	}

	/**
	 * sets the nested objects of the entity.
	 * 
	 * @param entity
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void getNestedObjects(Entity entity, Connection connection) throws SQLException, IOException {
		Registrar rar = RegistrarModel.getById(entity.getRarId(), connection);
		entity.setRegistrar(rar);

		VCard vCard = VCardModel.getById(entity.getVCardId(), connection);
		entity.setVCard(vCard);

		Long entityId = entity.getId();

		List<Status> byEntityId = StatusModel.getByEntityId(entityId, connection);
		entity.getStatus().addAll(byEntityId);

		List<Link> byEntityId2 = LinkModel.getByEntityId(entityId, connection);
		entity.getLinks().addAll(byEntityId2);

		List<Remark> byEntityId3 = RemarkModel.getByEntityId(entityId, connection);
		entity.getRemarks().addAll(byEntityId3);

		List<Event> byEntityId4 = EventModel.getByEntityId(entityId, connection);
		entity.getEvents().addAll(byEntityId4);
	}

	/**
	 * Proccess the {@link ResultSet} of the query.
	 * 
	 * @param resultSet
	 *            {@link ResultSet} to proccess.
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	private static Entity processResultSet(ResultSet resultSet, Connection connection) throws SQLException {
		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Entity not found");
		}

		EntityDAO entity = new EntityDAO();
		entity.loadFromDatabase(resultSet);

		return entity;
	}

}
