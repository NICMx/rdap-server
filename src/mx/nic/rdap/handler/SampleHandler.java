package mx.nic.rdap.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import mx.nic.rdap.RdapRequest;
import mx.nic.rdap.RdapRequestHandler;
import mx.nic.rdap.RdapResult;
import mx.nic.rdap.exception.ObjectNotFoundException;
import mx.nic.rdap.exception.RequestValidationException;
import mx.nic.rdap.request.SampleRequest;
import mx.nic.rdap.result.SampleResult;

public class SampleHandler implements RdapRequestHandler {

	@Override
	public String getResourceType() {
		return "sample";
	}

	@Override
	public RdapRequest validate(String[] query) throws RequestValidationException {
		InetAddress address;
		try {
			address = InetAddress.getByName(query[0]);
		} catch (UnknownHostException e) {
			throw new RequestValidationException(e);
		}

		Integer prefixLength = null;
		if (query.length > 1) {
			try {
				prefixLength = Integer.parseInt(query[1]);
			} catch (NumberFormatException e) {
				throw new RequestValidationException(e);
			}
		}

		return new SampleRequest(address, prefixLength);
	}

	@Override
	public RdapResult handle(RdapRequest rdapRequest, Connection connection)
			throws SQLException, ObjectNotFoundException {
		SampleRequest request = (SampleRequest) rdapRequest;
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(buildQuery(request));

		if (!resultSet.next()) {
			throw new ObjectNotFoundException("Object not found.");
		}
		String domain = resultSet.getString("name");
		return new SampleResult(domain);
	}

	/**
	 * TODO prepared statements.
	 */
	private String buildQuery(SampleRequest request) {
		StringBuilder query = new StringBuilder();
		byte[] components = request.getAddress().getAddress();

		query.append("select name from rdap.domain where ip='");
		/* "& 0xFF" prevents > 127 from being negative. */
		query.append(components[0] & 0xFF).append(".");
		query.append(components[1] & 0xFF).append(".");
		query.append(components[2] & 0xFF).append(".");
		query.append(components[3] & 0xFF).append("'");

		return query.toString();
	}

}
