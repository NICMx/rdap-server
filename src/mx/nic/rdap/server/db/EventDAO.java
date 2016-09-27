package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import mx.nic.rdap.core.db.Event;
import mx.nic.rdap.server.renderer.json.JsonParser;
import mx.nic.rdap.server.renderer.json.JsonUtil;
import mx.nix.rdap.core.catalog.EventAction;

/**
 * DAO for the Event object.This data structure represents events that have
 * occurred on an instance of an object class
 * 
 * @author dalpuche
 *
 */
public class EventDAO extends Event implements DatabaseObject, JsonParser {

	/**
	 * Constructor Default
	 */
	public EventDAO() {
		super();
	}

	/**
	 * @param resultSet
	 * @throws SQLException
	 */
	public EventDAO(ResultSet resultSet) throws SQLException {
		super();
		this.loadFromDatabase(resultSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		this.setId(resultSet.getLong("eve_id"));
		this.setEventAction(EventAction.getById(resultSet.getInt("eac_id")));
		this.setEventActor(resultSet.getString("eve_actor"));
		this.setEventDate(resultSet.getTimestamp("eve_date"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		preparedStatement.setLong(1, this.getEventAction().getId());
		preparedStatement.setString(2, this.getEventActor());
		preparedStatement.setTimestamp(3, new Timestamp(this.getEventDate().getTime()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.server.renderer.JsonParser#toJson()
	 */
	@Override
	public JsonObject toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("eventAction", this.getEventAction().getValue());
		if (this.getEventActor() != null && !this.getEventActor().isEmpty())
			builder.add("eventActor", this.getEventActor());
		builder.add("eventDate", this.getEventDate().toInstant().toString());
		if (this.getLinks() != null && !this.getLinks().isEmpty())
			builder.add("links", JsonUtil.getLinksJson(this.getLinks()));
		return builder.build();
	}

	public String toString() {
		return toJson().toString();
	}
}
