package mx.nic.rdap.sample;

import java.util.Properties;

import mx.nic.rdap.db.exception.InitializationException;
import mx.nic.rdap.db.exception.RdapDataAccessException;
import mx.nic.rdap.db.spi.AutnumDAO;
import mx.nic.rdap.db.spi.DataAccessImplementation;
import mx.nic.rdap.db.spi.DomainDAO;
import mx.nic.rdap.db.spi.EntityDAO;
import mx.nic.rdap.db.spi.IpNetworkDAO;
import mx.nic.rdap.db.spi.NameserverDAO;
import mx.nic.rdap.db.spi.RdapUserDAO;

/**
 * This is the class that essentially tells the RDAP server where the DAOs are.
 * <p>
 * This is so you can just point in configuration to this class instead of every
 * DAO separately.
 */
public class SampleHub implements DataAccessImplementation {

	@Override
	public void init(Properties properties) throws InitializationException {
		// No configuration needed in this example.
	}

	@Override
	public AutnumDAO getAutnumDAO() throws RdapDataAccessException {
		// Not implementing some data-access DAOs is perfectly acceptable; not
		// all object types are needed by every RDAP server.
		// Just return null in those cases.
		// RedDog will translate this into an HTTP 501.
		return null;
	}

	@Override
	public DomainDAO getDomainDAO() throws RdapDataAccessException {
		return null;
	}

	@Override
	public EntityDAO getEntityDAO() throws RdapDataAccessException {
		return null;
	}

	@Override
	public IpNetworkDAO getIpNetworkDAO() throws RdapDataAccessException {
		// This is probably your typical implementation referral; just instance
		// and return the object.
		return new SampleIpNetworkDaoImpl();
	}

	@Override
	public NameserverDAO getNameserverDAO() throws RdapDataAccessException {
		return null;
	}

	@Override
	public RdapUserDAO getRdapUserDAO() throws RdapDataAccessException {
		return null;
	}

}
