package mx.nic.rdap.server.result;

import java.util.List;

import mx.nic.rdap.core.db.RdapObject;

public abstract class RdapSearchResult extends RdapResult{

	private List<? extends RdapObject> rdapObjects;
	
	public List<? extends RdapObject> getRdapObjects() {
		return rdapObjects;
	}
	
	public void setRdapObjects(List<? extends RdapObject> rdapObjects) {
		this.rdapObjects = rdapObjects;
	}
	
}
