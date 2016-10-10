package com.accordionnick.bot;

import java.util.StringTokenizer;

import org.pircbotx.hooks.types.GenericMessageEvent;

public abstract class CommandHandler
{

	private final String commandName;

	public CommandHandler(String commandName)
	{
		if (commandName.startsWith("!"))
		{
			this.commandName = commandName;
		} else
		{
			this.commandName = "!" + commandName;
		}
	}

	public String getCommand()
	{
		return commandName;
	}

	/**
	 * Processes the requested command
	 * 
	 * @param tokenizedString
	 *            the unprocessed tokenized string.
	 * @param event 
	 * @return if the processing was successful.
	 */
	public abstract boolean process(StringTokenizer tokenizedString, GenericMessageEvent event);
	
	protected boolean isAdmin(GenericMessageEvent event)
	{
		return event.getUser().getNick().equals("accordionnick");
	}
	
	protected void noAccessToCommand( GenericMessageEvent event)
	{
		event.respondPrivateMessage("You don't have access to " + commandName + ".");
	}
}
