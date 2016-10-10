package com.accordionnick.bot.schedule;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.resources.ChannelsResource;

public class FollowerCountTask implements Job
{
	private static final Logger m_log = Logger.getLogger(FollowerCountTask.class);
	
	public static final String FOLLOWER_FILE_NAME = "followers.txt";
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		ChannelsResource resource =  (ChannelsResource) context.getJobDetail().getJobDataMap().get(ChannelsResource.class.getName());
		
		if( null != resource )
		{
			ChannelResponseHandler handler = new ChannelResponseHandler()
			{
				
				@Override
				public void onFailure(int statusCode, String statusMessage, String errorMessage)
				{
					m_log.error("Failure: [" + statusCode + "] " + statusMessage + " | " + errorMessage);
				}
	
				@Override
				public void onFailure(Throwable throwable)
				{
					m_log.error("Failure", throwable);
				}
	
				@Override
				public void onSuccess(Channel channel)
				{
					String followers;
					if (null == channel)
					{
						m_log.info("Channel returned null!");
						followers = "Offline";
					}
					else
					{
						m_log.debug("Stream currently has followers: " + channel.getFollowers());
						followers = Integer.toString( channel.getFollowers() );
					}
					
					try
					{
						PrintWriter writer = new PrintWriter(FOLLOWER_FILE_NAME, "UTF-8");
						writer.print(followers);
						writer.close();
					} catch (FileNotFoundException | UnsupportedEncodingException e)
					{
						m_log.error("Couldn't write follower count to file", e);
					}
				}
			};
			resource.get("accordionnick", handler);
		}
		m_log.debug("Scheduled task ran.");
	}

}
