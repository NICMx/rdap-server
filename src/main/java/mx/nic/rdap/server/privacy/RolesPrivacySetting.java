package mx.nic.rdap.server.privacy;

import java.util.Set;

import org.apache.shiro.subject.Subject;

/**
 * Privacy setting for configuration values related to roles, it can be mixed with "owner" role
 */
public class RolesPrivacySetting extends OwnerPrivacySetting {

	/**
	 * Indicates if the "owner" role is mixed with the custom roles
	 */
	private boolean isOwnerSet;

	/**
	 * Set of valid roles
	 */
	private Set<String> allowedRoles;

	public RolesPrivacySetting(boolean isOwnerSet, Set<String> allowedRoles) {
		this.isOwnerSet = isOwnerSet;
		this.allowedRoles = allowedRoles;
	}

	@Override
	public boolean isHidden(UserInfo userInfo) {
		// Must be logged in
		Subject subject = userInfo.getSubject();
		if (subject == null || !subject.isAuthenticated()) {
			return true;
		}

		// If it's the owner
		boolean result = true;
		if (isOwnerSet) {
			result = super.isHidden(userInfo);
		}

		if (!result) {
			return result;
		}

		// If it had at least one role then isn't hidden
		for (String role : allowedRoles) {
			if (subject.hasRole(role)) {
				result = false;
			}
		}

		return result;
	}

}