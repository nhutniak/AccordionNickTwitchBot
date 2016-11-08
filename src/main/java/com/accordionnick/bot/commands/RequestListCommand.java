package com.accordionnick.bot.commands;

import java.util.List;
import java.util.StringTokenizer;

import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.CommandHandler;
import com.accordionnick.bot.requests.Request;
import com.accordionnick.bot.requests.RequestManager;

public class RequestListCommand extends CommandHandler
{

	private RequestManager requestManager;

	public RequestListCommand( RequestManager requestManager )
	{
		super("list");
		this.requestManager = requestManager;
	}

	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		StringBuilder builder = new StringBuilder();
		if( requestManager.hasRequest() )
		{
			List<Request> all = requestManager.all();
			
			for (int i = 1; i<= all.size(); i++)
			{
				Request r = all.get(i-1);
				builder.append( i );
				builder.append( ". " );
//				builder.append( r.getUser().getNick());
//				builder.append( " - " );
				builder.append( r.value() );
				builder.append("  ");
			}
			
			event.respondWith("This is the current request list: " + builder.toString());
		}
		else
		{
			event.respondWith("There aren't any requests.");
		}
		
		
		return true;
	}

}
