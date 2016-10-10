package com.accordionnick.bot.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.StringTokenizer;

import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.CommandHandler;

public class DropDatabase extends CommandHandler
{
	private static final String DROPDB_COMMAND = "dropdb";
	private Connection conn;

	public DropDatabase(Connection conn)
	{
		super(DROPDB_COMMAND);
		this.conn = conn;
	}
	
	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		try
		{
			if( isAdmin(event) )
			{
				PreparedStatement prepareStatement = conn.prepareStatement("drop table request");
				prepareStatement.executeUpdate();
				
				event.respondWith("I hope you meant to do that...");
				return true;
			}
			else
			{
				noAccessToCommand(event);
				return false;
			}
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
