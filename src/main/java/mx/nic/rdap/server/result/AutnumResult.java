/**
 * 
 */
package mx.nic.rdap.server.result;


import java.util.ArrayList;

import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.renderer.object.RequestResponse;

public class AutnumResult extends RdapSingleResult {

	
	public AutnumResult(String header, String contextPath, Autnum autnum, String username) {
		setRdapObject(autnum);
		this.userInfo = new UserInfo(username);
		addSelfLinks(header, contextPath, autnum);
		
		validateResponse();
		
		setResultType(ResultType.AUTNUM);
		
		RequestResponse<Autnum> autnumResponse = new RequestResponse<>();
		autnumResponse.setNotices(notices);
		autnumResponse.setRdapConformance(new ArrayList<>());
		autnumResponse.getRdapConformance().add("rdap_level_0");
		autnumResponse.setRdapObject(autnum);
		
		setRdapResponse(autnumResponse);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#fillNotices()
	 */
	@Override
	public void fillNotices() {
		// At the moment, there is no notices for this request
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#validateResponse()
	 */
	@Override
	public void validateResponse() {
		// Nothing to validate
	}

	/**
	 * Generates a link with the self information and add it to the domain
	 */
	private static void addSelfLinks(String header, String contextPath, Autnum autnum) {
		Link self = new Link(header, contextPath, "autnum", autnum.getStartAutnum().toString());
		autnum.getLinks().add(self);

		for (Entity ent : autnum.getEntities()) {
			self = new Link(header, contextPath, "entity", ent.getHandle());
			ent.getLinks().add(self);
		}
	}

	
}
