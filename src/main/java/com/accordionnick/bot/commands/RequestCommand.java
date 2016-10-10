package com.accordionnick.bot.commands;

import java.util.StringTokenizer;

import org.pircbotx.User;
import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.CommandHandler;
import com.accordionnick.bot.requests.Request;
import com.accordionnick.bot.requests.RequestManager;

public class RequestCommand extends CommandHandler
{

	private static final String REQUEST_STRING = "req";
	private RequestManager requestManager;

	public RequestCommand(RequestManager requestManager)
	{
		super(REQUEST_STRING);
		this.requestManager = requestManager;
	}

	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		if( requestManager.isEnabled() )
		{
			User user = event.getUser();
			
			Request request = new Request( user, tokenizedString );
			if( request.isValid() )
			{
				int position = requestManager.add(request);
				
				event.respond("Your request has been received. It's " + position + " in the queue.  Stick around and I'll play it soon for you!");
			}
			else
			{
				event.respond("Sorry, your request isn't valid.  Use !" + REQUEST_STRING + " <song name or url to sheets>");
			}
		}
		else
		{
			event.respond("Sorry, requests are disabled right now.");
		}
		return true;
	}

}
