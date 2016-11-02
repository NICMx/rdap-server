package mx.nic.rdap.server.renderer.json;

import java.net.IDN;
import java.sql.SQLException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.core.db.SecureDNS;
import mx.nic.rdap.core.db.Variant;
import mx.nic.rdap.db.DomainDAO;
import mx.nic.rdap.db.LinkDAO;
import mx.nic.rdap.db.NameserverDAO;
import mx.nic.rdap.db.PublicIdDAO;
import mx.nic.rdap.db.SecureDNSDAO;
import mx.nic.rdap.db.VariantDAO;

/**
 * Parser for the DomainDAO object.
 * 
 * @author evaldes
 *
 */
public class DomainParser implements  JsonParser {

	DomainDAO domain;
	/**
	 * Default Constructor
	 */
	public DomainParser() {
	}

	/**
	 * Construct DomainParser with a ResultSet
	 * 
	 * @param resultSet
	 * @throws SQLException
	 */
	public DomainParser(DomainDAO domain)  {
		this.domain=domain;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.json.JsonParser#toJson()
	 */
	@Override
	public JsonObject getJson() {
		domain.getLinks().add(new LinkDAO("domain", domain.getLdhName()));

		JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("objectClassName", "domain");
		JsonUtil.getCommonRdapJsonObject(builder, domain);
		builder.add("ldhName", domain.getLdhName());
		builder.add("unicodeName", IDN.toUnicode(domain.getLdhName()));
		if (domain.getVariants() != null && !domain.getVariants().isEmpty()) {
			builder.add("variants", this.getVariantsJson(domain.getVariants()));
		}
		if (domain.getPublicIds() != null && !domain.getPublicIds().isEmpty()) {
			builder.add("publicIds", this.getPublicIdsJson(domain.getPublicIds()));
		}

		if (domain.getNameServers() != null && !domain.getNameServers().isEmpty()) {
			builder.add("nameservers", this.getNameServersJson(domain.getNameServers()));
		}
		getSecureDNSJson(builder, domain.getSecureDNS());
		return builder.build();
	}

	private JsonArray getVariantsJson(List<Variant> variants) {
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Variant variant : variants) { 
			VariantParser parser= new VariantParser((VariantDAO)variant);
			JsonObject json =parser.getJson();
			arrB.add(json);
		}
		return arrB.build();
	}

	private JsonArray getPublicIdsJson(List<PublicId> publicIds) {
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (PublicId publicId : publicIds) {
			PublicIdParser parser=new PublicIdParser((PublicIdDAO)publicId);
			JsonObject json = parser.getJson();
			arrB.add(json);
		}
		return arrB.build();
	}

	private JsonArray getNameServersJson(List<Nameserver> nameservers) {
		JsonArrayBuilder arrB = Json.createArrayBuilder();
		for (Nameserver nameserver : nameservers) {
			NameserverParser parser=new NameserverParser((NameserverDAO)nameserver);
			JsonObject json = parser.getJson();
			arrB.add(json);
		}
		return arrB.build();
	}

	private JsonObjectBuilder getSecureDNSJson(JsonObjectBuilder builder, SecureDNS secureDns) {
		if (secureDns != null) {
			SecureDNSParser parser=new SecureDNSParser((SecureDNSDAO)secureDns);
			builder.add("secureDNS", parser.getJson());
		}
		return builder;
	}
}
