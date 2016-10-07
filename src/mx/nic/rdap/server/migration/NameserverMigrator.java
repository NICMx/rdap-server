package mx.nic.rdap.server.migration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mx.nic.rdap.core.db.IpAddress;
import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nic.rdap.server.db.NameserverDAO;
import mx.nic.rdap.server.db.model.NameserverModel;
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.InvalidadDataStructure;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;

/**
 * Class that process the nameservers from the client's database and store them
 * in the RDAP database
 * 
 * @author dalpuche
 *
 */
public class NameserverMigrator {

	/**
	 * Process the resultSet of the select statement and return a list of
	 * Nameservers
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 * @throws RequiredValueNotFoundException
	 * @throws InvalidValueException
	 * @throws InvalidadDataStructure
	 */
	public static List<NameserverDAO> getNameserversFromResultSet(ResultSet result)
			throws SQLException, RequiredValueNotFoundException, InvalidValueException, InvalidadDataStructure {
		List<NameserverDAO> nameservers = new ArrayList<NameserverDAO>();
		while (result.next()) {
			NameserverDAO nameserver = new NameserverDAO();
			if (MigrationUtil.isResultSetValueValid(result.getString("handle")))
				nameserver.setHandle(result.getString("handle").trim());
			else {
				throw new RequiredValueNotFoundException("Handle", "Nameserver");
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("ldh_name")))
				nameserver.setPunycodeName(result.getString("ldh_name").trim());
			else {
				throw new RequiredValueNotFoundException("LDHName", "Nameserver");
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("port43")))
				nameserver.setPort43(result.getString("port43").trim());
			else {
				throw new RequiredValueNotFoundException("Port43", "Nameserver");
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("rdap_status"))) {
				nameserver.setStatus(MigrationUtil.getRDAPStatusFromResultSet(result.getString("rdap_status")));
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("epp_status"))) {
				nameserver.getStatus()
						.addAll(MigrationUtil.getRDAPStatusFromEPPStatusResultSet(result.getString("epp_status")));
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("events"))) {
				nameserver.setEvents(MigrationUtil.getEventsFromResultSet(result.getString("events")));
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("ip_addresses"))) {
				nameserver.setIpAddresses(getNameserverIpAddressesFromResultSet(result.getString("ip_addresses")));
			}
			if (MigrationUtil.isResultSetValueValid(result.getString("entities"))) {
				nameserver.setEntities(MigrationUtil.getEntityAndRolesFromResultSet(result.getString("entities")));
			}
			nameservers.add(nameserver);
		}
		return nameservers;
	}

	/**
	 * Store the nameservers in the RDAP database
	 * 
	 * @param nameservers
	 * @param con
	 */
	public static void storeNameserversInRDAPDatabase(List<NameserverDAO> nameservers, Connection con) {
		for (NameserverDAO nameserver : nameservers) {
			try {
				NameserverModel.storeToDatabase(nameserver, con);
			} catch (IOException | SQLException | RequiredValueNotFoundException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	/**
	 * Split the resultSet String to a NameserverIpAddressesStruct
	 * 
	 * @param result
	 *            must have the form "ipAddressType | IpAddress"
	 * @return
	 * @throws RequiredValueNotFoundException
	 * @throws InvalidValueException
	 * @throws InvalidadDataStructure
	 */
	private static NameserverIpAddressesStruct getNameserverIpAddressesFromResultSet(String result)
			throws RequiredValueNotFoundException, InvalidValueException, InvalidadDataStructure {
		NameserverIpAddressesStruct struct = new NameserverIpAddressesStruct();
		try {
			List<List<String>> ipAddressesList = MigrationUtil.getDataStructureList(result, 2);
			for (List<String> ipAddressesData : ipAddressesList) {
				String ipAddressType = ipAddressesData.get(0);
				String ipAddressValue = ipAddressesData.get(1);
				IpAddress ipAddress = new IpAddress();
				try {
					if (MigrationUtil.isResultSetValueValid(ipAddressType)) {
						ipAddress.setType(Integer.parseInt(ipAddressType.trim()));
						if (ipAddress.getType() != 4 && ipAddress.getType() != 6) {
							throw new InvalidValueException("Type", "IpAddress", ipAddressType);
						}
					} else
						throw new RequiredValueNotFoundException("Type", "IpAddress");

					if (MigrationUtil.isResultSetValueValid(ipAddressValue))
						ipAddress.setAddress(InetAddress.getByName(ipAddressValue.trim()));
					else
						throw new RequiredValueNotFoundException("Address", "IpAddress");

					if (ipAddress.getType() == 4) {
						struct.getIpv4Adresses().add(ipAddress);
					} else {
						struct.getIpv6Adresses().add(ipAddress);
					}
				} catch (NumberFormatException nfe) {
					throw new InvalidValueException("Type", "IpAddress", ipAddressType);
				} catch (UnknownHostException e) {
					throw new InvalidValueException("Address", "IpAddress", ipAddressValue);
				}
			}
		} catch (InvalidadDataStructure e) {
			throw new InvalidadDataStructure("IpaddressStruct", "ipAddressType | IpAddress");
		}
		return struct;

	}

}
