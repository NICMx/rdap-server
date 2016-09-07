package mx.nic.rdap.servlet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import mx.nic.rdap.RdapResult;
import mx.nic.rdap.RdapServlet;
import mx.nic.rdap.Util;
import mx.nic.rdap.db.QueryGroup;
import mx.nic.rdap.exception.MalformedRequestException;
import mx.nic.rdap.exception.ObjectNotFoundException;
import mx.nic.rdap.exception.RequestHandleException;
import mx.nic.rdap.result.SampleResult;

@WebServlet(name = "rdap", urlPatterns = { "/sample/*" })
public class SampleServlet extends RdapServlet {

	/** SHUT UP */
	private static final long serialVersionUID = 1L;
	private static final String QUERY_GROUP = "sample";

	protected QueryGroup queryGroup = null;

	public SampleServlet() throws IOException {
		super();
		this.queryGroup = new QueryGroup(QUERY_GROUP);
	}

	@Override
	protected RdapResult doRdapGet(HttpServletRequest httpRequest, Connection connection)
			throws RequestHandleException, IOException, SQLException {
		SampleRequest request = parseRequest(httpRequest);
		return doQuery(connection, request);
	}

	private SampleRequest parseRequest(HttpServletRequest httpRequest) throws RequestHandleException {
		String query[] = Util.getRequestParams(httpRequest);

		InetAddress address;
		try {
			address = InetAddress.getByName(query[0]);
		} catch (UnknownHostException e) {
			throw new MalformedRequestException(e);
		}

		Integer prefixLength = null;
		if (query.length > 1) {
			try {
				prefixLength = Integer.parseInt(query[1]);
			} catch (NumberFormatException e) {
				throw new MalformedRequestException(e);
			}
		}

		return new SampleRequest(address, prefixLength);
	}

	private RdapResult doQuery(Connection connection, SampleRequest request) throws IOException, SQLException {
		try (PreparedStatement statement = connection.prepareStatement(queryGroup.getQuery("find-domain"))) {
			statement.setString(1, request.address.getHostAddress());
			ResultSet resultSet = statement.executeQuery();

			if (!resultSet.next()) {
				throw new ObjectNotFoundException("Object not found.");
			}

			return new SampleResult(resultSet.getString("name"));
		}
	}

	@Override
	protected RdapResult doRdapHead(HttpServletRequest request, Connection connection) throws RequestHandleException {
		throw new RequestHandleException(501, "Not implemented yet.");
	}

	private class SampleRequest {

		public InetAddress address;
		// public Integer prefixLength;

		public SampleRequest(InetAddress address, Integer prefixLength) {
			super();
			this.address = address;
			// this.prefixLength = prefixLength;
		}

	}

}
