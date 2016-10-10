package com.accordionnick.bot.commands;

import java.util.StringTokenizer;

import org.pircbotx.hooks.types.GenericMessageEvent;

import com.accordionnick.bot.Shutdown;

import me.tyler.twitchbot.TwitchBot;

import com.accordionnick.bot.CommandHandler;

public class ShutdownCommand extends CommandHandler
{

	// FIXME: should use the shutdown interface
	private Shutdown s;
	private TwitchBot m_bot;
	public ShutdownCommand( TwitchBot m_bot )
	{
		super("shutdown");
		this.m_bot = m_bot;
	}
	@Override
	public boolean process(StringTokenizer tokenizedString, GenericMessageEvent event)
	{
		if( isAdmin(event) )
		{
			m_bot.close();
			m_bot.stopBotReconnect();
			m_bot.quit();
			
			return true;
		}
		else
		{
			return false;
		}
	}

}
