package mx.nic.rdap.handler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.RdapRequest;
import mx.nic.rdap.RdapRequestHandler;
import mx.nic.rdap.RdapResult;
import mx.nic.rdap.db.QueryGroup;
import mx.nic.rdap.db.QueryLoader;
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
			throws SQLException, ObjectNotFoundException, IOException {
		SampleRequest request = (SampleRequest) rdapRequest;
		QueryGroup queryGroup = QueryLoader.getQueryGroup(getResourceType());

		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("find-domain"))) {
			statement.setString(1, request.getAddress().getHostAddress());
			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}

			return new SampleResult(resultSet.getString("name"));
		}
	}

}
