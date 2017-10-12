package mx.nic.rdap.server.privacy;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import mx.nic.rdap.core.db.Autnum;
import mx.nic.rdap.server.util.PrivacyUtil;

public class AutnumPrivacyFilter {

	private AutnumPrivacyFilter() {
		// no code;
	}

	/**
	 * Hides information that is inaccessible to the current user/subject
	 * 
	 * @param autnum
	 *            {@link Autnum} to be filtered
	 * @return true if the result was filter, otherwise false
	 */
	public static boolean filterAutnum(Autnum autnum) {

		Subject subject = SecurityUtils.getSubject();
		UserInfo userInfo = new UserInfo(subject,
				PrivacyUtil.isSubjectOwner(subject.getPrincipal().toString(), autnum));

		return filterAutnum(autnum, userInfo);
	}

	private static boolean filterAutnum(Autnum autnum, UserInfo userInfo) {
		boolean isPrivate = false;

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getAutnumPrivacySettings();

		for (String key : privacySettings.keySet()) {
			PrivacySetting setting = privacySettings.get(key);
			boolean isHidden = setting.isHidden(userInfo);
			switch (key) {
			case "handle":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getHandle())) {
					autnum.setHandle(null);
					isPrivate = true;
				}
				break;
			case "entities":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getEntities())) {
					autnum.setEntities(null);
					isPrivate = true;
				} else {
					isPrivate |= EntityPrivacyFilter.filterAnidatedEntities(autnum.getEntities(), userInfo);
				}
				break;
			case "status":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getStatus())) {
					autnum.setStatus(null);
					isPrivate = true;
				}
				break;
			case "remarks":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getRemarks())) {
					autnum.setRemarks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterRemarks(autnum.getRemarks(), userInfo,
							PrivacyUtil.getAutnumRemarkPrivacySettings(), PrivacyUtil.getAutnumLinkPrivacySettings());
				}
				break;
			case "links":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getLinks())) {
					autnum.setLinks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterLinks(autnum.getLinks(), userInfo,
							PrivacyUtil.getAutnumLinkPrivacySettings());
				}
				break;
			case "port43":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getPort43())) {
					autnum.setPort43(null);
					isPrivate = true;
				}
				break;
			case "events":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getEvents())) {
					autnum.setEvents(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterEvents(autnum.getEvents(), userInfo,
							PrivacyUtil.getAutnumEventPrivacySettings(), PrivacyUtil.getAutnumLinkPrivacySettings());
				}
				break;
			case "startAutnum":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getStartAutnum())) {
					autnum.setStartAutnum(null);
					isPrivate = true;
				}
				break;
			case "endAutnum":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getEndAutnum())) {
					autnum.setEndAutnum(null);
					isPrivate = true;
				}
				break;
			case "name":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getName())) {
					autnum.setName(null);
					isPrivate = true;
				}
				break;
			case "type":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getType())) {
					autnum.setType(null);
					isPrivate = true;
				}
				break;
			case "country":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getCountryCode())) {
					autnum.setCountry(null);
					isPrivate = true;
				}
				break;

			case "lang":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(autnum.getLang())) {
					autnum.setLang(null);
					isPrivate = true;
				}
				break;
			}
		}

		return isPrivate;
	}

	public static boolean filterAnidatedAutnums(List<Autnum> autnums, UserInfo userInfo) {
		boolean isPrivate = false;

		if (ObjectPrivacyFilter.isValueEmpty(autnums)) {
			return false;
		}
		
		for(Autnum a : autnums) {
			isPrivate |= filterAutnum(a, userInfo);
		}

		return isPrivate;
	}

}
