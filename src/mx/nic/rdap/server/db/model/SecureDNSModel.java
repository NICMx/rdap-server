package mx.nic.rdap.server.db.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.server.db.QueryGroup;
import mx.nic.rdap.server.db.SecureDNSDAO;
import mx.nic.rdap.server.exception.ObjectNotFoundException;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Model for the SecureDNS object
 * 
 * @author evaldes
 * @author dhfelix
 *
 */
public class SecureDNSModel {

	private final static Logger logger = Logger.getLogger(SecureDNSModel.class.getName());

	private final static String QUERY_GROUP = "SecureDNS";

	protected static QueryGroup queryGroup = null;

	static {
		try {
			SecureDNSModel.queryGroup = new QueryGroup(QUERY_GROUP);
		} catch (IOException e) {
			throw new RuntimeException("Error loading query group");
		}
	}

	public static Long storeToDatabase(SecureDNS secureDns, Connection connection)
			throws SQLException, IOException, RequiredValueNotFoundException {
		String query = queryGroup.getQuery("storeToDatabase");
		try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
			((SecureDNSDAO) secureDns).storeToDatabase(statement);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			statement.executeUpdate();// TODO Validate if insert was correct

			ResultSet resultSet = statement.getGeneratedKeys();
			resultSet.next();
			Long secureDnsId = resultSet.getLong(1);

			secureDns.setId(secureDnsId);

		}

		DsDataModel.storeAllToDatabase(secureDns.getDsData(), secureDns.getId(), connection);

		return secureDns.getId();
	}

	public static SecureDNS getByDomain(Long domainId, Connection connection) throws SQLException, IOException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("getByDomain"));) {
			statement.setLong(1, domainId);
			logger.log(Level.INFO, "Executing QUERY: " + statement.toString());
			ResultSet resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object Not Found");
			}

			SecureDNSDAO secureDns = new SecureDNSDAO(resultSet);
			secureDns.setDsData(DsDataModel.getBySecureDnsId(secureDns.getId(), connection));
			return secureDns;
		}
	}
}
