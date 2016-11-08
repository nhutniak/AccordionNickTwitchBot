package com.accordionnick.bot.requests;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.pircbotx.User;

public class Request
{
	private final static Logger m_logger =  Logger.getLogger(Request.class); 
	
	private final String username;
	
	private final String originalValue;

	private final String songName;
	
	private final int id;
	
	public Request( int id, String user, String value )
	{
		this.id = id;
		this.username = user;
		this.originalValue = value;
		
		this.songName = extractSongname( value.trim() );

		if( m_logger.isDebugEnabled() )
		{
			m_logger.debug("Request songname: " + songName);
		}
	}
	
	private String extractSongname(String value)
	{
		if( value.contains("musescore.com") )
		{
			try
			{
				Document doc = Jsoup.connect(value).get();
				Elements select = doc.select("meta[name=\"twitter:title\"]");
				
				return select.attr("content");
			} catch (IOException e)
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	public Request( int id, Request r )
	{
		this( id, r.username, r.originalValue );
	}
	
	public Request(User user, StringTokenizer tokenizedString)
	{
		this(-1, user.getNick(), getTokenized(tokenizedString).toString());
	}

	private static StringBuilder getTokenized(StringTokenizer tokenizedString)
	{
		StringBuilder value = new StringBuilder();
		
		while( tokenizedString.hasMoreTokens() )
		{
			value.append(tokenizedString.nextToken());
			value.append(" ");
		}
		return value;
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
		return null != songName ? songName : originalValue;
	}
	
	public boolean hasSongName()
	{
		return null != songName;
	}
	
	public String getUrlAsString()
	{
		return originalValue;
	}
	
	public boolean isValid()
	{
		return originalValue.replaceAll(" ", "").length() > 0;
	}

	public int getId()
	{
		return id;
	}

}
