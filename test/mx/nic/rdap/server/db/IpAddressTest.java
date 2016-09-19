package mx.nic.rdap.server.db;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;

import org.junit.Test;

import mx.nic.rdap.core.db.IpAddress;
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
	 * Test the store of ipAddress in the database
	 */
	public void insert() {
		try {
			DatabaseSession.init(Util.loadProperties(DATABASE_FILE));
			NameserverIpAddressesStruct struct = new NameserverIpAddressesStruct();
			IpAddress ipv4 = new IpAddressDAO();
			ipv4.setType(4);
			ipv4.setNameserverId(1l);
			ipv4.setAddress(InetAddress.getByName("127.0.0.4"));
			IpAddress ipv6 = new IpAddressDAO();
			ipv6.setType(6);
			ipv6.setNameserverId(1l);
			ipv6.setAddress(InetAddress.getByName("2001:db8::1"));
			struct.getIpv4Adresses().add(ipv4);
			// struct.getIpv6Adresses().add(ipv6);
			IpAddressModel.storeToDatabase(struct);
			assert true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			assert false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			assert false;
			e.printStackTrace();
		}
	}

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
