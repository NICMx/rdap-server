package mx.nic.rdap.server.operational.profile;

import java.util.logging.Logger;

import mx.nic.rdap.core.catalog.EventAction;
import mx.nic.rdap.core.catalog.Rol;
import mx.nic.rdap.core.db.Domain;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.Nameserver;
import mx.nic.rdap.core.db.VCard;
import mx.nic.rdap.core.db.VCardPostalInfo;
import mx.nic.rdap.server.RdapConfiguration;

public class OperationalProfileValidator {
	private final static Logger logger = Logger.getLogger(OperationalProfileValidator.class.getName());

	// Point 1.4.12 of rdap operational profile by ICANN
	public static void validateEntityEvents(Entity entity) {
		String warningMessage = "When using profile " + RdapConfiguration.getServerProfile()
				+ ", an entity object must contain an events member with the following events:An event of eventAction type registration; An event of eventAction type last changed (MUST be omitted if the Contact Object  has not been updated since it was created);An event of eventAction type last update of RDAP database.";
		if (entity.getEvents() == null || entity.getEvents().isEmpty()) {
			logger.warning(warningMessage);
			return;
		}
		boolean containsRegistration = false;
		boolean containsLastUpdate = false;

		for (Event event : entity.getEvents()) {
			if (event.getEventAction().equals(EventAction.REGISTRATION)) {
				containsRegistration = true;
			} else if (event.getEventAction().equals(EventAction.LAST_UPDATE_OF_RDAP_DATABASE)) {
				containsLastUpdate = true;
			}
		}
		if (!containsLastUpdate || !containsRegistration) {
			logger.warning(warningMessage);
			return;
		}
	}

	public static void validateDomain(Domain domain) {
		OperationalProfileValidator.validateDomainStatus(domain);
		OperationalProfileValidator.validateDomainNameservers(domain);
		OperationalProfileValidator.validateDomainEntities(domain);
		OperationalProfileValidator.validateDomainRegistrar(domain);
		OperationalProfileValidator.validateDomainEvents(domain);
		OperationalProfileValidator.validateDomainSecureDns(domain);
	}

	// Point 1.5.19 of rdap operational profile by ICANN
	private static void validateDomainSecureDns(Domain domain) {
		if (domain.getSecureDNS() != null && domain.getSecureDNS().getDelegationSigned()) {
			if (domain.getSecureDNS().getDsData() == null || domain.getSecureDNS().getDsData().isEmpty()
					|| domain.getSecureDNS().getMaxSigLife() == null) {
				logger.warning("When using profile " + RdapConfiguration.getServerProfile()
						+ ",the domain object MUST contain a secureDNS member [RFC7483] including at least a delegationSigned element.  Other elements (e.g. dsData, maxSigLife) of the secureDNS member MUST be included, if the domain name is signed");
			}
		}

	}

	// Point 1.5.14 of rdap operational profile by ICANN
	private static void validateDomainEvents(Domain domain) {
		boolean containsRegistration = false;
		boolean containsLastUpdate = false;
		boolean containtsExpiration = false;
		String warningMessage = "When using profile " + RdapConfiguration.getServerProfile()
				+ ", a domain object must contain an events member with the following events:An event of eventAction type registration;An event of eventAction type expiration; An event of eventAction type last changed (MUST be omitted if the Domain Object  has not been updated since it was created);An event of eventAction type last update of RDAP database.";
		if (domain.getEvents() == null || domain.getEvents().isEmpty()) {
			logger.warning(warningMessage);
			return;
		}
		for (Event event : domain.getEvents()) {
			if (event.getEventAction() == EventAction.REGISTRATION) {
				containsRegistration = true;
			}
			if (event.getEventAction() == EventAction.LAST_UPDATE_OF_RDAP_DATABASE) {
				containsLastUpdate = true;
			}
			if (event.getEventAction() == EventAction.EXPIRATION) {
				containtsExpiration = true;
			}
		}
		if (!containsLastUpdate || !containsRegistration || !containtsExpiration) {
			logger.warning(warningMessage);
		}

	}

	// Point 1.5.12 of rdap operational profile by ICANN
	private static void validateDomainRegistrar(Domain domain) {
		boolean existRegistrar = false;
		for (Entity ent : domain.getEntities()) {
			if (ent.getRoles() != null) {
				if (ent.getRoles().contains(Rol.REGISTRAR)) {
					existRegistrar = true;
					if (ent.getPublicIds() == null || ent.getPublicIds().isEmpty()) {
						logger.warning(
								"The entity with the registrar role in the RDAP response MUST contain a publicIDs member");
					}
				}
			}
		}

		if (!existRegistrar) {
			logger.warning("The domain object in the RDAP response MUST contain an entity with the registrar role");
		}

	}

	// Point 1.5.3 of rdap operational profile by ICANN
	private static void validateDomainStatus(Domain domain) {
		if (domain.getStatus() == null || domain.getStatus().isEmpty()) {
			logger.warning("When using profile " + RdapConfiguration.getServerProfile()
					+ ",domain object in the RDAP response MUST contain a status member.");

		}
	}

	// Point 1.5.7 of rdap operational profile by ICANN
	private static void validateDomainNameservers(Domain domain) {
		for (Nameserver ns : domain.getNameServers()) {
			if (ns.getLdhName() == null || ns.getLdhName().isEmpty()) {
				logger.warning("When using profile " + RdapConfiguration.getServerProfile()
						+ ", each nameserver of a Domain object MUST contain the following member: ldhName");
			}

			OperationalProfileValidator.validateNameserverEvents(ns);
		}
	}

	// Point 1.4.13 of rdap operational profile by ICANN
	private static void validateNameserverEvents(Nameserver ns) {
		String warningMessage = "When using profile " + RdapConfiguration.getServerProfile()
				+ ", an Nameserver object must contain an events member with the following events:An event of eventAction type registration; An event of eventAction type last changed (MUST be omitted if the Host Object  has not been updated since it was created);An event of eventAction type last update of RDAP database.";
		if (ns.getEvents() == null || ns.getEvents().isEmpty()) {
			logger.warning(warningMessage);
			return;
		}
		boolean containsRegistration = false;
		boolean containsLastUpdate = false;

		for (Event event : ns.getEvents()) {
			if (event.getEventAction().equals(EventAction.REGISTRATION)) {
				containsRegistration = true;
			} else if (event.getEventAction().equals(EventAction.LAST_UPDATE_OF_RDAP_DATABASE)) {
				containsLastUpdate = true;
			}
		}
		if (!containsLastUpdate || !containsRegistration) {
			logger.warning(warningMessage);
			return;
		}
	}

	// Point 1.5.8 of rdap operational profile by ICANN
	private static void validateDomainEntities(Domain domain) {
		String warningMessage = "When using profile " + RdapConfiguration.getServerProfile()
				+ ",The domain object MUST contain entities with the following roles: registrant, administrative and technical.Exactly one entity per role, each of them with a handle and valid members fn, adr, tel, email ";
		Boolean existRegistrant = false;
		Boolean existAdministrative = false;
		Boolean existTechnical = false;
		for (Entity ent : domain.getEntities()) {
			OperationalProfileValidator.validateEntityEvents(ent);
			if (!ent.getRoles().isEmpty()) {
				for (Rol rol : ent.getRoles()) {
					if (rol == Rol.REGISTRANT) {
						// validate if there is already a entity with the same
						// rol
						if (existRegistrant || !isDomainEntityVcardValid(ent)) {
							logger.warning(warningMessage);
							return;
						} else {
							existRegistrant = true;
						}
					} else if (rol == Rol.ADMINISTRATIVE) {
						if (existAdministrative || !isDomainEntityVcardValid(ent)) {
							logger.warning(warningMessage);
							return;
						}
						existAdministrative = true;
					} else if (rol == Rol.TECHNICAL) {
						if (existTechnical || !isDomainEntityVcardValid(ent)) {
							logger.warning(warningMessage);
							return;
						}
						existTechnical = true;
					}
				}
			}
		}
		if (!existRegistrant || !existAdministrative || !existTechnical) {
			logger.warning(warningMessage);
		}
	}

	private static boolean isDomainEntityVcardValid(Entity ent) {
		if (ent.getVCardList() == null || ent.getVCardList().isEmpty() || ent.getVCardList().get(0) == null) {
			return false;
		} else {
			VCard vcard = ent.getVCardList().get(0);
			if (vcard.getName() == null || vcard.getName().isEmpty()) {
				return false;
			}
			if (vcard.getVoice() == null || vcard.getVoice().isEmpty()) {
				return false;
			}
			if (vcard.getEmail() == null || vcard.getEmail().isEmpty()) {
				return false;
			}
			if (vcard.getPostalInfo() == null || vcard.getPostalInfo().isEmpty()
					|| vcard.getPostalInfo().get(0) == null) {
				return false;
			} else {
				return isDomainEntityPostalInfoValid(vcard.getPostalInfo().get(0));
			}
		}
	}

	// Point 1.5.10 of rdap operational profile by ICANN
	private static boolean isDomainEntityPostalInfoValid(VCardPostalInfo postalInfo) {
		String warningMessage = "When using profile " + RdapConfiguration.getServerProfile()
				+ ",The following RDDS fields used to generate the adr member of the entities with the registrant, administrative and technical roles are REQUIRED: Street,City and Country";
		if (postalInfo.getCountry() == null || postalInfo.getCountry().isEmpty()) {
			logger.warning(warningMessage);
			return false;
		}
		if (postalInfo.getCity() == null || postalInfo.getCity().isEmpty()) {
			logger.warning(warningMessage);
			return false;
		}
		if (postalInfo.getStreet1() == null || postalInfo.getStreet1().isEmpty()) {
			logger.warning(warningMessage);
			return false;
		}
		return true;
	}

}
