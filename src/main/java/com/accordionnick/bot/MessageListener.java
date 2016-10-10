package com.accordionnick.bot;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class MessageListener extends ListenerAdapter
{
	private static Logger m_log = Logger.getLogger(MessageListener.class);
	
	private static final String SPACE = " ";
	
	private CommandRegister commandRegister;

	public MessageListener(CommandRegister commandRegister)
	{
		this.commandRegister = commandRegister;
	}

	@Override
	public void onGenericMessage(GenericMessageEvent event)
	{
		String message = event.getMessage();
		StringTokenizer spaceTokenizer = new StringTokenizer(message, SPACE);
		
		String firstToken = spaceTokenizer.nextToken();
		boolean processed = false;
		if( commandRegister.isRegistered(firstToken) )
		{
			// We only need to do something if there is a command handler
			// registered.
			CommandHandler handler = commandRegister.lookup(firstToken);
			m_log.info("Command request received: " + firstToken);
			
			processed = handler.process(spaceTokenizer, event);
		}
		
		if( message.startsWith("!") && !processed ) // Check to see if this user has permission to invalid commands?
		{
			event.respond( " that isn't a valid command" );
		}
	}
}
