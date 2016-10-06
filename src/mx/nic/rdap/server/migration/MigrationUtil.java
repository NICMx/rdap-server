package mx.nic.rdap.server.migration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.server.db.EntityDAO;
import mx.nic.rdap.server.db.EventDAO;
import mx.nix.rdap.core.catalog.EventAction;
import mx.nix.rdap.core.catalog.Rol;
import mx.nix.rdap.core.catalog.Status;

/**
 * Utilitys for the migration proccess
 * 
 * @author dalpuche
 *
 */
public class MigrationUtil {

	/**
	 * Process the resultset and return a list of RdapStatus
	 * 
	 * @param resultSet
	 *            must have the form: “rdap_status, rdap_status, …”
	 * @return
	 */
	public static List<Status> getRDAPStatusFromResultSet(String resultSet) {
		List<Status> statusList = new ArrayList<Status>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<String> statusListString = Arrays.asList(resultSet.split(","));
			for (String statusString : statusListString) {
				Status status = Status.getByName(statusString.trim());
				if (status != null)
					statusList.add(status);
			}
		}
		return statusList;
	}

	/**
	 * PRocess the resultset and map a list of eppStatus to rdapStatus
	 * 
	 * @param resultSet
	 *            must have the form: “epp_status, epp_status, …”
	 * @return
	 */
	public static List<Status> getRDAPStatusFromEPPStatusResultSet(String resultSet) {
		List<Status> statusList = new ArrayList<Status>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<String> statusListString = Arrays.asList(resultSet.split(","));
			for (String statusString : statusListString) {
				Status status = Status.getByEPPName(statusString.trim());
				if (status != null)
					statusList.add(status);
			}
		}
		return statusList;
	}

	/**
	 * Process the resultSet an return a list of events
	 * 
	 * @param resultSet
	 *            must have the form: “eventData1, eventData2” And each
	 *            "evenData" must have the form “EventAction | eventDate |
	 *            eventActor”
	 * 
	 * @return
	 */
	public static List<Event> getEventsFromResultSet(String resultSet) {
		List<Event> eventList = new ArrayList<Event>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<List<String>> eventDataStructureList = getDataStructureList(resultSet);
			for (List<String> eventData : eventDataStructureList) {
				EventDAO event = new EventDAO();
				String eventActionString = eventData.get(0);
				String eventDateString = eventData.get(1);
				String eventActorString = eventData.get(2);

				if (isResultSetValueValid(eventActionString)) {
					EventAction action = EventAction.getByName(eventActionString.trim());
					if (action == null) {
						throw new RuntimeException("Invalidad event action:" + eventActionString);
					}
					event.setEventAction(action);
				} else {
					throw new RuntimeException("Invalidad event action:" + eventActionString);
				}

				if (isResultSetValueValid(eventDateString)) {
					Date eventDate = Date.from(Instant.parse(eventDateString.trim()));
					event.setEventDate(eventDate);
				} else {
					throw new RuntimeException("Invalidad event date:" + eventDateString);
				}
				if (isResultSetValueValid(eventActorString)) {
					event.setEventActor(eventActorString.trim());
				}
				eventList.add(event);
			}
		}
		return eventList;
	}

	/**
	 * Process the resultSet and return a list of entitys with their roles
	 * 
	 * @param resultSet
	 *            must have the form: “entityData1, entityData2” And each
	 *            "entityData" Must have the form: “handle | rol”
	 * 
	 * @return
	 */
	public static List<Entity> getEntityAndRolesFromResultSet(String resultSet) {
		List<Entity> entityList = new ArrayList<Entity>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<List<String>> entityDataStructureList = getDataStructureList(resultSet);
			for (List<String> entityData : entityDataStructureList) {
				EntityDAO entity = new EntityDAO();
				String handle = entityData.get(0);
				String rolString = entityData.get(1);
				if (isResultSetValueValid(handle)) {
					entity.setHandle(handle);
				} else {
					throw new RuntimeException("Invalidad entity handle:" + handle);
				}
				if (isResultSetValueValid(rolString)) {
					Rol rol = Rol.getByName(rolString.trim());
					if (rol != null) {
						entity.getRoles().add(rol);
					}
				} else {
					throw new RuntimeException("Invalidad entity rol:" + rolString);
				}
				entityList.add(entity);
			}
		}
		return entityList;
	}

	/**
	 * Split the resultSet String to a List of lists
	 * 
	 * @param resultSet
	 *            must have the form
	 *            "dataStructura1Data1|dataStructura1Data2|...,dataStructura2Data1|dataStructura2Data2|...,..."
	 * @return
	 */
	public static List<List<String>> getDataStructureList(String resultSet) {
		List<List<String>> dataStructureList = new ArrayList<>();

		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<String> auxList = Arrays.asList(resultSet.trim().split(","));
			for (String aux : auxList) {
				List<String> dataList = Arrays.asList(aux.trim().split("|"));
				dataStructureList.add(dataList);
			}
		}
		return dataStructureList;
	}

	/**
	 * Validate if a string is not null or empty
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isResultSetValueValid(String value) {
		if (value == null | value.isEmpty() | value.trim().isEmpty())
			return false;
		return true;
	}
}
