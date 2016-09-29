package mx.nic.rdap.server.result;

import javax.json.JsonObject;

import mx.nic.rdap.core.db.Registrar;
import mx.nic.rdap.server.RdapResult;
import mx.nic.rdap.server.db.RegistrarDAO;

/**
 * A result from an Entity request
 * 
 * @author dhfelix
 *
 */
public class RegistrarResult implements RdapResult {

	private Registrar registrar;

	public RegistrarResult(Registrar registrar) {
		this.registrar = registrar;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.RdapResult#toJson()
	 */
	@Override
	public JsonObject toJson() {
		return ((RegistrarDAO) registrar).toJson();
	}

}
