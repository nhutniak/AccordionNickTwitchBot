package com.accordionnick.bot.customcommand;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import com.accordionnick.bot.database.Database;

/**
 * TODO: instantiate this class and use it.
 */
public class CommandManager extends Database
{
	public CommandManager(Connection conn) throws SQLException
	{
		super( conn, "command" );
		
		configureDB();
		loadRequests();
	}
	
	private void configureDB() throws SQLException
	{
		Set<String> dbTables = getDBTables(m_dbConnection);
		
		if( !dbTables.contains( TABLE_NAME ) )
		{
			// Create the table
			Statement createStatement = m_dbConnection.createStatement();
			String sql = "CREATE TABLE " + TABLE_NAME +
					" ( " +
					"  command_id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
					"  command_name varchar(128)," +
					"  text varchar(256)," +
					"  state varchar(32) " +
					"  )";
			createStatement.executeUpdate( sql );
		}
	}

	private void loadRequests() throws SQLException
	{
		Statement createStatement = m_dbConnection.createStatement();
		ResultSet executeQuery = createStatement.executeQuery( "SELECT * FROM " + TABLE_NAME + " WHERE state='REQUEST'" );
		
		while( executeQuery.next() )
		{
			int commandID = executeQuery.getInt("command_id");
			String commandName = executeQuery.getString("command_name");
			String text = executeQuery.getString("text");
			String stateString = executeQuery.getString("state");
//			State state = State.valueOf(stateString);
			
//			m_requestQueue.add( new Request(requestID, userName, songName) );
		}
	}
}
