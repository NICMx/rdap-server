package mx.nic.rdap.server.privacy;

import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.server.util.PrivacyUtil;

public class EntityPrivacyFilter {

	private EntityPrivacyFilter() {
		// no code;
	}

	/**
	 * Hides information that is inaccessible to the current user/subject
	 * 
	 * @param entity
	 *            {@link Entity} to be filtered
	 * @return true if the result was filter, otherwise false
	 */
	public static boolean filterEntity(Entity entity) {
		boolean isPrivate = false;

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getEntityPrivacySettings();
		Subject subject = SecurityUtils.getSubject();
		UserInfo userInfo = new UserInfo(subject,
				PrivacyUtil.isSubjectOwner(subject.getPrincipal().toString(), entity));

		for (String key : privacySettings.keySet()) {
			PrivacySetting setting = privacySettings.get(key);
			boolean isHidden = setting.isHidden(userInfo);
			switch (key) {
			case "handle":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getHandle())) {
					entity.setHandle(null);
					isPrivate = true;
				}
				break;
			case "vcardArray":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getVCardList())) {
					entity.setvCardList(null);
					isPrivate = true;
				} else {
					// TODO
				}
				break;
			case "roles":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getRoles())) {
					entity.setRoles(null);
					isPrivate = true;
				}
			case "publicIds":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getPublicIds())) {
					entity.setPublicIds(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterPublicId(entity.getPublicIds(), userInfo,
							PrivacyUtil.getEntityPublicIdsPrivacySettings());
				}
				break;
			case "entities":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getEntities())) {
					entity.setEntities(null);
					isPrivate = true;
				} else {
					// TODO
				}
				break;
			case "remarks":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getRemarks())) {
					entity.setRemarks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterRemarks(entity.getRemarks(), userInfo,
							PrivacyUtil.getEntityRemarkPrivacySettings(), PrivacyUtil.getEntityLinkPrivacySettings());
				}
				break;
			case "links":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getLinks())) {
					entity.setLinks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterLinks(entity.getLinks(), userInfo,
							PrivacyUtil.getEntityLinkPrivacySettings());
				}
				break;
			case "events":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getEvents())) {
					entity.setEvents(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterEvents(entity.getEvents(), userInfo,
							PrivacyUtil.getEntityEventPrivacySettings(), PrivacyUtil.getEntityLinkPrivacySettings());
				}
				break;
			// case "asEventActor":
			// TODO entity doesn't have getAsEventActor;
			// if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.ge))
			// break;
			case "status":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getStatus())) {
					entity.setStatus(null);
					isPrivate = true;
				}
				break;
			case "port43":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getPort43())) {
					entity.setPort43(null);
					isPrivate = true;
				}
				break;
			case "networks":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getIpNetworks())) {
					entity.setIpNetworks(null);
					isPrivate = true;
				} else {
					// TODO
				}
				break;
			case "autnums":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getAutnums())) {
					entity.setAutnums(null);
					isPrivate = true;
				} else {
					// TODO isPrivate |= ;
				}
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
