package mx.nic.rdap.server.result;

import java.util.ArrayList;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.server.renderer.json.EntityJsonWriter;

/**
 * A result from an Entity request
 */
public class EntityResult extends RdapResult {

	private Entity entity;

	public EntityResult(String header, String contextPath, Entity entity, String userName) {
		notices = new ArrayList<Remark>();
		this.entity = entity;
		this.userInfo = new UserInfo(userName);
		addSelfLinks(header, contextPath, entity);
		validateResponse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return EntityJsonWriter.getJson(entity, userInfo.isUserAuthenticated(), userInfo.isOwner(entity));
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
