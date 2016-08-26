package mx.nic.rdap.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;

import mx.nic.rdap.RdapRequest;
import mx.nic.rdap.RdapResult;
import mx.nic.rdap.RdapRequestHandler;
import mx.nic.rdap.exception.RequestHandleException;
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
	public RdapResult handle(RdapRequest rdapRequest) throws RequestHandleException {
		SampleRequest request = (SampleRequest) rdapRequest;
		String domain = getDomainForPrefix(request.getAddress(), request.getPrefixLength());
		return new SampleResult(domain);
	}

	private String getDomainForPrefix(InetAddress address, Integer prefixLength) {
		/*
		 * Database lookup goes here. This is an example so just return a
		 * constant.
		 */
		return "domain.mx";
	}

}
