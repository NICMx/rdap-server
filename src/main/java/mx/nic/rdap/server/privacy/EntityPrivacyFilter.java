package mx.nic.rdap.server.privacy;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
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
		Subject subject = SecurityUtils.getSubject();
		UserInfo userInfo = new UserInfo(subject,
				PrivacyUtil.isSubjectOwner(subject.getPrincipal().toString(), entity));

		return filterEntity(entity, userInfo);
	}

	public static boolean filterEntity(Entity entity, UserInfo userInfo) {
		boolean isPrivate = false;

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getEntityPrivacySettings();

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
					if (!entity.getVCardList().isEmpty()) {
						isPrivate |= filterVcard(entity.getVCardList().get(0), userInfo);
					}
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
					isPrivate |= filterAnidatedEntities(entity.getEntities(), userInfo);
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
					isPrivate |= IpNetworkPrivacyFilter.filterIpNetworks(entity.getIpNetworks(), userInfo);
				}
				break;
			case "autnums":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getAutnums())) {
					entity.setAutnums(null);
					isPrivate = true;
				} else {
					isPrivate |= AutnumPrivacyFilter.filterAnidatedAutnums(entity.getAutnums(), userInfo);
				}
			case "lang":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getLang())) {
					entity.setLang(null);
					isPrivate = true;
				}
				break;
			}
		}
		return isPrivate;
	}

	public static boolean filterAnidatedEntities(List<Entity> entities, UserInfo userInfo) {
		boolean isPrivate = false;

		if (ObjectPrivacyFilter.isValueEmpty(entities)) {
			return false;
		}

		for (Entity e : entities) {
			isPrivate |= filterEntity(e, userInfo);
		}

		return isPrivate;
	}

	private static boolean filterVcard(VCard vcard, UserInfo userInfo) {
		boolean isPrivate = false;

		if (ObjectPrivacyFilter.isValueEmpty(vcard)) {
			return false;
		}

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getVCardPrivacySettings();

		String key = "name";
		boolean isHidden = privacySettings.get(key).isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getName())) {
			isPrivate = true;
			vcard.setName(null);
		}

		key = "companyName";
		isHidden = privacySettings.get(key).isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getCompanyName())) {
			isPrivate = true;
			vcard.setCompanyName(null);
		}

		key = "companyUrl";
		isHidden = privacySettings.get(key).isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getCompanyURL())) {
			isPrivate = true;
			vcard.setCompanyURL(null);
		}

		key = "mail";
		isHidden = privacySettings.get(key).isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getEmail())) {
			isPrivate = true;
			vcard.setEmail(null);
		}

		key = "voice";
		isHidden = privacySettings.get(key).isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getVoice())) {
			isPrivate = true;
			vcard.setVoice(null);
		}

		key = "cellphone";
		isHidden = privacySettings.get(key).isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getCellphone())) {
			isPrivate = true;
			vcard.setCellphone(null);
		}

		key = "fax";
		isHidden = privacySettings.get(key).isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getFax())) {
			isPrivate = true;
			vcard.setFax(null);
		}

		key = "jobTitle";
		isHidden = privacySettings.get(key).isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getJobTitle())) {
			isPrivate = true;
			vcard.setJobTitle(null);
		}

		key = "postalInfo";
		isHidden = privacySettings.get(key).isHidden(userInfo);
		if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getPostalInfo())) {
			isPrivate = true;
			List<VCardPostalInfo> postalInfoNull = null;
			vcard.setPostalInfo(postalInfoNull);
		} else {
			isPrivate |= filterPostalInfo(vcard.getPostalInfo(), userInfo);
		}

		return isPrivate;
	}

	private static boolean filterPostalInfo(List<VCardPostalInfo> postalInfos, UserInfo userInfo) {
		boolean isPrivate = false;

		if (ObjectPrivacyFilter.isValueEmpty(postalInfos)) {
			return false;
		}

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getVCardPrivacySettings();
		String key;
		boolean isHidden;
		for (VCardPostalInfo postalInfo : postalInfos) {

			key = "type";
			isHidden = privacySettings.get(key).isHidden(userInfo);
			if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getType())) {
				isPrivate = true;
				postalInfo.setType(null);
			}

			key = "street1";
			isHidden = privacySettings.get(key).isHidden(userInfo);
			if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getStreet1())) {
				isPrivate = true;
				postalInfo.setStreet1(null);
			}

			key = "street2";
			isHidden = privacySettings.get(key).isHidden(userInfo);
			if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getStreet2())) {
				isPrivate = true;
				postalInfo.setStreet2(null);
			}

			key = "street3";
			isHidden = privacySettings.get(key).isHidden(userInfo);
			if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getStreet3())) {
				isPrivate = true;
				postalInfo.setStreet3(null);
			}

			key = "postalCode";
			isHidden = privacySettings.get(key).isHidden(userInfo);
			if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getPostalCode())) {
				isPrivate = true;
				postalInfo.setPostalCode(null);
			}

			key = "city";
			isHidden = privacySettings.get(key).isHidden(userInfo);
			if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getCity())) {
				isPrivate = true;
				postalInfo.setCity(null);
			}

			key = "state";
			isHidden = privacySettings.get(key).isHidden(userInfo);
			if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getState())) {
				isPrivate = true;
				postalInfo.setState(null);
			}

			key = "country";
			isHidden = privacySettings.get(key).isHidden(userInfo);
			if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getCountry())) {
				isPrivate = true;
				postalInfo.setCountry(null);
			}

		}

		return isPrivate;
	}

}
