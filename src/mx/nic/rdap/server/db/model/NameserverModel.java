package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.net.IDN;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.server.db.NameserverDAO;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
import mx.nic.rdap.server.exception.UnprocessableEntityException;
import mx.nix.rdap.core.catalog.Rol;

/**
 * Model for the Nameserver Object
 * 
 * @author dalpuche
 *
 */
public class NameserverModel {

	private final static Logger logger = Logger.getLogger(NameserverModel.class.getName());

	private final static String QUERY_GROUP = "Nameserver";

	private static QueryGroup queryGroup = null;

	static {
		try {
			queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	/**
	 * Validate the required attributes for the nameserver
	 * 
	 * @param nameserver
	 * @throws RequiredValueNotFoundException
	 */
	private static void isValidForStore(Nameserver nameserver) throws RequiredValueNotFoundException {
		if (nameserver.getPunycodeName() == null || nameserver.getPunycodeName().isEmpty())
			throw new RequiredValueNotFoundException("ldhName", "Nameserver");
	}

	/**
	 * Store a namerserver in the database
	 * 
	 * @param nameserver
	 * @throws IOException
	 * @throws SQLException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeToDatabase(Nameserver nameserver, Connection connection)
			throws IOException, SQLException, RequiredValueNotFoundException {
		isValidForStore(nameserver);
		String query = queryGroup.getQuery("storeToDatabase");
		Long nameserverId = null;
		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			((NameserverDAO) nameserver).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			statement.executeUpdate();
			ResultSet result = statement.getGeneratedKeys();
			result.next();
			nameserverId = result.getLong(1);// The id of the nameserver
												// inserted
			nameserver.setId(nameserverId);
		}
		IpAddressModel.storeToDatabase(nameserver.getIpAddresses(), nameserverId, connection);
		StatusModel.storeNameserverStatusToDatabase(nameserver.getStatus(), nameserverId, connection);
		RemarkModel.storeNameserverRemarksToDatabase(nameserver.getRemarks(), nameserverId, connection);
		LinkModel.storeNameserverLinksToDatabase(nameserver.getLinks(), nameserverId, connection);
		EventModel.storeNameserverEventsToDatabase(nameserver.getEvents(), nameserverId, connection);
		if (nameserver.getEntities().size() > 0) {
			for (Entity entity : nameserver.getEntities()) {
				Long entityId = EntityModel.existsByHandle(entity.getHandle(), connection);
				if (entityId == null) {
					throw new NullPointerException(
							"Entity: " + entity.getHandle() + " was not insert previously to the database");
				}
				entity.setId(entityId);
			}
			RolModel.storeNameserverEntityRoles(nameserver.getEntities(), nameserverId, connection);
		}
	}

	/**
	 * Store a list of nameservers that belong from a domain
	 * 
	 * @param nameservers
	 * @param domainId
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 * @throws RequiredValueNotFoundException
	 */
	public static void storeDomainNameserversToDatabase(List<Nameserver> nameservers, Long domainId,
			Connection connection) throws SQLException, IOException, RequiredValueNotFoundException {
		if (nameservers.isEmpty()) {
			return;
		}

		String query = queryGroup.getQuery("storeDomainNameserversToDatabase");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			Long nameserverId;
			for (Nameserver nameserver : nameservers) {
				statement.setLong(1, domainId);
				nameserverId = nameserver.getId();
				statement.setLong(2, nameserverId);
				logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
				statement.executeUpdate();
			}
		}
	}

	/**
	 * Find a nameserver object by it's name
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Nameserver findByName(String name, Connection connection) throws IOException, SQLException {
		String query = queryGroup.getQuery("findByName");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, IDN.toASCII(name));
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found.");
				}
				Nameserver nameserver = new NameserverDAO(resultSet);
				NameserverModel.loadNestedObjects(nameserver, connection);
				return nameserver;
			}
		}
	}

	/**
	 * Search nameservers by a namePattern
	 * 
	 * @param namePattern
	 * @return
	 * @throws UnprocessableEntityException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<NameserverDAO> searchByName(String namePattern, Connection connection)
			throws UnprocessableEntityException, SQLException, IOException {
		String query = "";
		String criteria = "";
		List<NameserverDAO> nameservers = new ArrayList<NameserverDAO>();
		if (namePattern.contains("*")) {// check if is a partial search

			if (namePattern.compareTo(IDN.toASCII(namePattern)) != 0) {
				throw new UnprocessableEntityException("Partial search must contain only ASCII values");
			}
			query = queryGroup.getQuery("searchByPartialName");
			criteria = namePattern.replace('*', '%');
		} else {
			query = queryGroup.getQuery("findByName");
			criteria = namePattern;
		}
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, criteria);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {

				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found");
				}
				do {
					NameserverDAO nameserver = new NameserverDAO(resultSet);
					NameserverModel.loadNestedObjects(nameserver, connection);
					nameservers.add(nameserver);
				} while (resultSet.next());

				return nameservers;
			}
		}
	}

	/**
	 * Search nameservers by a ipaddressPattern
	 * 
	 * @param ipaddressPattern
	 * @return
	 * @throws UnprocessableEntityException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<NameserverDAO> searchByIp(String ipaddressPattern, Connection connection)
			throws UnprocessableEntityException, SQLException, IOException {
		String query = "";
		List<NameserverDAO> nameservers = new ArrayList<NameserverDAO>();
		try {
			InetAddress address = InetAddress.getByName(ipaddressPattern);
			if (address instanceof Inet6Address) {
				query = queryGroup.getQuery("searchByIp6");
			} else if (address instanceof Inet4Address) {
				query = queryGroup.getQuery("searchByIp4");
			}
		} catch (UnknownHostException e) {
			throw new UnprocessableEntityException("Requested ip is invalid.");
		}
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, ipaddressPattern);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {

				if (!resultSet.next()) {
					throw new ObjectNotFoundException("Object not found");
				}
				do {
					NameserverDAO nameserver = new NameserverDAO(resultSet);
					NameserverModel.loadNestedObjects(nameserver, connection);
					nameservers.add(nameserver);
				} while (resultSet.next());

				return nameservers;
			}
		}
	}

	/**
	 * Find nameservers that belongs from a domain by the domain's id
	 * 
	 * @param domainId
	 * @param connection
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public static List<Nameserver> getByDomainId(Long domainId, Connection connection)
			throws SQLException, IOException {
		String query = queryGroup.getQuery("getByDomainId");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setLong(1, domainId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return Collections.emptyList();
				}
				List<Nameserver> nameservers = new ArrayList<Nameserver>();
				do {
					Nameserver nameserver = new NameserverDAO(resultSet);
					NameserverModel.loadNestedObjects(nameserver, connection);
					nameservers.add(nameserver);
				} while (resultSet.next());
				return nameservers;
			}
		}
	}

	/**
	 * verify if a nameserver exist by it's name
	 * 
	 * @param name
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	public static boolean existNameserverByName(String name, Connection connection) throws IOException, SQLException {
		String query = queryGroup.getQuery("existByName");
		try (PreparedStatement statement = connection.prepareStatement(query)) {
			statement.setString(1, name);
			logger.log(Level.INFO, "Executing QUERY:" + statement.toString());
			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) {
					return false;
				}
				return true;
			}
		}
	}

	/**
	 * Load the nested object of the nameserver
	 * 
	 * @param nameserver
	 * @param connection
	 * @throws SQLException
	 * @throws IOException
	 */
	private static void loadNestedObjects(Nameserver nameserver, Connection connection)
			throws IOException, SQLException {

		// Retrieve the ipAddress
		try {
			nameserver.setIpAddresses(IpAddressModel.getIpAddressStructByNameserverId(nameserver.getId(), connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, ipaddresses is not required
		}
		// Retrieve the status
		try {
			nameserver.getStatus().addAll(StatusModel.getByNameServerId(nameserver.getId(), connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, status is not required
		}
		// Retrieve the remarks
		try {
			nameserver.getRemarks().addAll(RemarkModel.getByNameserverId(nameserver.getId(), connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, remarks is not required
		}
		// Retrieve the links
		try {
			nameserver.getLinks().addAll(LinkModel.getByNameServerId(nameserver.getId(), connection));
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, links is not required
		}
		// Retrieve the events
		nameserver.getEvents().addAll(EventModel.getByNameServerId(nameserver.getId(), connection));
		// Retrieve the entities
		try {
			List<Entity> entities = EntityModel.getEntitiesByNameserverId(nameserver.getId(), connection);
			nameserver.getEntities().addAll(entities);
			for (Entity entity : entities) {
				List<Rol> roles = RolModel.getNameserverEntityRol(nameserver.getId(), entity.getId(), connection);
				entity.setRoles(roles);
			}
		} catch (ObjectNotFoundException onfe) {
			// Do nothing, entitys is not required
		}
	}
}
