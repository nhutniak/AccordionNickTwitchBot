package com.accordionnick.bot.commands;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.CommandHandler;

public class AddCommand extends CommandHandler
{
	private static final Logger m_log = Logger.getLogger(AddCommand.class);

	public AddCommand()
	{
		super("add");
	}

	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		if( isAdmin(event) )
		{
			String assembledString = constructStringFrom(tokenizedString);
			m_log.info("Received request to add command: " + assembledString);
	
			event.respond("Processed request for command..." + assembledString);
			return true;
		}
		else
		{
			return false;
		}
	}

}
