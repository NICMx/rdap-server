package mx.nic.rdap.server.privacy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.DsData;
import mx.nic.rdap.core.db.KeyData;
import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.server.util.PrivacyUtil;

public class DomainPrivacyFilter {

	private DomainPrivacyFilter() {
		// no code;
	}

	/**
	 * Hides information that is inaccessible to the current user/subject
	 * 
	 * @param domain
	 *            {@link Domain} to be filtered
	 * @return true if the result was filter, otherwise false
	 */
	public static boolean filterDomain(Domain domain) {
		boolean isPrivate = false;

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getDomainPrivacySettings();
		Subject subject = SecurityUtils.getSubject();
		UserInfo userInfo = new UserInfo(subject,
				PrivacyUtil.isSubjectOwner(subject.getPrincipal().toString(), domain));

		for (String key : privacySettings.keySet()) {
			PrivacySetting setting = privacySettings.get(key);
			boolean isHidden = setting.isHidden(userInfo);
			switch (key) {
			case "handle":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getHandle())) {
					domain.setHandle(null);
					isPrivate = true;
				}
				break;
			case "ldhName":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getLdhName())) {
					domain.setLdhName(null);
					isPrivate = true;
				}
				break;
			case "unicodeName":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getUnicodeName())) {
					domain.setUnicodeName(null);
					isPrivate = true;
				}
				break;
			case "variants":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getVariants())) {
					domain.setVariants(null);
					isPrivate = true;
				} else {
					isPrivate |= filterVariants(domain.getVariants(), userInfo);
				}
				break;
			case "nameservers":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getNameServers())) {
					domain.setNameServers(null);
					isPrivate = true;
				} else {
					isPrivate |= NameserverPrivacyFilter.filterAnidatedNameserver(domain.getNameServers(), userInfo);
				}
				break;
			case "secureDNS":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getSecureDNS())) {
					domain.setSecureDNS(null);
					isPrivate = true;
				} else {
					isPrivate |= filterDomainSecureDns(domain.getSecureDNS(), userInfo);
				}
				break;
			case "entities":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getEntities())) {
					domain.setEntities(null);
					isPrivate = true;
				} else {
					isPrivate |= EntityPrivacyFilter.filterAnidatedEntities(domain.getEntities(), userInfo);
				}
				break;
			case "status":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getStatus())) {
					domain.setStatus(null);
					isPrivate = true;
				}
				break;
			case "publicIds":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getPublicIds())) {
					domain.setPublicIds(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterPublicId(domain.getPublicIds(), userInfo,
							PrivacyUtil.getDomainPublicIdsPrivacySettings());
				}
				break;
			case "remarks":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getRemarks())) {
					domain.setRemarks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterRemarks(domain.getRemarks(), userInfo,
							PrivacyUtil.getDomainRemarkPrivacySettings(), PrivacyUtil.getDomainLinkPrivacySettings());
				}
				break;
			case "links":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getLinks())) {
					domain.setLinks(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterLinks(domain.getLinks(), userInfo,
							PrivacyUtil.getDomainLinkPrivacySettings());
				}
				break;
			case "port43":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getPort43())) {
					domain.setPort43(null);
					isPrivate = true;
				}
				break;
			case "events":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getEvents())) {
					domain.setEvents(null);
					isPrivate = true;
				} else {
					isPrivate |= ObjectPrivacyFilter.filterEvents(domain.getEvents(), userInfo,
							PrivacyUtil.getDomainEventPrivacySettings(), PrivacyUtil.getDomainLinkPrivacySettings());
				}
				break;
			case "network":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getIpNetwork())) {
					domain.setIpNetwork(null);
					isPrivate = true;
				} else {
					isPrivate |= IpNetworkPrivacyFilter.filterIpNetwork(domain.getIpNetwork(), userInfo);
				}
				break;
			case "lang":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(domain.getLang())) {
					domain.setLang(null);
					isPrivate = true;
				}
				break;
			}
		}
		return isPrivate;
	}

	private static boolean filterDomainSecureDns(SecureDNS secureDNS, UserInfo userInfo) {
		boolean isPrivate = false;

		if (ObjectPrivacyFilter.isValueEmpty(secureDNS)) {
			return false;
		}

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getSecureDnsPrivacySettings();
		Set<Entry<String, PrivacySetting>> entrySet = privacySettings.entrySet();
		for (Entry<String, PrivacySetting> entry : entrySet) {
			String key = entry.getKey();
			PrivacySetting setting = entry.getValue();
			boolean isHidden = setting.isHidden(userInfo);
			switch (key) {
			case "zoneSigned":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(secureDNS.getZoneSigned())) {
					secureDNS.setZoneSigned(null);
					isPrivate = true;
				}
				break;
			case "delegationSigned":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(secureDNS.getDelegationSigned())) {
					secureDNS.setDelegationSigned(null);
					isPrivate = true;
				}
				break;
			case "maxSigLife":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(secureDNS.getMaxSigLife())) {
					secureDNS.setMaxSigLife(null);
					isPrivate = true;
				}
				break;
			case "dsData":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(secureDNS.getDsData())) {
					secureDNS.setDsData(null);
					isPrivate = true;
				} else {
					isPrivate |= filterDomainDsData(secureDNS.getDsData(), userInfo);
				}
				break;
			case "keyData":
				if (isHidden && !ObjectPrivacyFilter.isValueEmpty(secureDNS.getKeyData())) {
					secureDNS.setKeyData(null);
					isPrivate = true;
				} else {
					isPrivate |= filterDomainKeyData(secureDNS.getKeyData(), userInfo);
				}
				break;

			}
		}

		return isPrivate;
	}

	private static boolean filterDomainKeyData(List<KeyData> keys, UserInfo userInfo) {
		boolean isPrivate = false;

		if (ObjectPrivacyFilter.isValueEmpty(keys)) {
			return isPrivate;
		}

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getKeyDataPrivacySettings();
		Set<Entry<String, PrivacySetting>> entrySet = privacySettings.entrySet();
		for (KeyData k : keys) {
			for (Entry<String, PrivacySetting> entry : entrySet) {
				String key = entry.getKey();
				PrivacySetting setting = entry.getValue();
				boolean isHidden = setting.isHidden(userInfo);
				switch (key) {
				case "flags":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(k.getFlags())) {
						k.setFlags(null);
						isPrivate = true;
					}
					break;
				case "protocol":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(k.getProtocol())) {
						k.setProtocol(null);
						isPrivate = true;
					}
					break;
				case "publicKey":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(k.getPublicKey())) {
						k.setPublicKey(null);
						isPrivate = true;
					}
					break;
				case "algorithm":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(k.getAlgorithm())) {
						k.setAlgorithm(null);
						isPrivate = true;
					}
					break;
				case "events":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(k.getEvents())) {
						k.setEvents(null);
						isPrivate = true;
					} else {
						isPrivate |= ObjectPrivacyFilter.filterEvents(k.getEvents(), userInfo,
								PrivacyUtil.getDomainEventPrivacySettings(),
								PrivacyUtil.getDomainLinkPrivacySettings());
					}
					break;
				case "links":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(k.getLinks())) {
						k.setLinks(null);
						isPrivate = true;
					} else {
						isPrivate |= ObjectPrivacyFilter.filterLinks(k.getLinks(), userInfo,
								PrivacyUtil.getDomainLinkPrivacySettings());
					}
					break;
				}
			}
		}

		return isPrivate;
	}

	private static boolean filterDomainDsData(List<DsData> dsDatas, UserInfo userInfo) {
		boolean isPrivate = false;

		if (ObjectPrivacyFilter.isValueEmpty(dsDatas)) {
			return isPrivate;
		}

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getKeyDataPrivacySettings();
		Set<Entry<String, PrivacySetting>> entrySet = privacySettings.entrySet();
		for (DsData ds : dsDatas) {
			for (Entry<String, PrivacySetting> entry : entrySet) {
				String key = entry.getKey();
				PrivacySetting setting = entry.getValue();
				boolean isHidden = setting.isHidden(userInfo);
				switch (key) {
				case "keyTag":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ds.getKeytag())) {
						ds.setKeytag(null);
						isPrivate = true;
					}
					break;
				case "algorithm":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ds.getAlgorithm())) {
						ds.setAlgorithm(null);
						isPrivate = true;
					}
					break;
				case "digest":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ds.getDigest())) {
						ds.setDigest(null);
						isPrivate = true;
					}
					break;
				case "digestType":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ds.getDigestType())) {
						ds.setDigestType(null);
						isPrivate = true;
					}
					break;
				case "events":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ds.getEvents())) {
						ds.setEvents(null);
						isPrivate = true;
					} else {
						isPrivate |= ObjectPrivacyFilter.filterEvents(ds.getEvents(), userInfo,
								PrivacyUtil.getDomainEventPrivacySettings(),
								PrivacyUtil.getDomainLinkPrivacySettings());
					}
					break;
				case "links":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(ds.getLinks())) {
						ds.setLinks(null);
						isPrivate = true;
					} else {
						isPrivate |= ObjectPrivacyFilter.filterLinks(ds.getLinks(), userInfo,
								PrivacyUtil.getDomainLinkPrivacySettings());
					}
					break;
				}
			}
		}
		return isPrivate;
	}

	private static boolean filterVariants(List<Variant> variants, UserInfo userInfo) {
		boolean isPrivate = false;

		if (ObjectPrivacyFilter.isValueEmpty(variants)) {
			return false;
		}

		Map<String, PrivacySetting> privacySettings = PrivacyUtil.getDomainVariantsPrivacySettings();
		Set<Entry<String, PrivacySetting>> entrySet = privacySettings.entrySet();
		for (Variant v : variants) {
			for (Entry<String, PrivacySetting> entry : entrySet) {
				String key = entry.getKey();
				boolean isHidden = entry.getValue().isHidden(userInfo);
				switch (key) {
				case "relation":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(v.getRelations())) {
						v.setRelations(null);
						isPrivate = true;
					}
					break;
				case "idnTable":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(v.getIdnTable())) {
						v.setIdnTable(null);
						isPrivate = true;
					}
					break;
				case "variantNames":
					if (isHidden && !ObjectPrivacyFilter.isValueEmpty(v.getVariantNames())) {
						v.setVariantNames(null);
						isPrivate = true;
					}
					break;
				// TODO
				// case "ldhName":
				// if (isHidden && !ObjectPrivacyFilter.isValueEmpty(v)) {
				// isPrivate = true;
				// }
				// break;
				// case "unicodeName":
				// if (isHidden && !ObjectPrivacyFilter.isValueEmpty(v)) {
				// isPrivate = true;
				// }
				// break;

				}
			}

		}

		return isPrivate;
	}

}
