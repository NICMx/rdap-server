package mx.nic.rdap.request;

import java.net.InetAddress;

import mx.nic.rdap.RdapRequest;

public class SampleRequest implements RdapRequest {

	private InetAddress address;
	private Integer prefixLength;

	public SampleRequest(InetAddress address, Integer prefixLength) {
		super();
		this.address = address;
		this.prefixLength = prefixLength;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public Integer getPrefixLength() {
		return prefixLength;
	}

}
