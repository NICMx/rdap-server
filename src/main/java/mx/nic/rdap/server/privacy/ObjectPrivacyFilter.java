package mx.nic.rdap.server.privacy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Link;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.Remark;

public class ObjectPrivacyFilter {

	private ObjectPrivacyFilter() {
		// Empty
	}

	static boolean filterEvents(List<Event> events, UserInfo userInfo, Map<String, PrivacySetting> privacySettings,
			Map<String, PrivacySetting> linkPrivacySettings) {
		boolean isPrivate = false;
		if (isValueEmpty(events)) {
			return false;
		}
		Set<Entry<String, PrivacySetting>> entrySet = privacySettings.entrySet();
		for (Event e : events) {
			for (Entry<String, PrivacySetting> entry : entrySet) {
				String key = entry.getKey();
				PrivacySetting setting = entry.getValue();
				boolean isHidden = setting.isHidden(userInfo);
				switch (key) {
				case "eventAction":
					if (isHidden && !isValueEmpty(e.getEventAction())) {
						e.setEventAction(null);
						isPrivate = true;
					}
					break;
				case "eventActor":
					if (isHidden && !isValueEmpty(e.getEventActor())) {
						e.setEventActor(null);
						isPrivate = true;
					}
					break;
				case "eventDate":
					if (isHidden && !isValueEmpty(e.getEventDate())) {
						e.setEventDate(null);
						isPrivate = true;
					}
					break;
				case "links":
					if (isHidden && !isValueEmpty(e.getLinks())) {
						e.setLinks(null);
						isPrivate = true;
					} else {
						isPrivate |= filterLinks(e.getLinks(), userInfo, linkPrivacySettings);
					}
					break;
				}
			}
		}

		return isPrivate;
	}

	static boolean filterLinks(List<Link> links, UserInfo userInfo, Map<String, PrivacySetting> privacySettings) {
		boolean isPrivate = false;

		if (isValueEmpty(links)) {
			return false;
		}

		Set<Entry<String, PrivacySetting>> entrySet = privacySettings.entrySet();
		for (Link l : links) {
			for (Entry<String, PrivacySetting> entry : entrySet) {
				String key = entry.getKey();
				PrivacySetting setting = entry.getValue();
				boolean isHidden = setting.isHidden(userInfo);
				switch (key) {
				case "value":
					if (isHidden && !isValueEmpty(l.getValue())) {
						l.setValue(null);
						isPrivate = true;
					}
					break;
				case "rel":
					if (isHidden && !isValueEmpty(l.getRel())) {
						l.setRel(null);
						isPrivate = true;
					}
					break;
				case "href":
					if (isHidden && !isValueEmpty(l.getHref())) {
						l.setHref(null);
						isPrivate = true;
					}
					break;
				case "hreflang":
					if (isHidden && !isValueEmpty(l.getHreflang())) {
						l.setHreflang(null);
						isPrivate = true;
					}
					break;
				case "title":
					if (isHidden && !isValueEmpty(l.getTitle())) {
						l.setTitle(null);
						isPrivate = true;
					}
					break;
				case "media":
					if (isHidden && !isValueEmpty(l.getMedia())) {
						l.setMedia(null);
						isPrivate = true;
					}
					break;
				case "type":
					if (isHidden && !isValueEmpty(l.getType())) {
						l.setType(null);
						isPrivate = true;
					}
					break;

				}
			}
		}
		return isPrivate;
	}

	static boolean filterRemarks(List<Remark> remarks, UserInfo userInfo, Map<String, PrivacySetting> privacySettings,
			Map<String, PrivacySetting> linkPrivacySettings) {
		boolean isPrivate = false;

		if (isValueEmpty(remarks)) {
			return false;
		}

		Set<Entry<String, PrivacySetting>> entrySet = privacySettings.entrySet();
		for (Remark r : remarks) {
			for (Entry<String, PrivacySetting> entry : entrySet) {
				String key = entry.getKey();
				PrivacySetting setting = entry.getValue();
				boolean isHidden = setting.isHidden(userInfo);
				switch (key) {
				case "title":
					if (isHidden && !isValueEmpty(r.getTitle())) {
						r.setTitle(null);
						isPrivate = true;
					}
					break;
				case "type":
					if (isHidden && !isValueEmpty(r.getType())) {
						r.setType(null);
						isPrivate = true;
					}
					break;
				case "description":
					if (isHidden && !isValueEmpty(r.getDescriptions())) {
						r.setDescriptions(null);
						isPrivate = true;
					}
					break;
				case "links":
					if (isHidden && !isValueEmpty(r.getLinks())) {
						r.setLinks(null);
						isPrivate = true;
					} else {
						isPrivate |= filterLinks(r.getLinks(), userInfo, linkPrivacySettings);
					}
					break;
				case "lang":
					if (isHidden && !isValueEmpty(r.getLanguage())) {
						r.setLanguage(null);
						isPrivate = true;
					}
					break;

				}
			}
		}

		return isPrivate;
	}

	static boolean filterPublicId(List<PublicId> publicIds, UserInfo userInfo,
			Map<String, PrivacySetting> privacySettings) {
		boolean isPrivate = false;

		if (isValueEmpty(publicIds)) {
			return false;
		}
		Set<Entry<String, PrivacySetting>> entrySet = privacySettings.entrySet();
		for (PublicId pid : publicIds) {
			for (Entry<String, PrivacySetting> entry : entrySet) {
				String key = entry.getKey();
				PrivacySetting setting = entry.getValue();
				boolean isHidden = setting.isHidden(userInfo);
				switch (key) {
				case "identifier":
					if (isHidden && !isValueEmpty(pid.getPublicId())) {
						pid.setPublicId(null);
						isPrivate = true;
					}
					break;
				case "type":
					if (isHidden && !isValueEmpty(pid.getType())) {
						pid.setType(null);
						isPrivate = true;
					}
					break;

				}
			}
		}

		return isPrivate;
	}

	@SuppressWarnings("rawtypes")
	static boolean isValueEmpty(Object value) {
		if (value == null) {
			return true;
		}

		if (value instanceof List) {
			boolean result = ((List) value).isEmpty();
			return result;
		}

		return false;
	}

}
