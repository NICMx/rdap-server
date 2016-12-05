package mx.nic.rdap.server;

import mx.nic.rdap.core.catalog.Rol;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.RdapObject;

public abstract class UserRequestInfo {

	private String userName;

	public boolean isUserAuthenticated() {
		return userName != null;
	}

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

	private boolean isEntityOwner(Entity ent) {
		if (!ent.getHandle().equals(userName)) {
			return false;
		}

		if (ent.getRoles() == null || ent.getRoles().isEmpty()) {
			return false;
		}

		for (Rol rol : ent.getRoles()) {
			if (RdapConfiguration.isRolAnOwner(rol)) {
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
