package mx.nic.rdap.server.privacy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import mx.nic.rdap.core.catalog.Role;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Remark;
import mx.nic.rdap.core.db.RemarkDescription;
import mx.nic.rdap.core.db.UserConsent;
import mx.nic.rdap.core.db.UserConsentGlobal;
import mx.nic.rdap.core.db.UserConsentByAttribute;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.server.util.PrivacyUtil;
import mx.nic.rdap.server.util.Util;

public class EntityPrivacyFilter {

	private EntityPrivacyFilter() {
		// no code;
	}

	/**
	 * Hides information that is inaccessible to the current user/subject
	 * 
	 * @param entity {@link Entity} to be filtered
	 * @return true if the result was filter, otherwise false
	 */
	public static boolean filterEntity(Entity entity) {
		Subject subject = SecurityUtils.getSubject();
		UserInfo userInfo = new UserInfo(subject, PrivacyUtil.isSubjectOwner(Util.getUsername(subject), entity));

		return filterEntity(entity, userInfo);
	}

	private static boolean filterEntity(Entity entity, UserInfo userInfo) {
		List<Role> entityRoles = entity.getRoles();
		if (entityRoles == null || entityRoles.isEmpty()) {
			return filterEntity(entity, userInfo, PrivacyUtil.getEntityPrivacySettings());
		}

		boolean result = false;
		boolean isFilterByRole = false;
		for (Role role : entityRoles) {
			Map<String, PrivacySetting> privacySettings = PrivacyUtil.getEntityPrivacySettings(role);
			if (privacySettings == null || privacySettings.isEmpty()) {
				continue;
			}

			result |= filterEntity(entity, userInfo, privacySettings);
			isFilterByRole = true;
		}

		if (!isFilterByRole) {
			result = filterEntity(entity, userInfo, PrivacyUtil.getEntityPrivacySettings());
		}

		return result;
	}

	private static boolean filterEntity(Entity entity, UserInfo userInfo, Map<String, PrivacySetting> privacySettings) {
		boolean isPrivate = false;

		List<Role> vCardRoles = entity.getRoles();
		if (vCardRoles == null) {
			vCardRoles = Collections.emptyList();
		}

		UserConsent consent = entity.getConsent();

		for (String key : privacySettings.keySet()) {
			PrivacySetting setting = privacySettings.get(key);
			boolean isHidden = setting.isHidden(userInfo);
			switch (key) {
			case "handle":
				if (consent != null && consent.isHandleConsent()) {
					break;
				}

				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getHandle())) {
					entity.setHandle(null);
					isPrivate = true;
				}
				break;
			case "vcardArray":
				if (consent != null) {
					if (consent instanceof UserConsentGlobal) {
						if (((UserConsentGlobal) consent).isGlobalConsent()) {
							break;
						} else {
							if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getVCardList())) {
								entity.setvCardList(null);
								isPrivate = true;
							} else {
								List<VCard> vCardList = entity.getVCardList();
								if (vCardList != null && !vCardList.isEmpty()) {
									isPrivate |= filterVcard(entity.getVCardList().get(0), userInfo, vCardRoles, null);
								}
							}
						}
					} else if (consent instanceof UserConsentByAttribute) {
						UserConsentByAttribute consentByAttr = (UserConsentByAttribute) consent;
						if (consentByAttr.isAllTrue()) {
							break;
						}
						if (consentByAttr.isAllFalse() && isHidden
								&& !ObjectPrivacyFilter.isValueEmpty(entity.getVCardList())) {
							entity.setvCardList(null);
							isPrivate = true;
						} else {
							List<VCard> vCardList = entity.getVCardList();
							if (vCardList != null && !vCardList.isEmpty()) {
								isPrivate |= filterVcard(entity.getVCardList().get(0), userInfo, vCardRoles,
										consentByAttr);
							}
						}

					}
				} else {
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getVCardList())) {
						entity.setvCardList(null);
						isPrivate = true;
					} else {
						List<VCard> vCardList = entity.getVCardList();
						if (vCardList != null && !vCardList.isEmpty()) {
							isPrivate |= filterVcard(entity.getVCardList().get(0), userInfo, vCardRoles, null);
						}
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
							PrivacyUtil.getEntityRemarkPrivacySettings(),
							PrivacyUtil.getEntityRemarksLinksPrivacySettings());
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
							PrivacyUtil.getEntityEventPrivacySettings(),
							PrivacyUtil.getEntityEventsLinksPrivacySettings());
				}
				break;
			// case "asEventActor":
			// XXX entity doesn't have getAsEventActor;
			// if (isHidden && !ObjectPrivacyFilter.isValueEmpty(entity.getAsEventActor()))
			// {}
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
			boolean isRedacted = filterEntity(e, userInfo);
			addRedactedForPrivacy(isRedacted, e);
			isPrivate |= isRedacted;
		}

		return isPrivate;
	}

	/*
	 * Rdap response profile feb-19 2.7.4.3
	 * https://www.icann.org/en/system/files/files/rdap-response-profile-15feb19-en.
	 * pdf
	 */
	private static void addRedactedForPrivacy(boolean isRedacted, Entity e) {
		if (!isRedacted)
			return;

		if (e.getRemarks() == null)
			e.setRemarks(new ArrayList<>());

		Remark r = new Remark();
		r.setTitle("REDACTED FOR PRIVACY");
		r.setType("object redacted due to authorization.");

		RemarkDescription rd = new RemarkDescription();
		rd.setDescription("Some of the data in this object has been removed.");
		rd.setOrder(1);
		rd.setRemarkId(1L);

		r.getDescriptions().add(rd);

		e.getRemarks().add(r);
	}

	private static boolean filterVcard(VCard vcard, UserInfo userInfo, List<Role> entityRoles,
			UserConsentByAttribute userConsent) {
		if (entityRoles == null || entityRoles.isEmpty()) {
			return filterVcard(vcard, userInfo, PrivacyUtil.getVCardPrivacySettings(), userConsent);
		}

		boolean result = false;
		boolean isFilterByRole = false;
		for (Role role : entityRoles) {
			Map<String, PrivacySetting> privacySettings = PrivacyUtil.getVCardPrivacySettings(role);
			if (privacySettings == null || privacySettings.isEmpty()) {
				continue;
			}

			result |= filterVcard(vcard, userInfo, privacySettings, userConsent);
			isFilterByRole = true;
		}

		if (!isFilterByRole) {
			result = filterVcard(vcard, userInfo, PrivacyUtil.getVCardPrivacySettings(), userConsent);
		}

		return result;
	}

	private static boolean filterVcard(VCard vcard, UserInfo userInfo, Map<String, PrivacySetting> privacySettings,
			UserConsentByAttribute userConsent) {
		boolean isPrivate = false;
		boolean isHidden;
		boolean hasUserConsent = userConsent != null;
		PrivacySetting privacySetting;

		if (ObjectPrivacyFilter.isValueEmpty(vcard)) {
			return false;
		}

		for (String key : privacySettings.keySet()) {
			switch (key) {
			case "name":
				if (hasUserConsent && userConsent.isName())
					break;
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getName())) {
					isPrivate = true;
					if (privacySetting instanceof ObscuredPrivacy) {
						String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
						vcard.setName(textToShow);
					} else {
						vcard.setName(null);
					}
				}
				break;

			case "companyName":
				if (hasUserConsent && userConsent.isCompanyName())
					break;
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getCompanyName())) {
					isPrivate = true;
					if (privacySetting instanceof ObscuredPrivacy) {
						String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
						vcard.setCompanyName(textToShow);
					} else {
						vcard.setCompanyName(null);
					}
				}

				break;
			case "companyUrl":
				if (hasUserConsent && userConsent.isCompanyURL())
					break;
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getCompanyURL())) {
					isPrivate = true;
					if (privacySetting instanceof ObscuredPrivacy) {
						String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
						vcard.setCompanyURL(textToShow);
					} else {
						vcard.setCompanyURL(null);
					}
				}

				break;
			case "mail":
				if (hasUserConsent && userConsent.isEmail())
					break;
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getEmail())) {
					isPrivate = true;
					if (privacySetting instanceof ObscuredPrivacy) {
						String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
						vcard.setEmail(textToShow);
					} else {
						vcard.setEmail(null);
					}
				}

				break;
			case "voice":
				if (hasUserConsent && userConsent.isVoice())
					break;
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getVoice())) {
					isPrivate = true;
					if (privacySetting instanceof ObscuredPrivacy) {
						String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
						vcard.setVoice(textToShow);
					} else {
						vcard.setVoice(null);
					}
				}

				break;
			case "cellphone":
				if (hasUserConsent && userConsent.isCellphone())
					break;
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getCellphone())) {
					isPrivate = true;
					if (privacySetting instanceof ObscuredPrivacy) {
						String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
						vcard.setCellphone(textToShow);
					} else {
						vcard.setCellphone(null);
					}
				}

				break;
			case "fax":
				if (hasUserConsent && userConsent.isFax())
					break;
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getFax())) {
					isPrivate = true;
					if (privacySetting instanceof ObscuredPrivacy) {
						String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
						vcard.setFax(textToShow);
					} else {
						vcard.setFax(null);
					}
				}

				break;
			case "jobTitle":
				if (hasUserConsent && userConsent.isJobTitle())
					break;
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getJobTitle())) {
					isPrivate = true;
					if (privacySetting instanceof ObscuredPrivacy) {
						String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
						vcard.setJobTitle(textToShow);
					} else {
						vcard.setJobTitle(null);
					}
				}

				break;
			case "postalInfo":
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				
				boolean processPostalInfo = hasUserConsent && (userConsent.isType() || userConsent.isCountry()
						|| userConsent.isCountryCode() || userConsent.isPostalCode() || userConsent.isState()
						|| userConsent.isCity() || userConsent.isStreet1() || userConsent.isStreet2()
						|| userConsent.isStreet3());
				
				if (!processPostalInfo && isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getPostalInfo())) {
					isPrivate = true;
					List<VCardPostalInfo> postalInfoNull = null;
					vcard.setPostalInfo(postalInfoNull);
				} else {
					isPrivate |= filterPostalInfo(vcard.getPostalInfo(), userInfo, privacySettings, userConsent);
				}

				break;
			case "contactUri":
				if (hasUserConsent && userConsent.isContactUri())
					break;
				privacySetting = privacySettings.get(key);
				isHidden = privacySetting.isHidden(userInfo);
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(vcard.getContactUri())) {
					isPrivate = true;
					vcard.setContactUri(null);
				}
			default:
				break;
			}
		}
		return isPrivate;
	}

	private static boolean filterPostalInfo(List<VCardPostalInfo> postalInfos, UserInfo userInfo,
			Map<String, PrivacySetting> privacySettings, UserConsentByAttribute userConsent) {
		boolean isPrivate = false;
		boolean hasUserConsent = userConsent != null;

		if (ObjectPrivacyFilter.isValueEmpty(postalInfos)) {
			return false;
		}

		boolean isHidden;
		PrivacySetting privacySetting;
		for (VCardPostalInfo postalInfo : postalInfos) {
			for (String key : privacySettings.keySet()) {
				switch (key) {

				case "type":
					if (hasUserConsent && userConsent.isType()) {
						break;
					}
					privacySetting = privacySettings.get(key);
					isHidden = privacySetting.isHidden(userInfo);
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getType())) {
						isPrivate = true;
						if (privacySetting instanceof ObscuredPrivacy) {
							String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
							postalInfo.setType(textToShow);
						} else {
							postalInfo.setType(null);
						}
					}

					break;
				case "countryCode":
					if (hasUserConsent && userConsent.isCountryCode()) {
						break;
					}
					privacySetting = privacySettings.get(key);
					isHidden = privacySetting.isHidden(userInfo);
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getCountryCode())) {
						isPrivate = true;
						if (privacySetting instanceof ObscuredPrivacy) {
							String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
							postalInfo.setCountryCode(textToShow);
						} else {
							postalInfo.setCountryCode(null);
						}
					}

					break;
				case "street1":
					if (hasUserConsent && userConsent.isStreet1()) {
						break;
					}
					privacySetting = privacySettings.get(key);
					isHidden = privacySetting.isHidden(userInfo);
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getStreet1())) {
						isPrivate = true;
						if (privacySetting instanceof ObscuredPrivacy) {
							String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
							postalInfo.setStreet1(textToShow);
						} else {
							postalInfo.setStreet1(null);
						}
					}

					break;
				case "street2":
					if (hasUserConsent && userConsent.isStreet2()) {
						break;
					}
					privacySetting = privacySettings.get(key);
					isHidden = privacySetting.isHidden(userInfo);
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getStreet2())) {
						isPrivate = true;
						if (privacySetting instanceof ObscuredPrivacy) {
							String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
							postalInfo.setStreet2(textToShow);
						} else {
							postalInfo.setStreet2(null);
						}
					}

					break;
				case "street3":
					if (hasUserConsent && userConsent.isStreet3()) {
						break;
					}
					privacySetting = privacySettings.get(key);
					isHidden = privacySetting.isHidden(userInfo);
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getStreet3())) {
						isPrivate = true;
						if (privacySetting instanceof ObscuredPrivacy) {
							String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
							postalInfo.setStreet3(textToShow);
						} else {
							postalInfo.setStreet3(null);
						}
					}

					break;
				case "postalCode":
					if (hasUserConsent && userConsent.isPostalCode()) {
						break;
					}
					privacySetting = privacySettings.get(key);
					isHidden = privacySetting.isHidden(userInfo);
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getPostalCode())) {
						isPrivate = true;
						if (privacySetting instanceof ObscuredPrivacy) {
							String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
							postalInfo.setPostalCode(textToShow);
						} else {
							postalInfo.setPostalCode(null);
						}
					}

					break;
				case "city":
					if (hasUserConsent && userConsent.isCity()) {
						break;
					}
					privacySetting = privacySettings.get(key);
					isHidden = privacySetting.isHidden(userInfo);
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getCity())) {
						isPrivate = true;
						if (privacySetting instanceof ObscuredPrivacy) {
							String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
							postalInfo.setCity(textToShow);
						} else {
							postalInfo.setCity(null);
						}
					}

					break;
				case "state":
					if (hasUserConsent && userConsent.isState()) {
						break;
					}
					privacySetting = privacySettings.get(key);
					isHidden = privacySetting.isHidden(userInfo);
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getState())) {
						isPrivate = true;
						if (privacySetting instanceof ObscuredPrivacy) {
							String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
							postalInfo.setState(textToShow);
						} else {
							postalInfo.setState(null);
						}
					}

					break;
				case "country":
					if (hasUserConsent && userConsent.isCountry()) {
						break;
					}
					privacySetting = privacySettings.get(key);
					isHidden = privacySetting.isHidden(userInfo);
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(postalInfo.getCountry())) {
						isPrivate = true;
						if (privacySetting instanceof ObscuredPrivacy) {
							String textToShow = ((ObscuredPrivacy) privacySetting).getTextToShow();
							postalInfo.setCountry(textToShow);
						} else {
							postalInfo.setCountry(null);
						}
					}

				default:
					break;
				}

			}
		}

		return isPrivate;
	}

}
