package mx.nic.rdap.server;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
import mx.nic.rdap.server.exception.MalformedRequestException;

public class ValidateIpTest extends TestCase {

	/**
	 * Several ip validations
	 * 
	 * @throws MalformedRequestException
	 */
	@Test
	public void testValidateIpAddress() throws MalformedRequestException {
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

	}

	private static void testIpAddress(String ipAddress, boolean isValid) {
		boolean exception = false;
		try {
			Util.validateIpAddress(ipAddress);
		} catch (MalformedRequestException e) {
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
