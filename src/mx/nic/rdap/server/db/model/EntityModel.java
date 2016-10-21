package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.net.IDN;
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

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.server.db.EntityDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nic.rdap.server.exception.UnprocessableEntityException;
import mx.nix.rdap.core.catalog.Rol;
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

	private final static String GET_ENTITY_ENTITY_QUERY = "getEntitysEntitiesQuery";
	private final static String GET_DOMAIN_ENTITY_QUERY = "getDomainsEntitiesQuery";
	private final static String GET_NS_ENTITY_QUERY = "getNameserversEntitiesQuery";

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error while loading query group on " + EntityModel.class.getName(), e);
		}
	}

	public static Long existsByHandle(String entityHandle, Connection connection) throws SQLException {
		String query = queryGroup.getQuery("getIdByHandle");
		Long entId = null;
		try (PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setString(1, entityHandle);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {
				return null;
			}
			long long1 = rs.getLong("ent_id");
			if (!rs.wasNull()) {
				entId = long1;
			}
		}

		return entId;
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
		Long entityId = existsByHandle(entity.getHandle(), connection);
		// TODO Validate if the entity exist then only store the entity's role,
		// else insert the role and the entity
		if (entityId != null) {
			entity.setId(entityId);
			return entityId;
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

		List<VCard> vCardList = entity.getVCardList();
		if (!vCardList.isEmpty()) {
			for (VCard vCard : vCardList) {
				VCardModel.storeToDatabase(vCard, connection);
			}
			VCardModel.storeRegistrarContactToDatabase(vCardList, entityId, connection);
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
		PublicIdModel.storePublicIdByEntity(entity.getPublicIds(), entity.getId(), connection);
		StatusModel.storeEntityStatusToDatabase(entity.getStatus(), entity.getId(), connection);
		RemarkModel.storeEntityRemarksToDatabase(entity.getRemarks(), entity.getId(), connection);
		LinkModel.storeEntityLinksToDatabase(entity.getLinks(), entity.getId(), connection);
		EventModel.storeEntityEventsToDatabase(entity.getEvents(), entity.getId(), connection);
		for (Entity ent : entity.getEntities()) {
			storeToDatabase(ent, connection);
		}
		RolModel.storeEntityEntityRoles(entity.getEntities(), entity.getId(), connection);

		if (!entity.getRoles().isEmpty() && !entity.getEntities().isEmpty())
			RolModel.storeMainEntityRol(entity.getEntities(), entity, connection);
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
	 * sets the nested objects of the entity.
	 * 
	 * @param entity
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void getNestedObjects(Entity entity, Connection connection) throws SQLException, IOException {

		Long entityId = entity.getId();
		try {
			List<VCard> vCardList = VCardModel.getByEntityId(entityId, connection);
			entity.getVCardList().addAll(vCardList);
		} catch (ObjectNotFoundException e) {
			// Do nothing, vcard is not required
		}
		// Retrieve the status
		try {
			entity.getStatus().addAll(StatusModel.getByEntityId(entityId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, status is not required
		}
		// Retrieve the links
		try {
			entity.getLinks().addAll(LinkModel.getByEntityId(entityId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, links is not required
		}
		// Retrive the remarks
		try {
			entity.getRemarks().addAll(RemarkModel.getByEntityId(entityId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, remarks is not required
		}
		// Retrieve the events
		try {
			entity.getEvents().addAll(EventModel.getByEntityId(entityId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, events is not required
		}
		// Retrieve the public ids
		try {
			entity.getPublicIds().addAll(PublicIdModel.getByEntity(entityId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, public ids is not required
		}
		// Retrieve the entities
		try {
			List<Entity> entitiesByEntityId = getEntitiesByEntityId(entityId, connection);
			entity.getEntities().addAll(entitiesByEntityId);
			entity.getRoles().addAll(RolModel.getMainEntityRol(entitiesByEntityId, entity, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, entities is not required
		}

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
			throw new ObjectNotFoundException("Object not found");
		}

		EntityDAO entity = new EntityDAO();
		entity.loadFromDatabase(resultSet);

		return entity;
	}

	public static List<Entity> getEntitiesByEntityId(Long entityId, Connection connection)
			throws SQLException, IOException {
		List<Entity> entitiesById = getEntitiesById(entityId, connection, GET_ENTITY_ENTITY_QUERY);
		for (Entity ent : entitiesById) {
			List<Rol> entityEntityRol = RolModel.getEntityEntityRol(entityId, ent.getId(), connection);
			ent.getRoles().addAll(entityEntityRol);
		}

		return entitiesById;
	}

	public static List<Entity> getEntitiesByDomainId(Long domainId, Connection connection)
			throws SQLException, IOException {
		List<Entity> entitiesById = getEntitiesById(domainId, connection, GET_DOMAIN_ENTITY_QUERY);
		for (Entity ent : entitiesById) {
			List<Rol> entityEntityRol = RolModel.getDomainEntityRol(domainId, ent.getId(), connection);
			ent.getRoles().addAll(entityEntityRol);
		}
		return entitiesById;
	}

	public static List<Entity> getEntitiesByNameserverId(Long nameserverId, Connection connection)
			throws SQLException, IOException {
		List<Entity> entitiesById = getEntitiesById(nameserverId, connection, GET_NS_ENTITY_QUERY);
		for (Entity ent : entitiesById) {
			List<Rol> entityEntityRol = RolModel.getNameserverEntityRol(nameserverId, ent.getId(), connection);
			ent.getRoles().addAll(entityEntityRol);
		}
		return entitiesById;
	}

	private static List<Entity> getEntitiesById(Long id, Connection connection, String getQueryId)
			throws SQLException, IOException {
		String query = queryGroup.getQuery(getQueryId);
		List<Entity> result = null;
		try (PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setLong(1, id);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet rs = statement.executeQuery();
			if (!rs.next()) {
				return Collections.emptyList();
			}
			result = new ArrayList<>();

			do {
				EntityDAO dao = new EntityDAO();
				dao.loadFromDatabase(rs);
				result.add(dao);
			} while (rs.next());
		}

		setNestedSimpleObjects(result, connection);

		return result;
	}

	private static void setNestedSimpleObjects(List<Entity> entities, Connection connection)
			throws SQLException, IOException {

		for (Entity entity : entities) {
			Long entityId = entity.getId();
			try {
				List<VCard> vCardList = VCardModel.getByEntityId(entityId, connection);
				entity.getVCardList().addAll(vCardList);
			} catch (ObjectNotFoundException e) {
				// Could not have a VCard.
			}

			List<Status> statusList = StatusModel.getByEntityId(entityId, connection);
			entity.getStatus().addAll(statusList);

			List<PublicId> pidList = PublicIdModel.getByEntity(entityId, connection);
			entity.getPublicIds().addAll(pidList);

			List<Event> eventList = EventModel.getByEntityId(entityId, connection);
			entity.getEvents().addAll(eventList);
		}

		return;
	}

	public static List<Entity> searchByHandle(String handle, Connection connection)
			throws UnprocessableEntityException, SQLException, IOException {
		return searchBy(handle, connection, queryGroup.getQuery("searchByPartialHandle"),
				queryGroup.getQuery("getByHandle"));
	}

	public static List<Entity> searchByVCardName(String handle, Connection connection)
			throws UnprocessableEntityException, SQLException, IOException {
		return searchBy(handle, connection, queryGroup.getQuery("searchByPartialName"),
				queryGroup.getQuery("getByName"));
	}

	private static List<Entity> searchBy(String handle, Connection connection, String searchByPartialQuery,
			String getByQuery) throws UnprocessableEntityException, SQLException, IOException {
		String query;
		String criteria;
		List<Entity> entities = new ArrayList<>();
		if (handle.contains("*")) {
			if (!handle.equals(IDN.toASCII(handle))) {
				throw new UnprocessableEntityException("Partial search must contain only ASCII values");
			}

			query = searchByPartialQuery;
			criteria = handle.replace('*', '%');
		} else {
			query = getByQuery;
			criteria = handle;
		}

		try (PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setString(1, criteria);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				EntityDAO ent = new EntityDAO();
				ent.loadFromDatabase(rs);
				getNestedObjects(ent, connection);
				entities.add(ent);
			}
		}

		return entities;
	}

}
