package mx.nic.rdap.server.result;

import java.util.ArrayList;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.renderer.object.RequestResponse;

/**
 * A result from an Entity request
 */
public class EntityResult extends RdapSingleResult {

	public EntityResult(String header, String contextPath, Entity entity, String userName) {
		setRdapObject(entity);
		
		addSelfLinks(header, contextPath, entity);
		validateResponse();
		
		setResultType(ResultType.ENTITY);
		RequestResponse<Entity> entityResponse = new RequestResponse<>();
		entityResponse.setNotices(notices);
		entityResponse.setRdapConformance(new ArrayList<>());
		entityResponse.getRdapConformance().add("rdap_level_0");
		entityResponse.setRdapObject(entity);
		
		setRdapResponse(entityResponse);
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
	 * Generates a link with the self information and add it to the domain and
	 * its attributes
	 */
	public static void addSelfLinks(String header, String contextPath, Entity entity) {
		Link self = new Link(header, contextPath, "entity", entity.getHandle());
		entity.getLinks().add(self);

		for (Entity ent : entity.getEntities()) {
			self = new Link(header, contextPath, "entity", ent.getHandle());
			ent.getLinks().add(self);
		}

		for (IpNetwork ip : entity.getIpNetworks()) {
			self = new Link(header, contextPath, "ip", ip.getStartAddress().getHostAddress() + "/" + ip.getPrefix());
			ip.getLinks().add(self);
		}
	}

	
}
