package com.accordionnick.bot.requests;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.accordionnick.bot.database.Database;


public class RequestManager extends Database
{
	enum State {
		REQUEST,
		PLAYED,
		CANCELLED;
	}

	private final Queue<Request> m_requestQueue = new ConcurrentLinkedQueue<>();
	
	private Request m_lastRequest;
	
	private boolean m_enabled = true;
	

	public RequestManager(Connection conn) throws SQLException
	{
		super( conn,  "request" );

		configureDB();
		loadRequests();
	}

	public int add(Request request)
	{
		try
		{
			PreparedStatement ps= m_dbConnection.prepareStatement("insert into " + TABLE_NAME + " (user_name, song_name, state) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, request.getUsername());
			ps.setString(2, request.value());
			ps.setString(3, State.REQUEST.toString());
			int rowsChanged = ps.executeUpdate();
			
			ResultSet generatedKeys = ps.getGeneratedKeys();
			if( 1 == rowsChanged )
			{
				generatedKeys.next();
				// Success
				int id = generatedKeys.getInt(1);
				request = new Request(id, request);
			}
			else
			{
				System.out.println("failed to insert: " + ps.toString());
			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		m_requestQueue.add(request);

		return m_requestQueue.size();
	}

	public Request next()
	{
		Request poll = m_requestQueue.poll();
		
		try
		{
			PreparedStatement ps = m_dbConnection.prepareStatement("update " + TABLE_NAME + " SET state=? WHERE request_id=?" );
			ps.setString(1, State.PLAYED.toString());
			ps.setInt(2, poll.getId());
			
			int executeUpdate = ps.executeUpdate();
			if( 1 == executeUpdate)
			{
				// Success
			}
			else
			{
				System.out.println("Failed to update next()");
			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		m_lastRequest = poll;
		return poll;
	}

	public List<Request> all()
	{
		List<Request> requests = new ArrayList<>();
		for (Request request : m_requestQueue)
		{
			requests.add(request);
		}
		return Collections.unmodifiableList(requests);
	}

	public boolean hasRequest()
	{
		return !m_requestQueue.isEmpty();
	}

	public Request getLastRequest()
	{
		return m_lastRequest;
	}
	
	public void setEnable( final boolean value )
	{
		m_enabled = value;
	}
	
	public boolean isEnabled()
	{
		return m_enabled;
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
					"  request_id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
					"  song_name varchar(128)," +
					"  user_name varchar(128)," +
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
			int requestID = executeQuery.getInt("request_id");
			String songName = executeQuery.getString("song_name");
			String userName = executeQuery.getString("user_name");
//			String stateString = executeQuery.getString("state");
//			State state = State.valueOf(stateString);
			
			m_requestQueue.add( new Request(requestID, userName, songName) );
		}
	}
}
