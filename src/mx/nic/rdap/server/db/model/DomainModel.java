package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.net.IDN;
import java.net.Inet4Address;
import java.net.InetAddress;
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
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.DomainDAO;
import mx.nic.rdap.server.db.IpAddressDAO;
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
		if (domain.getSecureDNS() != null) {
			domain.getSecureDNS().setDomainId(domainId);
			SecureDNSModel.storeToDatabase(domain.getSecureDNS(), connection);
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
	public static Domain findByLdhName(String name, Connection connection)
			throws SQLException, IOException, InvalidValueException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByLdhName"))) {
			// if is a reverse address,dont validate the zone
			if (!ZoneModel.isReverseAddress(name))
				validateDomainZone(name);
			statement.setString(1, IDN.toASCII(name));
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

	/**
	 * Searches a domain by it´s name and TLD
	 * 
	 * @param name
	 * @param zone
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws InvalidValueException
	 * @throws IOException
	 */
	public static List<Domain> searchByName(String name, String zone, Connection connection)
			throws SQLException, IOException, InvalidValueException {
		DomainModel.validateDomainZone(name + "." + zone);
		name = name.replaceAll("\\*", "%");

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("searchByNameWZone"))) {
			Integer zoneId = ZoneModel.getIdByZoneName(zone);

			statement.setString(1, name);
			statement.setInt(2, zoneId);
			statement.setInt(3, Util.getMaxNumberOfResultsForUser());

			logger.log(Level.INFO, "Executing query" + statement.toString());
			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}
			List<Domain> domains = new ArrayList<Domain>();
			do {
				DomainDAO domain = new DomainDAO(resultSet);
				loadNestedObjects(domain, connection);
				domains.add(domain);
			} while (resultSet.next());

			return domains;
		}
	}

	/**
	 * Searches a domain by it's name when user don´t care about the TLD
	 * 
	 * @param name
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws InvalidValueException
	 * @throws IOException
	 */
	public static List<Domain> searchByName(String name, Connection connection) throws SQLException, IOException {
		name = name.replaceAll("\\*", "%");
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("searchByNameWOutZone"))) {
			statement.setString(1, name);
			statement.setInt(2, Util.getMaxNumberOfResultsForUser());
			logger.log(Level.INFO, "Executing query" + statement.toString());
			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}
			List<Domain> domains = new ArrayList<Domain>();
			do {
				DomainDAO domain = new DomainDAO(resultSet);
				loadNestedObjects(domain, connection);
				domains.add(domain);
			} while (resultSet.next());

			return domains;
		}
	}

	/**
	 * Searches all domains with a nameserver by name
	 * 
	 * @param name
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<Domain> searchByNsLdhName(String name, Connection connection) throws SQLException, IOException {
		name = name.replace("*", "%");
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("searchByNsLdhName"))) {
			statement.setString(1, name);
			statement.setInt(2, Util.getMaxNumberOfResultsForUser());
			logger.log(Level.INFO, "Executing query" + statement.toString());
			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}
			List<Domain> domains = new ArrayList<Domain>();
			do {
				// no Zone Id
				DomainDAO domain = new DomainDAO(resultSet);
				loadNestedObjects(domain, connection);
				domains.add(domain);
			} while (resultSet.next());
			return domains;
		}
	}

	/**
	 * searches all domains with a nameserver by address
	 * 
	 * @param ip
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<Domain> searchByNsIp(String ip, Connection connection) throws SQLException, IOException {
		IpAddressDAO ipAddress = new IpAddressDAO();
		InetAddress address = InetAddress.getByName(ip);
		ipAddress.setAddress(address);
		if (ipAddress.getAddress() instanceof Inet4Address) {
			ipAddress.setType(4);

		} else if (ipAddress.getAddress() instanceof Inet4Address) {
			ipAddress.setType(6);
		}
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("searchByNsIp"))) {
			statement.setInt(1, ipAddress.getType());
			statement.setString(2, ipAddress.getAddress().getHostAddress());
			statement.setString(3, ipAddress.getAddress().getHostAddress());
			statement.setInt(4, Util.getMaxNumberOfResultsForUser());
			logger.log(Level.INFO, "Executing query" + statement.toString());
			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}
			List<Domain> domains = new ArrayList<Domain>();
			do {
				DomainDAO domain = new DomainDAO(resultSet);
				loadNestedObjects(domain, connection);
				domains.add(domain);
			} while (resultSet.next());
			return domains;
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
	 * @throws ObjectNotFoundException
	 */
	public static void validateDomainZone(String domainName) throws InvalidValueException, ObjectNotFoundException {
		String[] domainData = domainName.split("\\.");
		String domainZone = "";
		try {
			domainZone = domainName.substring(domainData[0].length() + 1);
		} catch (IndexOutOfBoundsException iobe) {
			throw new InvalidValueException("Zone", "ZoneModel", "Domain");
		}
		if (!ZoneModel.existsZone(domainZone)) {
			throw new ObjectNotFoundException("Zone not found.");
		}
	}
}
