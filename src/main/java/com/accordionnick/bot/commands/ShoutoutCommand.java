package com.accordionnick.bot.commands;

import java.util.StringTokenizer;

import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.CommandHandler;

public class ShoutoutCommand extends CommandHandler
{
	public ShoutoutCommand()
	{
		super("so");
	}
	
	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		// TODO we should try to do this eventually... 
		return false;
	}

}
