package mx.nic.rdap.server.db;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Test;

import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nic.rdap.server.Util;
import mx.nic.rdap.server.db.model.IpAddressModel;

/**
 * Test for the class IpAddress
 * 
 * @author dalpuche
 *
 */
public class IpAddressTest {
	/** File from which we will load the database connection. */
	private static final String DATABASE_FILE = "database";

	@Test
	/**
	 * Test that retrieve a nameserverIpAddressesStruct from a Nameserver id
	 */
	public void getByNameserverId() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			NameserverIpAddressesStruct struct = IpAddressModel.getIpAddressStructByNameserverId(1L);
			assert !struct.getIpv4Adresses().isEmpty() && !struct.getIpv6Adresses().isEmpty();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
