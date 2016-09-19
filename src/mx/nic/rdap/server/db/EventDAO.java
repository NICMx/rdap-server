package mx.nic.rdap.server.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mx.nic.rdap.core.db.Event;

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
	public EventDAO(ResultSet resultSet) {
		super();
		try {
			this.loadFromDatabase(resultSet);
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
	public void loadFromDatabase(ResultSet resultSet) throws SQLException {
		if (resultSet.wasNull())
			return;
		this.setId(resultSet.getLong("eve_id"));
		// this.setEventAction(resultSet.getLong("eve_action"));
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
		// preparedStatement.setString(1, this.getEventAction());
		preparedStatement.setString(2, this.getEventActor());
		preparedStatement.setString(3, this.getEventDate());

	}

}
