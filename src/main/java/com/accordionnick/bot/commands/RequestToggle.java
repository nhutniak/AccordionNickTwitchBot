package com.accordionnick.bot.commands;

import java.util.StringTokenizer;

import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.CommandHandler;
import com.accordionnick.bot.requests.RequestManager;

public class RequestToggle extends CommandHandler
{

	private static final String REQUEST_STRING = "request";
	private RequestManager requestManager;

	public RequestToggle(RequestManager requestManager)
	{
		super(REQUEST_STRING);
		this.requestManager = requestManager;
	}

	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		if( isAdmin(event) )
		{
			String setTo = tokenizedString.nextToken();
			
			boolean newValue = false;
			if( "status".equalsIgnoreCase(setTo) )
			{
				if( setTo.equalsIgnoreCase("true") || setTo.equalsIgnoreCase("on") )
				{
					newValue = true;
				}
				requestManager.setEnable( newValue );
			}
			else
			{
				newValue = requestManager.isEnabled();
			}
			String enableString = newValue ? "enabled" : "disabled";
			
			event.respondWith( "Requests are " + enableString + ".");
			
			return true;
		}
		else
		{
			return false;
		}
	}

}
