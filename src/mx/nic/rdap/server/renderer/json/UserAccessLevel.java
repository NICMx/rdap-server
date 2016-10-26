package mx.nic.rdap.server.renderer.json;

/*
 * The level of access that a user has respecting a attribute or object
 * @author dalpuche
 *
 */
public enum UserAccessLevel {

	OWNER(), AUTHENTICATED(), ANY(), NONE();

}
