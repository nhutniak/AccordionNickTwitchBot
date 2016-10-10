package com.accordionnick.bot.commands;

import java.util.StringTokenizer;

import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.CommandHandler;
import com.accordionnick.bot.requests.Request;
import com.accordionnick.bot.requests.RequestManager;

public class PlayCommand extends CommandHandler
{

	private static final String PLAY_COMMAND = "playnext";
	private RequestManager requestManager;
	
	public PlayCommand(RequestManager requestManager)
	{
		super(PLAY_COMMAND);
		this.requestManager = requestManager;
	}
	
	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		if( isAdmin(event) )
		{
			if( requestManager.hasRequest() )
			{
				Request nextRequest = requestManager.next();
				
				event.respondWith( nextRequest.getUsername() + " your request for " + nextRequest.value() + " is coming up next." );
			}
			else
			{
				event.respondWith( "There are no requests." );
			}
			return true;
		}
		else
		{
			return false;
		}
	}

}
