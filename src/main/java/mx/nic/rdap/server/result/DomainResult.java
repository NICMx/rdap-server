package mx.nic.rdap.server.result;

import java.util.ArrayList;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.renderer.object.RequestResponse;

/**
 * A result from a Domain request
 */
public class DomainResult extends RdapSingleResult {

	public DomainResult(String header, String contextPath, Domain domain, String userName) {
		setRdapObject(domain);
		
		addSelfLinks(header, contextPath, domain);
		validateResponse();
		
		setResultType(ResultType.DOMAIN);
		RequestResponse<Domain> domainResponse = new RequestResponse<>();
		
		domainResponse.setNotices(notices);
		domainResponse.setRdapConformance(new ArrayList<>());
		domainResponse.getRdapConformance().add("rdap_level_0");
		domainResponse.setRdapObject(domain);
		
		setRdapResponse(domainResponse);
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
	public static void addSelfLinks(String header, String contextPath, Domain domain) {
		String domainName = domain.getFQDN() != null ? domain.getFQDN() : domain.getUnicodeFQDN();

		Link self = new Link(header, contextPath, "domain", domainName);
		domain.getLinks().add(self);

		for (Nameserver ns : domain.getNameServers()) {
			String nsName = ns.getFqdnLdhName() != null ? ns.getFqdnLdhName()
					: ns.getFqdnUnicodeName();
			
			self = new Link(header, contextPath, "nameserver", nsName);
			ns.getLinks().add(self);
		}

		for (Entity ent : domain.getEntities()) {
			self = new Link(header, contextPath, "entity", ent.getHandle());
			ent.getLinks().add(self);
		}

		if (domain.getIpNetwork() != null) {
			self = new Link(header, contextPath, "ip", domain.getIpNetwork().getStartAddress().getHostAddress());
			domain.getIpNetwork().getLinks().add(self);
		}
	}
	
}
