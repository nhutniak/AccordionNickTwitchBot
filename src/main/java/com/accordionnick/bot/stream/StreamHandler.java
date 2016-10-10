package com.accordionnick.bot.stream;

import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Stream;

public class StreamHandler implements StreamResponseHandler
{
	final public static String STREAM_HANDLER = "StreamHandler";
	
	@Override
	public void onFailure(int statusCode, String statusMessage, String errorMessage)
	{
	}

	@Override
	public void onFailure(Throwable throwable)
	{
	}

	@Override
	public void onSuccess(Stream stream)
	{
		if (null == stream)
		{
			System.out.println("Stream is offline.");
		} else
		{
			System.out.println("Stream currently has viewers: " + stream.getViewers());
		}
	}
}
