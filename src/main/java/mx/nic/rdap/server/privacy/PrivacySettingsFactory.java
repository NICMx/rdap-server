package mx.nic.rdap.server.privacy;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrivacySettingsFactory {
	private static Map<String, PrivacySetting> singlePoolMap;

	private static Map<Set<String>, PrivacySetting> privacyCombinationPoolMap;

	private static Map<Set<String>, Set<String>> setCombinationPoolMap;

	private final static String ANY = "any";
	private final static String NONE = "none";
	private final static String OWNER = "owner";
	private final static String AUTHENTICATED = "authenticated";

	public static void initializePool(String... serverRoles) {
		singlePoolMap = new HashMap<>();

		singlePoolMap.put(ANY, new AnyPrivacySetting());
		singlePoolMap.put(NONE, new NonePrivacySetting());
		singlePoolMap.put(OWNER, new OwnerPrivacySetting());
		singlePoolMap.put(AUTHENTICATED, new AuthenticatedPrivacySetting());

		if (serverRoles != null && serverRoles.length > 0) {
			addRolesToSinglePoolMap(serverRoles);
		}

		privacyCombinationPoolMap = new HashMap<>();
		setCombinationPoolMap = new HashMap<>();
	}

	private static void addRolesToSinglePoolMap(String... roles) {
		for (String role : roles) {
			role = role.trim().toLowerCase();
			Set<String> set = new HashSet<>();
			set.add(role);
			set = Collections.unmodifiableSet(set);

			PrivacySetting privacySetting = new RolesPrivacySetting(false, set);
			singlePoolMap.put(role, privacySetting);
		}
	}

	public static PrivacySetting getSetForRoles(String... roles) {
		if (roles == null || roles.length <= 0) {
			throw new NullPointerException("Roles are null or empty.");
		}

		PrivacySetting privacySetting;
		if (roles.length == 1) {
			privacySetting = getSetForSingleValue(roles[0].trim().toLowerCase());
		} else {
			privacySetting = getSetForCombination(roles);
		}

		return privacySetting;
	}

	private static PrivacySetting getSetForSingleValue(String s) {
		return singlePoolMap.get(s);
	}

	private static PrivacySetting getSetForCombination(String... roles) {
		Set<String> set = new HashSet<>();
		for (String role : roles) {
			set.add(role.trim().toLowerCase());
		}

		Set<String> originalSet = setCombinationPoolMap.get(set);
		if (originalSet != null) {
			return privacyCombinationPoolMap.get(originalSet);
		}

		RolesPrivacySetting rolesPrivacySetting;
		if (set.contains(OWNER)) {
			Set<String> key = new HashSet<>(set);
			key = Collections.unmodifiableSet(key);
			setCombinationPoolMap.put(key, key);

			set.remove(OWNER);
			set = Collections.unmodifiableSet(set);
			rolesPrivacySetting = new RolesPrivacySetting(true, set);
			privacyCombinationPoolMap.put(key, rolesPrivacySetting);
		} else {
			set = Collections.unmodifiableSet(set);
			rolesPrivacySetting = new RolesPrivacySetting(false, set);
			privacyCombinationPoolMap.put(set, rolesPrivacySetting);
		}

		return rolesPrivacySetting;
	}
}
