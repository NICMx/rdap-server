package mx.nic.rdap.server;

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
			return isEntityOwner((Entity) object);
		}

		for (Entity ent : object.getEntities()) {
			if (ent.getHandle().equals(userName)) {

			}
		}

		return false;
	}

	private boolean isEntityOwner(Entity ent) {
		boolean result = true;
		if (!ent.getHandle().equals(userName)) {
			return false;
		}

		// TODO: this code will work when we filter by owner and its rol.
		// if (ent.getRoles() == null || ent.getRoles().isEmpty()) {
		// return false;
		// }
		// for(Rol rol : ent.getRoles()) {
		// if (Util.getOwnerRoles().contains(rol)) {
		// result = true;
		// break;
		// }
		// }

		return result;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
