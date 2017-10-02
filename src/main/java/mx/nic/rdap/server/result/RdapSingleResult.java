package mx.nic.rdap.server.result;

import mx.nic.rdap.core.db.RdapObject;

public abstract class RdapSingleResult extends RdapResult{

	private RdapObject rdapObject;
	
	public RdapObject getRdapObject() {
		return rdapObject;
	}
	
	public void setRdapObject(RdapObject rdapObject) {
		this.rdapObject = rdapObject;
	}
	
	
	
}
