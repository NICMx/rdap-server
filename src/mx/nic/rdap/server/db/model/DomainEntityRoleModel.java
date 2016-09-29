package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

public class DomainEntityRoleModel {

	private final static Logger logger = Logger.getLogger(DomainEntityRoleModel.class.getName());

	private final static String QUERY_GROUP = "DomainEntityRole";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error while loading query group on " + EntityModel.class.getName(), e);
		}
	}

	/**
	 * Stores all domain entity role relations into database on table
	 * domain_entity_role
	 * 
	 * @param entities
	 *            A list of entities from the domain
	 * @param domainId
	 *            Unique identifier of the domain
	 * @param connection
	 * @throws SQLException
	 * @throws RequiredValueNotFoundException
	 * @throws IOException
	 */
	public static void storeAllToDatabase(List<Entity> entities, Long domainId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		for (Entity entity : entities) {
			Long entityId = EntityModel.storeToDatabase(entity, connection);
			entityId += 0;// TODO delete warning shutupper
			// TODO roles on entity
			// entity.getRoles(), connection);
		}
	}

	public static void storeToDatabase(Long entityId, Long domainId, Connection connection) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("storeToDatabase"))) {
			/*
			 * for(Role role : roles){ statement.setLong(1, domainId);
			 * statement.setLong(2, entityId); statment.setLong(3, roleId);
			 * logger.log(LevelINFO, "Executing QUERY:" + statement.toString());
			 * statement.executeUpdate(); } TODO Add Role object to Entity
			 */
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());// TODO
																				// delete
																				// warning
																				// shutupper
		}
	}
}
