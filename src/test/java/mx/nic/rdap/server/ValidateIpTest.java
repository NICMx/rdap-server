package mx.nic.rdap.server;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
import mx.nic.rdap.server.exception.BadRequestException;
import mx.nic.rdap.server.util.IpUtil;

public class ValidateIpTest extends TestCase {

	/**
	 * Several ip validations
	 * 
	 * @throws MalformedRequestException
	 */
	@Test
	public void testValidateIpAddress() {
		testIpAddress("1.2", true);
		testIpAddress("192.168.001.004", true);
		testIpAddress("4000000000", true);
		testIpAddress("128.213123", true);
		testIpAddress("128.15.1456", true);
		testIpAddress("128.15.15.300", false);
		testIpAddress("08.015.005.00", true);
		testIpAddress("08.015.005.1", true);
		testIpAddress("018.15.1.1", true);
		testIpAddress("128.15.15.3000", false);
		testIpAddress("128.15.15.46", true);
		testIpAddress("128.15.15.46.45", false);
		testIpAddress("128.15.15.com", false);
		testIpAddress("128.co.15", false);
		testIpAddress("128.15.15.46", true);
		testIpAddress("128.15.15.46.45", false);
		testIpAddress("300.3000", false);
		testIpAddress("300.300.3000", false);
		testIpAddress("255.300.3000", false);
		testIpAddress("255.3000", true);
		testIpAddress("255.255.3000", true);
		testIpAddress("05.255.3000", true);
		testIpAddress("6:6", false);
		testIpAddress("dominio.com:6", false);
		testIpAddress("6::192.168.1.254", true);
		testIpAddress("6::192.168.1", false);
		testIpAddress("6::192.168.1.ca", false);
		testIpAddress("6::123.co", false);

		testIpAddress("0.1.2.2.", false);
		testIpAddress("255.255.255.255", true);
		testIpAddress("255.255.65535", true);
		testIpAddress("255.16777215", true);
		testIpAddress("255.+1", false);

		// for (Long i = 0L ; i <= FIRST_OCTECT_LIMIT.longValue(); i++) {
		// testIpAddress("255.255.255." + i, true);
		// }
		//
		// for (Long i = 0L ; i <= SECOND_OCTECT_LIMIT.longValue(); i++) {
		// testIpAddress("255.255." + i, true);
		// }
		//
		// for (Long i = 0L ; i <= THIRD_OCTECT_LIMIT.longValue(); i++) {
		// testIpAddress("255." + i, true);
		// }
		//
		testIpAddress("4294967295", true);
		testIpAddress("4294967296", false);
		testIpAddress(".255", false);
		testIpAddress("0.255", true);
		testIpAddress("0.0.255", true);
		testIpAddress("0.0.0.255", true);
		testIpAddress("1.255", true);
		testIpAddress("1.1.255", true);
		testIpAddress("1.1.1.255", true);
		testIpAddress("12.255", true);
		testIpAddress("12.12.255", true);
		testIpAddress("12.12.12.255", true);
	}

	private static void testIpAddress(String ipAddress, boolean isValid) {
		boolean exception = false;
		try {
			IpUtil.validateIpAddress(ipAddress);
		} catch (BadRequestException e) {
			exception = true;
			System.out.println("invalid ip Address : " + ipAddress);
			if (isValid) {
				Assert.fail("The ipAddress is supposed to be valid " + ipAddress);
			}
			return;
		}
		System.out.println("valid ip Address : " + ipAddress);
		if (!isValid && !exception) {
			Assert.fail("The ipAddress is supposed to be invalid " + ipAddress);
		}
	}

}
