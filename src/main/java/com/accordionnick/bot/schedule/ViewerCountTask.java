package com.accordionnick.bot.schedule;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.Stream;
import com.mb3364.twitch.api.resources.StreamsResource;

public class ViewerCountTask implements Job
{
	private static final Logger m_log = Logger.getLogger(ViewerCountTask.class);
	
	public static final String VIEWER_FILE_NAME = "viewers.txt";
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		StreamsResource resource =  (StreamsResource) context.getJobDetail().getJobDataMap().get(StreamsResource.class.getName());
		
		if( null != resource )
		{
			StreamResponseHandler handler = new StreamResponseHandler()
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
				public void onSuccess(Stream stream)
				{
					String viewers;
					if (null == stream)
					{
						m_log.debug("Stream is offline.");
						viewers = "Offline";
					}
					else
					{
						m_log.debug("Stream currently has viewers: " + stream.getViewers());
						viewers = Integer.toString( stream.getViewers() );
					}
					
					try
					{
						PrintWriter writer = new PrintWriter(VIEWER_FILE_NAME, "UTF-8");
						writer.print(viewers);
						writer.close();
					} catch (FileNotFoundException | UnsupportedEncodingException e)
					{
						m_log.error("Couldn't write viewer count to file", e);
					}
				}
			};
			resource.get("accordionnick", handler);
		}
		m_log.debug("Scheduled task ran.");
	}

}
