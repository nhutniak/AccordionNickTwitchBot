package com.accordionnick.bot.requests;

import java.util.StringTokenizer;

import org.pircbotx.User;

public class Request
{

	private final String username;
//	private final StringTokenizer tokenizedString;
	private final String value;

	private final int id;
	
	public Request( int id, Request r )
	{
		this.id = id;
		this.username = r.username;
		this.value = r.value;
	}
	
	public Request( int id, String user, String value)
	{
		this.id = id;
		this.username = user;
		this.value = value;
	}
	
	public Request(User user, StringTokenizer tokenizedString)
	{
		this.id = -1;
		this.username = user.getNick();
//		this.tokenizedString = tokenizedString;
		StringBuilder value = new StringBuilder();
		
		while( tokenizedString.hasMoreTokens() )
		{
			value.append(tokenizedString.nextToken());
			value.append(" ");
		}
		
		this.value = value.toString();
	}

	public String getUsername()
	{
		return username;
	}

//	public StringTokenizer getTokenizedString()
//	{
//		return tokenizedString;
//	}

	public String value()
	{
		return value;
	}
	
	public boolean isValid()
	{
		return value.replaceAll(" ", "").length() > 0;
	}

	public int getId()
	{
		return id;
	}

}
