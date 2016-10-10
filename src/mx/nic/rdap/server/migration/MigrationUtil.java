package mx.nic.rdap.server.migration;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import mx.nic.rdap.core.db.Entity;
import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.core.db.PublicId;
import mx.nic.rdap.server.db.EntityDAO;
import mx.nic.rdap.server.db.EventDAO;
<<<<<<< HEAD
import mx.nic.rdap.server.exception.InvalidValueException;
import mx.nic.rdap.server.exception.InvalidadDataStructure;
import mx.nic.rdap.server.exception.RequiredValueNotFoundException;
=======
import mx.nic.rdap.server.db.PublicIdDAO;
>>>>>>> branch 'migration' of ssh://git@200.34.22.164:2222/opt/git/rdap-server.git
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
	 * @throws InvalidValueException
	 */
	public static List<Status> getRDAPStatusFromResultSet(String resultSet) throws InvalidValueException {
		List<Status> statusList = new ArrayList<Status>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<String> statusListString = Arrays.asList(resultSet.split(","));
			for (String statusString : statusListString) {
				Status status = Status.getByName(statusString.trim());
				if (status != null)
					statusList.add(status);
				else
					throw new InvalidValueException("Value", "Status", statusString);
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
	 * @throws InvalidValueException
	 */
	public static List<Status> getRDAPStatusFromEPPStatusResultSet(String resultSet) throws InvalidValueException {
		List<Status> statusList = new ArrayList<Status>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<String> statusListString = Arrays.asList(resultSet.split(","));
			for (String statusString : statusListString) {
				Status status = Status.getByEPPName(statusString.trim());
				if (status != null)
					statusList.add(status);
				else
					throw new InvalidValueException("Value", "Status", statusString);
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
	 * @throws RequiredValueNotFoundException
	 * @throws InvalidValueException
	 * @throws InvalidadDataStructure
	 */
	public static List<Event> getEventsFromResultSet(String resultSet)
			throws RequiredValueNotFoundException, InvalidValueException, InvalidadDataStructure {
		List<Event> eventList = new ArrayList<Event>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<List<String>> eventDataStructureList;
			try {
				eventDataStructureList = getDataStructureList(resultSet, 3);
				for (List<String> eventData : eventDataStructureList) {
					EventDAO event = new EventDAO();
					String eventActionString = eventData.get(0);
					String eventDateString = eventData.get(1);
					String eventActorString = eventData.get(2);

					if (isResultSetValueValid(eventActionString)) {
						EventAction action = EventAction.getByName(eventActionString.trim());
						if (action == null) {
							throw new InvalidValueException("EventAction", "Event", eventActionString);
						}
						event.setEventAction(action);
					} else {
						throw new RequiredValueNotFoundException("EventAction", "Event");
					}

					if (isResultSetValueValid(eventDateString)) {
						Date eventDate = Date.from(Instant.parse(eventDateString.trim()));
						event.setEventDate(eventDate);
					} else {
						throw new RequiredValueNotFoundException("EventDate", "Event");
					}
					if (isResultSetValueValid(eventActorString)) {
						event.setEventActor(eventActorString.trim());
					}
					eventList.add(event);
				}
			} catch (InvalidadDataStructure e) {
				throw new InvalidadDataStructure("eventData", "“EventAction | eventDate | eventActor”");
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
	 * @throws RequiredValueNotFoundException
	 * @throws InvalidValueException
	 * @throws InvalidadDataStructure
	 */
	public static List<Entity> getEntityAndRolesFromResultSet(String resultSet)
			throws RequiredValueNotFoundException, InvalidValueException, InvalidadDataStructure {
		List<Entity> entityList = new ArrayList<Entity>();
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			try {
				List<List<String>> entityDataStructureList = getDataStructureList(resultSet, 2);
				for (List<String> entityData : entityDataStructureList) {
					EntityDAO entity = new EntityDAO();
					String handle = entityData.get(0);
					String rolString = entityData.get(1);
					if (isResultSetValueValid(handle)) {
						entity.setHandle(handle);
					} else {
						throw new RequiredValueNotFoundException("Handle", "Entity");
					}
					if (isResultSetValueValid(rolString)) {
						Rol rol = Rol.getByName(rolString.trim());
						if (rol != null) {
							entity.getRoles().add(rol);
						} else {
							throw new InvalidValueException("Rol", "Entity", rolString);
						}
					} else {
						throw new RequiredValueNotFoundException("Rol", "Entity");
					}
					entityList.add(entity);
				}
			} catch (InvalidadDataStructure e) {
				throw new InvalidadDataStructure("entityData", "handle | rol");
			}
		}

		return entityList;
	}

	/**
	 * Process the resultSet and return a list of PublicIds with their roles
	 * 
	 * @param resultSet
	 *            must have the form: "publicId1, publicId2" and each
	 *            "PublicIdData" must have the form: “publicId | type”
	 * @return
	 */
	public static List<PublicId> getPublicIdsFromResultSet(String resultSet) {
		// TODO
		List<PublicId> publicIdList = new ArrayList<PublicId>();
		if (isResultSetValueValid(resultSet)) {
			List<List<String>> publicIdStructureList = getDataStructureList(resultSet);
			for (List<String> publicIdData : publicIdStructureList) {
				PublicIdDAO publicId = new PublicIdDAO();
				String publicIdValue = publicIdData.get(0);
				String type = publicIdData.get(1);
				if (isResultSetValueValid(publicIdValue) || isResultSetValueValid(type)) {
					publicId.setPublicId(publicIdValue.trim());
					publicId.setType(type.trim());
				} else {
					throw new RuntimeException("Required value not found.");
				}
				publicIdList.add(publicId);

			}
		}
		return publicIdList;
	}

	/**
	 * Split the resultSet String to a List of lists
	 * 
	 * @param resultSet
	 *            must have the form
	 *            "dataStructura1Data1|dataStructura1Data2|...,dataStructura2Data1|dataStructura2Data2|...,..."
	 * @param expectedStructureSize
	 *            the number of string that it's expected to split the string
	 * @return
	 * @throws InvalidadDataStructure
	 */
	public static List<List<String>> getDataStructureList(String resultSet, int expectedStructureSize)
			throws InvalidadDataStructure {
		List<List<String>> dataStructureList = new ArrayList<>();
		resultSet = resultSet + " ";// To add a value if the last character is a
									// pipe
		if (resultSet != null && !resultSet.trim().isEmpty()) {
			List<String> auxList = Arrays.asList(resultSet.split(","));
			for (String aux : auxList) {
				List<String> dataList = Arrays.asList(aux.split("\\|"));
				if (dataList.size() != expectedStructureSize) {
					throw new InvalidadDataStructure();
				}
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
