package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.net.IDN;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mysql.jdbc.Statement;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.server.db.DomainDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.InvalidValueException;
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
	 * @throws InvalidValueException
	 */
	public static Domain findByLdhName(String name, Connection connection) throws SQLException, IOException {
		// TODO use also zone for the search
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByLdhName"))) {
			statement.setString(1, name);
			statement.setString(1, IDN.toASCII(name));
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new SQLException("Object not found.");
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

	/**
	 * Load the nested object of the domain
	 * 
	 * @param domain
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void loadNestedObjects(Domain domain, Connection connection) throws SQLException, IOException {
		Long domainId = domain.getId();

		// Retrieve the events
		try {
			domain.getEvents().addAll(EventModel.getByDomainId(domainId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, events is not required
		}
		// Retrieve the links
		try {
			domain.getLinks().addAll(LinkModel.getByDomainId(domainId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, links is not required
		}
		// Retrieve the status
		try {
			domain.getStatus().addAll(StatusModel.getByDomainId(domainId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, status is not required
		}
		// Retrieve the remarks
		try {
			domain.getRemarks().addAll(RemarkModel.getByDomainId(domainId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, remarks is not required
		}
		// Retrieve the public ids
		try {
			domain.setPublicIds(PublicIdModel.getByDomain(domainId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, public ids is not required
		}
		// Retrieve the secure dns
		try {
			domain.setSecureDNS(SecureDNSModel.getByDomain(domainId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, secure dns is not required
		}
		// Retrieve the variants
		try {
			domain.setVariants(VariantModel.getByDomainId(domainId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, variants is not required
		}
		// Retrieve the domainsNs
		try {
			domain.getNameServers().addAll(NameserverModel.getByDomainId(domainId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, nameservers is not required
		}
		// Retrieve the entities
		try {
			domain.getEntities().addAll(EntityModel.getEntitiesByDomainId(domainId, connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, entities is not required
		}
	}

	/**
	 * Validate if the zone of the request domain is managed by the server
	 * 
	 * @param domainName
	 * @throws InvalidValueException
	 */
	public static void validateDomainZone(String domainName) throws InvalidValueException {
		String[] domainData = domainName.split("\\.");
		String domainZone = domainName.substring(domainData[0].length() + 1);
		if (!ZoneModel.existsZone(domainZone)) {
			throw new InvalidValueException("Zone", "ZoneModel", "Domain");
		}
	}
}
