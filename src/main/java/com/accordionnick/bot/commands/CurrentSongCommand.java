package com.accordionnick.bot.commands;

import java.util.StringTokenizer;

import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.CommandHandler;
import com.accordionnick.bot.requests.Request;
import com.accordionnick.bot.requests.RequestManager;

public class CurrentSongCommand extends CommandHandler
{
	private RequestManager requestManager;

	public CurrentSongCommand(RequestManager requestManager)
	{
		super("current");
		this.requestManager = requestManager;
	}
	
	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		if( isAdmin(event) )
		{
			Request request = new Request(event.getUser(), tokenizedString);
			if( !requestManager.setCurrent(request) )
			{
				event.respondPrivateMessage( "Sorry, there was an error. Please try again." );
			}
			return true;
		}
		else
		{
			return false;
		}
	}

}
