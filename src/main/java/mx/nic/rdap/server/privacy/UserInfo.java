package mx.nic.rdap.server.privacy;

import org.apache.shiro.subject.Subject;

/**
 * Information about the user that makes a request, useful for privacy settings.
 */
public class UserInfo {

	private Subject subject;

	private boolean isObjectOwner;

	public UserInfo(Subject subject, boolean isObjectOwner) {
		this.subject = subject;
		this.isObjectOwner = isObjectOwner;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public boolean isObjectOwner() {
		return isObjectOwner;
	}

	public void setIsObjectOwner(boolean isObjectOwner) {
		this.isObjectOwner = isObjectOwner;
	}

}