package mx.nic.rdap.sample;

import java.net.InetAddress;
import java.net.UnknownHostException;

import mx.nic.rdap.core.catalog.IpVersion;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.db.exception.NotImplementedException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.spi.IpNetworkDAO;

/**
 * This is the class that will handle data-access for IP network lookups
 * (http://<RDAP-server>/ip).
 */
public class SampleIpNetworkDaoImpl implements IpNetworkDAO {

	@Override
	public IpNetwork getByInetAddress(String ipAddress) throws RdapDataAccessException {
		// This method is the one that gets called whenever the user wants to
		// get information regarding one IP network, using an address within it.
		// (example: http://<RDAP-server>/ip/192.0.2.1)

		// You would access your data storage here.
		// Since this is an example and I don't have a data store, I will simply
		// always return the same sample network, no matter what the user asked
		// for.

		// Red Dog will create one SampleIpNetworkDaoImpl instance per network
		// request, so you generally don't need to worry about thread safety.

		IpNetwork ipNetwork = new IpNetwork();
		ipNetwork.setIpVersion(IpVersion.V6);
		try {
			ipNetwork.setStartAddress(InetAddress.getByName("2001:db8::"));
			ipNetwork.setEndAddress(InetAddress.getByName("2001:db8::ff"));
		} catch (UnknownHostException e) {
			throw new RuntimeException("Java attempted a lookup on a literal address. Weird.");
		}
		return ipNetwork;
	}

	@Override
	public IpNetwork getByInetAddress(String ipAddress, Integer cidr) throws RdapDataAccessException {
		// This method is the one that gets called whenever the user wants to
		// get information regarding one IP network, using the "CIDR prefix/CIDR
		// length" format.
		// (example: http://<RDAP-server>/ip/192.0.2.0/24)

		// I will not implement lookups including CIDR, just to show that I can.
		// These will become HTTP 501s.

		// You can also return null instead if you want, but this method allows
		// you to specify a custom error message.
		throw new NotImplementedException("Not implemented yet.");
	}

}
