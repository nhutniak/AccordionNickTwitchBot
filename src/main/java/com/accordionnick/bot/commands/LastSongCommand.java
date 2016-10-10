package com.accordionnick.bot.commands;

import java.util.StringTokenizer;

import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.CommandHandler;
import com.accordionnick.bot.requests.Request;
import com.accordionnick.bot.requests.RequestManager;

public class LastSongCommand extends CommandHandler
{

	private final static String LAST_SONG_COMMAND = "song";
	private RequestManager requestManager;
	
	public LastSongCommand( RequestManager requestManager )
	{
		super( LAST_SONG_COMMAND );
		this.requestManager = requestManager;
	}
	
	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		Request lastRequest = requestManager.getLastRequest();
		
		if( null ==  lastRequest )
		{
			event.respondWith( "Sorry, I don't know what song's being played." );
		}
		else
		{
			event.respondWith( "Currently playing: " + lastRequest.value() );
		}
		return true;
	}

}
