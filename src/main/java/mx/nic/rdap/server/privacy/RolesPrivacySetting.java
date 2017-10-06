package mx.nic.rdap.server.privacy;

import java.util.Set;

public class RolesPrivacySetting extends OwnerPrivacySetting {

	private boolean isOwnerSet;

	private Set<String> allowedRoles;

	public RolesPrivacySetting(boolean isOwnerSet, Set<String> allowedRoles) {
		this.isOwnerSet = isOwnerSet;
		this.allowedRoles = allowedRoles;
	}

	@Override
	public boolean isHidden(Object userInfo) {
		boolean result = false;
		if (isOwnerSet) {
			result = super.isHidden(userInfo);
		}

		if (result) {
			return result;
		}

		for (String role : allowedRoles) {

		}
		// TODO Auto-generated method stub
		return result;
	}

}
