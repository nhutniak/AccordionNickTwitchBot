package com.accordionnick.bot.requests;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

import org.apache.log4j.Logger;

import com.accordionnick.bot.database.Database;


public class RequestManager extends Database
{
	enum State {
		REQUEST,
		PLAYED,
		CANCELLED,
		SET_CURRENT,
		;
	}

	private static final String REQUEST_FILE_NAME = "requests.txt";
	
	private static final String CURRENT_SONG = "currentsong.txt";

	private static final Logger m_log = Logger.getLogger(RequestManager.class);

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
		updateFiles(m_requestQueue);

		return m_requestQueue.size();
	}

	public boolean setCurrent( Request request )
	{
		try
		{
			PreparedStatement ps= m_dbConnection.prepareStatement("insert into " + TABLE_NAME + " (user_name, song_name, state) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, request.getUsername());
			ps.setString(2, request.value());
			ps.setString(3, State.SET_CURRENT.toString());
			int rowsChanged = ps.executeUpdate();
			
			if( 1 != rowsChanged )
			{
				m_log.error("Failed to insert: " + ps.toString());
			}
		} catch (SQLException e)
		{
			m_log.error("Failed to set current song", e);
			return false;
		}
		saveCurrentSong( request.value() );
		
		return true;
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
		updateFiles( m_requestQueue );
		
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
	
	private void updateFiles(Queue<Request> requestQueue)
	{
		saveRequests(requestQueue);
		saveCurrentSong( null == m_lastRequest ? null : m_lastRequest.value() );
	}
	
	private void saveRequests(Queue<Request> requestQueue)
	{
		try
		{
			PrintWriter writer = new PrintWriter(REQUEST_FILE_NAME, "UTF-8");
			int count=1;
			if( requestQueue.isEmpty() )
			{
				writer.println( "No requests.");
			}
			writer.println( "Use !req <song name or a link to sheets> to request a song!" );
			for (Request request : requestQueue)
			{
				writer.println( count + " - " + request.value() );
				count++;
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			m_log.error("Couldn't write requests to file", e);
		}
	}
	
	
	private void saveCurrentSong( String lastRequest )
	{
		try
		{
			PrintWriter writer = new PrintWriter(CURRENT_SONG, "UTF-8");
			if( null == lastRequest )
			{
				writer.println( "" );
			}
			else
			{
				writer.println( "Currently playing: " + lastRequest );
			}
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e)
		{
			m_log.error("Couldn't write current song to file", e);
		}
		
	}
}
