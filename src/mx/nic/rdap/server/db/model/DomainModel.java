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

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.db.DomainDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nix.rdap.core.catalog.Rol;

/**
 * Model for the Domain Object
 * 
 * @author evaldes
 *
 */
public class DomainModel {

	private final static Logger logger = Logger.getLogger(DomainModel.class.getName());

	private final static String QUERY_GROUP = "Domain";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Stores Object domain to database
	 * 
	 * @param domain
	 * @param connection
	 * @throws SQLException
	 * @throws RequiredValueNotFoundException
	 * @throws IOException
	 */
	public static Long storeToDatabase(Domain domain, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		String query = queryGroup.getQuery("storeToDatabase");
		Long domainId;
		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			((DomainDAO) domain).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();// TODO Validate if insert was correct

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			domainId = resultSet.getLong(1);
			domain.setId(domainId);
		}
		RemarkModel.storeDomainRemarksToDatabase(domain.getRemarks(), domainId, connection);
		EventModel.storeDomainEventsToDatabase(domain.getEvents(), domainId, connection);
		StatusModel.storeDomainStatusToDatabase(domain.getStatus(), domainId, connection);
		LinkModel.storeDomainLinksToDatabase(domain.getLinks(), domainId, connection);
		NameserverModel.storeDomainNameserversToDatabase(domain.getNameServers(), domainId, connection);
		SecureDNSModel.storeToDatabase(domain.getSecureDNS(), connection);
		// TODO Store Entity domain relation
		// storeDomainEntityRoles(domain.getEntities(), domainId, connection);
		domain.setId(domainId);
		return domainId;
	}

	/**
	 * Finds domain with its Punycode name
	 * 
	 * @param name
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Domain findByLdhName(String name, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByLdhName"))) {
			statement.setString(1, name);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO
																			// Manage
																			// exception
				}
				Domain domain = new DomainDAO(resultSet);
				System.out.println(domain.getId());
				loadNestedObjects(domain, connection);
				return domain;
			}
		}
	}

	/**
	 * Finds domain with its Punycode name
	 * 
	 * @param name
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	public static Domain getDomainById(Long domainId, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getDomainById"))) {
			statement.setLong(1, domainId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");// TODO
																			// Manage
																			// exception
				}
				Domain domain = new DomainDAO(resultSet);
				System.out.println(domain.getId());
				loadNestedObjects(domain, connection);
				return domain;
			}
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
	public static void storeDomainEntityRoles(List<Entity> entities, Long domainId, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		for (Entity entity : entities) {
			Long entityId = entity.getId();
			List<Rol> roles = new ArrayList<Rol>();
			System.out.println("" + entityId + " " + domainId);
			System.out.println(entity.getRoles() + " " + entity.getRoles().get(0).getId());
			try (PreparedStatement statement = connection
					.prepareStatement(queryGroup.getQuery("storeDomainEntityRoles"))) {
				statement.setLong(1, domainId);
				statement.setLong(2, entityId);
				for (Rol rol : roles) {
					statement.setLong(3, rol.getId());
					logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
					statement.executeUpdate();
				}
			}
		}
	}

	public static void loadNestedObjects(Domain domain, Connection connection) throws SQLException, IOException {
		Long domainId = domain.getId();

		domain.setEvents(EventModel.getByDomainId(domainId, connection));
		domain.setLinks(LinkModel.getByDomainId(domainId, connection));
		domain.setStatus(StatusModel.getByDomainId(domainId, connection));
		// TODO domain.setNameServers(NameserverModel.getByDomainId(domainId,
		// connection));
		domain.setRemarks(RemarkModel.getByDomainId(domainId, connection));
		domain.setPublicIds(PublicIdModel.getByDomain(domainId, connection));
		domain.setSecureDNS(SecureDNSModel.getByDomain(domainId, connection));
		domain.setVariants(VariantModel.getByDomainId(domainId, connection));
		domain.setZone(ZoneModel.getByZoneId(domain.getZoneId(), connection));
		// TODO look for entity with domain
		// domain.setEntities(EntityModel.getByDomainId(domainId, connection));
	}

}
