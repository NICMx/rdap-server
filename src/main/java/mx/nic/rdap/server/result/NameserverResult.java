package mx.nic.rdap.server.result;

import java.util.ArrayList;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.renderer.object.RequestResponse;

/**
 * A result from a Nameserver request
 */
public class NameserverResult extends RdapSingleResult {

	public NameserverResult(String header, String contextPath, Nameserver nameserver, String userName, int nsCount) {
		setRdapObject(nameserver);

		addSelfLinks(header, contextPath, nameserver);

		setResultType(ResultType.NAMESERVER);
		RequestResponse<Nameserver> nameserverResponse = new RequestResponse<>();
		nameserverResponse.setNotices(notices);
		nameserverResponse.setRdapConformance(new ArrayList<>());
		nameserverResponse.getRdapConformance().add("rdap_level_0");
		nameserverResponse.setRdapObject(nameserver);

		if (nsCount > 0) {
			addSearchOtherNSLink(header, contextPath, nameserver);
		}

		setRdapResponse(nameserverResponse);
	}

	public void addSearchOtherNSLink(String header, String contextPath, Nameserver nameserver) {
		Link nsSearchLink = new Link();

		nsSearchLink.setValue(header + contextPath + "/nameserver/" + nameserver.getLdhName());
		nsSearchLink.setRel("collection");
		nsSearchLink.setType("application/rdap+json");
		nsSearchLink.setHref(header + contextPath + "/nameservers?name=" + nameserver.getLdhName());

		nameserver.getLinks().add(nsSearchLink);
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
	 * 
	 * @param nameserver
	 */
	public static void addSelfLinks(String header, String contextPath, Nameserver nameserver) {
		Link self = new Link(header, contextPath, "nameserver", nameserver.getLdhName());
		nameserver.getLinks().add(self);

		for (Entity ent : nameserver.getEntities()) {
			self = new Link(header, contextPath, "entity", ent.getHandle());
			ent.getLinks().add(self);
		}
	}

}
