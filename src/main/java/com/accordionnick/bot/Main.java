package com.accordionnick.bot;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.pircbotx.exception.IrcException;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.accordionnick.bot.channel.ChannelHandler;
import com.accordionnick.bot.commands.AddCommand;
import com.accordionnick.bot.commands.DropDatabase;
import com.accordionnick.bot.commands.LastSongCommand;
import com.accordionnick.bot.commands.PlayCommand;
import com.accordionnick.bot.commands.RequestCommand;
import com.accordionnick.bot.commands.RequestListCommand;
import com.accordionnick.bot.commands.ShutdownCommand;
import com.accordionnick.bot.requests.RequestManager;
import com.accordionnick.bot.schedule.FollowerCountTask;
import com.accordionnick.bot.schedule.ViewerCountTask;
import com.accordionnick.bot.stream.StreamHandler;
import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.resources.ChannelsResource;
import com.mb3364.twitch.api.resources.StreamsResource;

import me.tyler.twitchbot.TwitchBot;

public class Main implements Shutdown
{
	private static Logger m_log = Logger.getLogger(Main.class);
	
	private static TwitchBot m_bot;

	public static void main(String[] args) throws IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		BasicConfigurator.configure();

		Connection conn = setupDatabase();
		
		Properties properties = new BotProperties().getProperties();
		Twitch twitch = new Twitch();

		twitch.setClientId(properties.getProperty("clientID"));

		twitch.channels().get("accordionnick", new ChannelHandler());
		twitch.streams().get("accordionnick", new StreamHandler());

		RequestManager requestManager = new RequestManager( conn );
		
		CommandRegister commandRegister = new CommandRegister();
		commandRegister.register(new AddCommand());
		commandRegister.register(new RequestCommand(requestManager));
		commandRegister.register(new RequestListCommand(requestManager));
		commandRegister.register(new PlayCommand(requestManager));
		commandRegister.register(new LastSongCommand(requestManager));
		commandRegister.register(new DropDatabase(conn));
		
		m_bot = new TwitchBot(properties.getProperty("user"), properties.getProperty("auth"), "#accordionnick");
		m_bot.addListener(new MessageListener(commandRegister));
		
		commandRegister.register(new ShutdownCommand(m_bot));

		setupScheduledTasks(twitch);
		try
		{
			m_log.info("Connecting to Twitch...");
			m_bot.startBot();

		} catch (IrcException e)
		{
			e.printStackTrace();
			m_log.error("Encountered issue while running twitchbot", e);
		} finally
		{
			m_bot.close();
			m_bot.stopBotReconnect();
			m_bot.quit();
		}
		conn.close();
		m_log.info("Shutting down.");
	}

	private static void setupScheduledTasks(Twitch twitch)
	{
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(StreamsResource.class.getName(), twitch.streams());
		jobDataMap.put(ChannelsResource.class.getName(), twitch.channels());
		JobDetail viewerTask = JobBuilder.newJob(ViewerCountTask.class)
				.setJobData(jobDataMap)
				.withIdentity("viewercount", "streamstats")
				.build();
		JobDetail followerTask = JobBuilder.newJob(FollowerCountTask.class)
				.setJobData(jobDataMap)
				.withIdentity("followercount", "streamstats")
				.build();
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity("1secTrigger")
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(1)
						.repeatForever()
						)
				.build();
		Trigger followerTrigger = TriggerBuilder.newTrigger()
				.withIdentity("15secTrigger")
				.startNow()
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(15)
						.repeatForever()
						)
				.build();
		m_bot.addTask(viewerTask, trigger);
		m_bot.addTask(followerTask, followerTrigger);
	}

	private static Connection setupDatabase()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException
	{
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		Class.forName(driver).newInstance();

		String protocol = "jdbc:derby:";
		Properties props = new Properties();
		Connection conn = DriverManager.getConnection(protocol + "derbyDB;create=true", props);
		return conn;
	}

	@Override
	public void shutdown()
	{
		m_bot.stopBotReconnect();
	}
}
