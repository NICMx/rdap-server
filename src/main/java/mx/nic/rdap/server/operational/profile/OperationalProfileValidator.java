package mx.nic.rdap.server.operational.profile;

import java.util.logging.Level;
import java.util.logging.Logger;

import mx.nic.rdap.core.catalog.EventAction;
import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.server.RdapConfiguration;

public class OperationalProfileValidator {
	private final static Logger logger = Logger.getLogger(OperationalProfileValidator.class.getName());

	public static void validateEntityEvents(Entity entity) {
		String warningMessage = "When using profile " + RdapConfiguration.getServerProfile()
				+ ", an entity object must contain an events member with the following events:An event of eventAction type registration; An event of eventAction type last changed (MUST be omitted if the Contact Object  has not been updated since it was created;An event of eventAction type last update of RDAP database.";
		if (entity.getEvents() == null || entity.getEvents().isEmpty()) {
			logger.log(Level.WARNING, warningMessage);
			return;
		}
		boolean containsRegistration=false;
		boolean containgsLastUpdate=false;
		for(Event event:entity.getEvents()){
			if(event.getEventAction().equals(EventAction.REGISTRATION)){
				containsRegistration=true;
			}
			else if(event.getEventAction().equals(EventAction.LAST_UPDATE_OF_RDAP_DATABASE)){
				containgsLastUpdate=true;
			}
		}
		if(!containgsLastUpdate||!containsRegistration){
			logger.log(Level.WARNING, warningMessage);
			return;
		}
	}
}
