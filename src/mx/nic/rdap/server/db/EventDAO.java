package mx.nic.rdap.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.Event;
import mx.nix.rdap.core.catalog.EventAction;

/**
 * DAO for the Event object.This data structure represents events that have
 * occurred on an instance of an object class
 * 
 * @author dalpuche
 *
 */
public class EventDAO extends Event implements DatabaseObject {

	/**
	 * Constructor Default
	 */
	public EventDAO() {
		super();
	}

	/**
	 * @param resultSet
	 */
	public EventDAO(ResultSet resultSet,Connection connection) {
		super();
		try {
			this.loadFromDatabase(resultSet,connection);
		} catch (SQLException e) {
			// TODO Manage the exception
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * mx.nic.rdap.core.db.DatabaseObject#loadFromDatabase(java.sql.ResultSet)
	 */
	@Override
	public void loadFromDatabase(ResultSet resultSet,Connection connection) throws SQLException {
		if (resultSet.wasNull())
			return;
		this.setId(resultSet.getLong("eve_id"));
		this.setEventAction(EventAction.getById(Integer.parseInt(resultSet.getString("eve_action"))));
		this.setEventActor(resultSet.getString("eve_actor"));
		this.setEventDate(resultSet.getString("eve_date"));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see mx.nic.rdap.core.db.DatabaseObject#storeToDatabase(java.sql.
	 * PreparedStatement)
	 */
	@Override
	public void storeToDatabase(PreparedStatement preparedStatement) throws SQLException {
		 preparedStatement.setString(1, Integer.toString(this.getEventAction().getId()));
		preparedStatement.setString(2, this.getEventActor());
		preparedStatement.setString(3, this.getEventDate());

	}

}
