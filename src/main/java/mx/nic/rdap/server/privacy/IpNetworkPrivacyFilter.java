package mx.nic.rdap.server.privacy;

import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import mx.nic.rdap.core.db.IpNetwork;
import mx.nic.rdap.server.util.PrivacyUtil;

public class IpNetworkPrivacyFilter {

	private IpNetworkPrivacyFilter() {
		// no code;
	}

	/**
	 * Hides information that is inaccessible to the current user/subject
	 * 
	 * @param ip
	 *            {@link IpNetwork} to be filtered
	 * @return true if the result was filter, otherwise false
	 */
	public static boolean filterAutnum(IpNetwork ip) {
		boolean isPrivate = false;

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getIpNetworkPrivacySettings();
		Subject subject = SecurityUtils.getSubject();
		UserInfo userInfo = new UserInfo(subject,
				PrivacyUtil.isSubjectOwner(subject.getPrincipal().toString(), ip));

		for (String key : privacySettings.keySet()) {
			PrivacySetting setting = privacySettings.get(key);
			boolean isHidden = setting.isHidden(userInfo);
			switch (key) {
			case "handle":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getHandle())) {
					ip.setHandle(null);
					isPrivate = true;
				}
				break;
			case "startAddress":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getStartAddress())) {
					ip.setStartAddress(null);
					isPrivate = true;
				}
				break;
			case "endAddress":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getEndAddress())) {
					ip.setEndAddress(null);
					isPrivate = true;
				}
				break;
			case "ipVersion":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getIpVersion())) {
					ip.setIpVersion(null);
					isPrivate = true;
				}
				break;
			case "entities":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getEntities())) {
					ip.setEntities(null);
					isPrivate = true;
				} else {
					// TODO
				}
				break;
			case "status":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getStatus())) {
					ip.setStatus(null);
					isPrivate = true;
				}
				break;
			case "remarks":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getRemarks())) {
					ip.setRemarks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterRemarks(ip.getRemarks(), userInfo,
							PrivacyUtil.getIpNetworkRemarkPrivacySettings(), PrivacyUtil.getIpNetworkLinkPrivacySettings());
				}
				break;
			case "links":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getLinks())) {
					ip.setLinks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterLinks(ip.getLinks(), userInfo,
							PrivacyUtil.getIpNetworkLinkPrivacySettings());
				}
				break;
			case "port43":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getPort43())) {
					ip.setPort43(null);
					isPrivate = true;
				}
				break;
			case "events":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getEvents())) {
					ip.setEvents(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterEvents(ip.getEvents(), userInfo,
							PrivacyUtil.getIpNetworkEventPrivacySettings(), PrivacyUtil.getIpNetworkLinkPrivacySettings());
				}
				break;
			case "name":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getName())) {
					ip.setName(null);
					isPrivate = true;
				}
				break;
			case "type":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getType())) {
					ip.setType(null);
					isPrivate = true;
				}
				break;
			case "country":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getCountry())) {
					ip.setCountry(null);
					isPrivate = true;
				}
				break;
			case "parentHandle":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ip.getParentHandle())) {
					ip.setParentHandle(null);
					isPrivate = true;
				}
				break;

			case "lang":
				if (isHidden) {
					// FIXME where do I get this?
					// domain.setLang(null);
				}
				break;
			}
		}
		return isPrivate;
	}

}
