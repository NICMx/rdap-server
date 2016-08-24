package mx.nic.rdap.result;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import mx.nic.rdap.RdapResult;

public class PotatoResult implements RdapResult {

	@Override
	public JsonObject toJson() {
		return Json.createObjectBuilder() //
				.add("foo", "potato") //
				.add("baz", JsonValue.NULL) //
				.build();
	}

}
