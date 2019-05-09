package mx.nic.rdap.sample;

import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.ip.AddressBlock;
import mx.nic.rdap.core.ip.IpAddressFormatException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.spi.IpNetworkDAO;

/**
 * This is the class that will handle data-access for IP network lookups
 * (http://<RDAP-server>/ip).
 */
public class SampleIpNetworkDaoImpl implements IpNetworkDAO {

	@Override
	public IpNetwork getByAddressBlock(AddressBlock ipAddress) throws RdapDataAccessException {
		// This method is the one that gets called whenever the user wants to
		// get information regarding one IP network.
		// (example: http://<RDAP-server>/ip/192.0.2.0/24)

		// You would access your data storage here.
		// Since this is an example and I don't have a data store, I will simply
		// always return the same sample network, no matter what the user asked
		// for.

		// RedDog will create one SampleIpNetworkDaoImpl instance per network
		// request, so you generally don't need to worry about thread safety.
		// (You probably want to worry about the speed of your constructor,
		// though.)

		AddressBlock block;
		try {
			block = new AddressBlock("2001:db8::", 120);
		} catch (IpAddressFormatException e) {
			throw new RuntimeException("Programming error: Literal address could not be parsed.", e);
		}

		IpNetwork network = new IpNetwork();
		network.setAddressBlock(block);

		return network;
	}

	// This is what you'd do if you didn't want to implement one of the methods
	// @Override
	// public IpNetwork getByAddressBlock(AddressBlock ipAddress)
	// throws RdapDataAccessException {
	// // These exceptions become HTTP 501s.
	// throw new NotImplementedException("Not implemented yet.");
	// }

}
