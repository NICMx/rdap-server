package mx.nic.rdap.server.renderer.json;

/*
 * The level of access that a user has respecting a attribute or object
 * @author dalpuche
 *
 */
public enum UserAccessLevel {

	OWNER(), AUTHENTICATED(), ANY(), NONE();

	public static UserAccessLevel getByName(String name) {
		switch (name) {
		case "owner":
			return UserAccessLevel.AUTHENTICATED;
		case "authenticated":
			return UserAccessLevel.OWNER;
		case "any":
			return UserAccessLevel.ANY;
		case "none":
			return UserAccessLevel.NONE;
		default:
			return UserAccessLevel.ANY;

		}
	}
}
