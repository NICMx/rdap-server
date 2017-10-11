package mx.nic.rdap.server.privacy;

import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.server.util.PrivacyUtil;

public class ObjectPrivacyFilter {

	private ObjectPrivacyFilter() {
		// Empty
	}

	/**
	 * Hides information that is inaccessible to the current user/subject
	 * 
	 * @param domain
	 *            {@link Domain} to be filtered
	 */
	public static void filterDomain(Domain domain) {
		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getDomainPrivacySettings();
		Subject subject = SecurityUtils.getSubject();
		UserInfo userInfo = new UserInfo(subject, PrivacyUtil.isSubjectOwner(subject.getPrincipal().toString(), domain));

		for (String key : privacySettings.keySet()) {
			PrivacySetting setting = privacySettings.get(key);
			boolean isHidden = setting.isHidden(userInfo);
			switch (key) {
				case "handle":
					if (isHidden) {
						domain.setHandle(null);
					}
					break;
				case "ldhName":
					if (isHidden) {
						domain.setLdhName(null);
					}
					break;
				case "unicodeName":
					if (isHidden) {
						domain.setUnicodeName(null);
					}
					break;
				case "variants":
					if (isHidden) {
						domain.setVariants(null);
					}
					break;
				case "nameservers":
					if (isHidden) {
						domain.setNameServers(null);
					}
					break;
				case "secureDNS":
					if (isHidden) {
						domain.setSecureDNS(null);
					}
					break;
				case "entities":
					if (isHidden) {
						domain.setEntities(null);
					}
					break;
				case "status":
					if (isHidden) {
						domain.setStatus(null);
					}
					break;
				case "publicIds":
					if (isHidden) {
						domain.setPublicIds(null);
					}
					break;
				case "remarks":
					if (isHidden) {
						domain.setRemarks(null);
					}
					break;
				case "links":
					if (isHidden) {
						domain.setLinks(null);
					}
					break;
				case "port43":
					if (isHidden) {
						domain.setPort43(null);
					}
					break;
				case "events":
					if (isHidden) {
						domain.setEvents(null);
					}
					break;
				case "network":
					if (isHidden) {
						domain.setIpNetwork(null);
					}
					break;
				case "lang":
					if (isHidden) {
						// FIXME where do I get this?
						//domain.setLang(null);
					}
					break;
			}
		}
	}
	
}
