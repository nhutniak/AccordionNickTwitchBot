package com.accordionnick.bot;

import java.util.HashMap;
import java.util.Map;

/**
 * This class isn't thread safe... but we don't really care.
 */
public class CommandRegister
{

	private final Map<String, CommandHandler> m_commandHandlerMap;

	public CommandRegister()
	{
		m_commandHandlerMap = new HashMap<>();
	}

	public void register(final CommandHandler commandHandler)
	{
		m_commandHandlerMap.put(commandHandler.getCommand(), commandHandler);
	}

	public boolean isRegistered(String commandName)
	{
		return m_commandHandlerMap.containsKey(commandName);
	}

	public CommandHandler lookup(String commandName)
	{
		return m_commandHandlerMap.get(commandName);
	}
}
