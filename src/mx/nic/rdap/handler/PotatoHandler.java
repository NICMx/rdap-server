package mx.nic.rdap.handler;

import mx.nic.rdap.RdapRequest;
import mx.nic.rdap.RdapResult;
import mx.nic.rdap.RdapRequestHandler;
import mx.nic.rdap.exception.RequestHandleException;
import mx.nic.rdap.exception.RequestValidationException;
import mx.nic.rdap.result.PotatoResult;

public class PotatoHandler implements RdapRequestHandler {
	
	@Override
	public String getResourceType() {
		return "potato";
	}

	@Override
	public RdapRequest validate(String[] query) throws RequestValidationException {
		return null;
	}

	@Override
	public RdapResult handle(RdapRequest request) throws RequestHandleException {
		return new PotatoResult();
	}
	
}
