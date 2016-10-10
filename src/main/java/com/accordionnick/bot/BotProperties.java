package com.accordionnick.bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BotProperties
{

	public Properties getProperties() throws IOException
	{
		Properties properties = new Properties();
		InputStream is = null;
		try
		{
			is = this.getClass().getResourceAsStream("channel.properties");

			properties.load(is);
		} catch (IOException ex)
		{
			throw ex;
		} finally
		{
			if (null != is)
			{
				try
				{
					is.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return properties;
	}
}
