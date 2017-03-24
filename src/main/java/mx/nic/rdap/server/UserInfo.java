package mx.nic.rdap.server;

import mx.nic.rdap.core.catalog.Role;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.RdapObject;

/**
 * Information about the user that make a request.
 */
public class UserInfo {

	private String userName;

	public UserInfo(String username) {
		this.userName = username;
	}

	public boolean isUserAuthenticated() {
		return userName != null;
	}

	/**
	 * @return <code>true</code> if this user is the owner of the RdapObject
	 */
	public boolean isOwner(RdapObject object) {
		if (!isUserAuthenticated())
			return false;

		if (object instanceof Entity) {
			Entity ent = (Entity) object;
			return ent.getHandle().equals(userName);
		}

		for (Entity ent : object.getEntities()) {
			if (isEntityOwner(ent)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return <code>true</code> if this user is the owner of the Entity Object
	 */
	private boolean isEntityOwner(Entity ent) {
		if (!ent.getHandle().equals(userName)) {
			return false;
		}

		if (ent.getRoles() == null || ent.getRoles().isEmpty()) {
			return false;
		}

		for (Role role : ent.getRoles()) {
			if (RdapConfiguration.isRolAnOwner(role)) {
				return true;
			}
		}

		return false;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
