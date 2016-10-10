package com.accordionnick.bot.channel;

import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.models.Channel;

public class ChannelHandler implements ChannelResponseHandler
{
	@Override
	public void onFailure(Throwable throwable)
	{
		System.out.println("Failed connecting to channel:  " + throwable.getMessage());
	}

	@Override
	public void onFailure(int statusCode, String statusMessage, String errorMessage)
	{
		System.out.println("Failed connecting to channel:  " + statusCode + " Message: " + statusMessage
				+ " errorMessage: " + errorMessage);
	}

	@Override
	public void onSuccess(Channel channel)
	{
		System.out.println("Success for channel: " + channel);
	}
}
