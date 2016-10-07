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

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.server.db.DomainDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Model for the Domain Object
 * 
 * @author evaldes
 *
 */
public class DomainModel {

	private final static Logger logger = Logger.getLogger(DomainModel.class.getName());

	private final static String QUERY_GROUP = "Domain";

	private static QueryGroup queryGroup = null;

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
			statement.executeUpdate();
			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			domainId = resultSet.getLong(1);
			domain.setId(domainId);
		}

		RemarkModel.storeDomainRemarksToDatabase(domain.getRemarks(), domainId, connection);
		EventModel.storeDomainEventsToDatabase(domain.getEvents(), domainId, connection);
		StatusModel.storeDomainStatusToDatabase(domain.getStatus(), domainId, connection);
		LinkModel.storeDomainLinksToDatabase(domain.getLinks(), domainId, connection);

		domain.getSecureDNS().setDomainId(domainId);
		SecureDNSModel.storeToDatabase(domain.getSecureDNS(), connection);

		for (Nameserver ns : domain.getNameServers()) {
			NameserverModel.storeToDatabase(ns, connection);
		}
		NameserverModel.storeDomainNameserversToDatabase(domain.getNameServers(), domainId, connection);

		if (domain.getEntities().size() > 0) {
			for (Entity ent : domain.getEntities()) {
				Long entId = EntityModel.existsByHandle(ent.getHandle(), connection);
				if (entId == null) {
					throw new NullPointerException(
							"Entity: " + ent.getHandle() + " was not insert previously to the database");
				}
				ent.setId(entId);
			}
			RolModel.storeDomainEntityRoles(domain.getEntities(), domainId, connection);
		}

		PublicIdModel.storePublicIdByDomain(domain.getPublicIds(), domain.getId(), connection);

		VariantModel.storeAllToDatabase(domain.getVariants(), domain.getId(), connection);

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
					throw new ObjectNotFoundException("Object not found.");
				}
				Domain domain = new DomainDAO(resultSet);
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
					throw new ObjectNotFoundException("Object not found.");
				}
				Domain domain = new DomainDAO(resultSet);
				loadNestedObjects(domain, connection);
				return domain;
			}
		}
	}

	public static void loadNestedObjects(Domain domain, Connection connection) throws SQLException, IOException {
		Long domainId = domain.getId();

		domain.setEvents(EventModel.getByDomainId(domainId, connection));
		domain.setLinks(LinkModel.getByDomainId(domainId, connection));
		domain.setStatus(StatusModel.getByDomainId(domainId, connection));
		domain.setRemarks(RemarkModel.getByDomainId(domainId, connection));
		domain.setPublicIds(PublicIdModel.getByDomain(domainId, connection));
		domain.setSecureDNS(SecureDNSModel.getByDomain(domainId, connection));
		domain.setVariants(VariantModel.getByDomainId(domainId, connection));

		List<Nameserver> domainsNS = NameserverModel.getByDomainId(domainId, connection);
		domain.getNameServers().addAll(domainsNS);

		List<Entity> entitiesByDomainId = EntityModel.getEntitiesByDomainId(domainId, connection);
		domain.setEntities(entitiesByDomainId);

	}

}
