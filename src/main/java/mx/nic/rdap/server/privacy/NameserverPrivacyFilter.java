package mx.nic.rdap.server.privacy;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.struct.NameserverIpAddressesStruct;
import mx.nic.rdap.server.util.PrivacyUtil;

public class NameserverPrivacyFilter {

	private NameserverPrivacyFilter() {
		// no code;
	}

	/**
	 * Hides information that is inaccessible to the current user/subject
	 * 
	 * @param ns
	 *            {@link Nameserver} to be filtered
	 * @return true if the result was filter, otherwise false
	 */
	public static boolean filterNameserver(Nameserver ns) {

		Subject subject = SecurityUtils.getSubject();
		UserInfo userInfo = new UserInfo(subject,
				PrivacyUtil.isSubjectOwner(subject.getPrincipal().toString(), ns));

		return filterNameserver(ns, userInfo);
	}
	
	public static boolean filterAnidatedNameserver(List<Nameserver> nameservers, UserInfo userInfo) {
		boolean isPrivate = false;
		
		if (ObjectPrivacyFilter.isValueEmpty(nameservers)) {
			return false;
		}

		for(Nameserver ns : nameservers) {
			isPrivate |= filterNameserver(ns, userInfo);
		}
		
		return isPrivate;
	}

	private static boolean filterNameserver(Nameserver ns, UserInfo userInfo) {
		boolean isPrivate = false;

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getNameserverPrivacySettings();

		for (String key : privacySettings.keySet()) {
			PrivacySetting setting = privacySettings.get(key);
			boolean isHidden = setting.isHidden(userInfo);
			switch (key) {
			case "handle":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getHandle())) {
					ns.setHandle(null);
					isPrivate = true;
				}
				break;
			case "ldhName":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getLdhName())) {
					ns.setLdhName(null);
					isPrivate = true;
				}
				break;
			case "unicodeName":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getUnicodeName())) {
					ns.setUnicodeName(null);
					isPrivate = true;
				}
				break;
			case "ipAddresses":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getIpAddresses())) {
					ns.setIpAddresses(null);
					isPrivate = true;
				} else {
					isPrivate |= filterIpAddress(ns.getIpAddresses(), userInfo);
				}
				break;
			case "entities":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getEntities())) {
					ns.setEntities(null);
					isPrivate = true;
				} else {
					isPrivate |= EntityPrivacyFilter.filterAnidatedEntities(ns.getEntities(), userInfo);
				}
				break;
			case "status":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getStatus())) {
					ns.setStatus(null);
					isPrivate = true;
				}
				break;
			case "remarks":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getRemarks())) {
					ns.setRemarks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterRemarks(ns.getRemarks(), userInfo,
							PrivacyUtil.getNameserverRemarkPrivacySettings(),
							PrivacyUtil.getNameserverLinkPrivacySettings());
				}
				break;
			case "links":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getLinks())) {
					ns.setLinks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterLinks(ns.getLinks(), userInfo,
							PrivacyUtil.getNameserverLinkPrivacySettings());
				}
				break;
			case "port43":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getPort43())) {
					ns.setPort43(null);
					isPrivate = true;
				}
				break;
			case "events":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getEvents())) {
					ns.setEvents(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterEvents(ns.getEvents(), userInfo,
							PrivacyUtil.getNameserverEventPrivacySettings(),
							PrivacyUtil.getNameserverLinkPrivacySettings());
				}
				break;

			case "lang":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ns.getLang())) {
					ns.setLang(null);
					isPrivate = true;
				}
				break;
			}
		}
		return isPrivate;
	}

	private static boolean filterIpAddress(NameserverIpAddressesStruct ips, UserInfo userInfo) {
		boolean isPrivate = false;
		if (ObjectPrivacyFilter.isValueEmpty(ips)) {
			return false;
		}
		
		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getNameserverPrivacySettings();

		boolean isHidden = privacySettings.get("v4").isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ips.getIpv4Adresses())) {
			isPrivate = true;
			ips.setIpv4Adresses(null);
		}

		isHidden = privacySettings.get("v6").isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ips.getIpv6Adresses())) {
			isPrivate = true;
			ips.setIpv6Adresses(null);
		}

		return isPrivate;
	}

}
