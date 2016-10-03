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

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nix.rdap.core.catalog.Rol;

/**
 * Model for roles of nested entities of main objects.
 * 
 * @author dhfelix
 *
 */
public class RolModel {

	private final static Logger logger = Logger.getLogger(RolModel.class.getName());

	private final static String QUERY_GROUP = "rol";

	private static final String STORE_DOMAIN_ROLES = "storeDomainsEntityRol";
	private static final String STORE_ENTITY_ROLES = "storeEntitiesEntityRol";
	private static final String STORE_NS_ROLES = "storeNSEntityRol";

	private static final String GET_DOMAIN_ROLES = "getDomainRol";
	private static final String GET_ENTITY_ROLES = "getEntityRol";
	private static final String GET_NS_ROLES = "getNSRol";

	private static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error while loading query group on " + EntityModel.class.getName(), e);
		}
	}

	public static List<Rol> getDomainEntityRol(Long domainId, Long entityId, Connection connection)
			throws SQLException {
		return getNestedEntityRol(domainId, entityId, connection, GET_DOMAIN_ROLES);
	}

	public static List<Rol> getNameserverEntityRol(Long nameserverId, Long entityId, Connection connection)
			throws SQLException {
		return getNestedEntityRol(nameserverId, entityId, connection, GET_NS_ROLES);
	}

	public static List<Rol> getEntityEntityRol(Long mainEntityId, Long nestedEntityId, Connection connection)
			throws SQLException {
		return getNestedEntityRol(mainEntityId, nestedEntityId, connection, GET_ENTITY_ROLES);
	}

	private static List<Rol> getNestedEntityRol(Long ownerId, Long nestedEntityId, Connection connection,
			String getQuery) throws SQLException {
		String query = queryGroup.getQuery(getQuery);
		List<Rol> roles = null;
		try (PreparedStatement statement = connection.prepareStatement(query);) {
			statement.setLong(1, ownerId);
			statement.setLong(2, nestedEntityId);
			ResultSet rs = statement.executeQuery();

			if (!rs.next()) {
				return Collections.emptyList();
			}

			roles = new ArrayList<>();
			do {
				int rolId = rs.getInt(1);
				if (rs.wasNull()) {
					throw new NullPointerException("Rol id was null");
				}
				roles.add(Rol.getById(rolId));
			} while (rs.next());
		}

		return roles;
	}

	public static void storeEntityEntityRoles(List<Entity> entities, Long entityId, Connection connection)
			throws SQLException {
		storeEntitiesRoles(entities, entityId, connection, STORE_ENTITY_ROLES);
	}

	public static void storeNameserverEntityRoles(List<Entity> entities, Long nsId, Connection connection)
			throws SQLException {
		storeEntitiesRoles(entities, nsId, connection, STORE_NS_ROLES);
	}

	public static void storeDomainEntityRoles(List<Entity> entities, Long domainId, Connection connection)
			throws SQLException {
		storeEntitiesRoles(entities, domainId, connection, STORE_DOMAIN_ROLES);
	}

	private static void storeEntitiesRoles(List<Entity> entities, Long ownerId, Connection connection,
			String storeQuery) throws SQLException {
		if (entities.isEmpty())
			return;

		String query = queryGroup.getQuery(storeQuery);

		try (PreparedStatement statement = connection.prepareStatement(query);) {
			for (Entity entity : entities) {
				for (Rol rol : entity.getRoles()) {
					statement.setLong(1, ownerId);
					statement.setLong(2, entity.getId());
					statement.setLong(3, rol.getId());
					logger.log(Level.INFO, "Executing QUERY" + statement.toString());
					statement.execute();
				}
			}
		}
	}

}
