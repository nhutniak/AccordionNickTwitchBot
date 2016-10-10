package com.accordionnick.bot.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.accordionnick.bot.requests.Request;

public class PlaylistManager
{
	private final Queue<Request> m_playlist = new ConcurrentLinkedQueue<>();
	
	public int add( final Request request )
	{
		m_playlist.add(request);
		
		return m_playlist.size();
	}
	
	public Request next()
	{
		return m_playlist.poll();
	}
	
	public List<Request> all()
	{
		List<Request> requests = new ArrayList<>();
		for (Request request : m_playlist)
		{
			requests.add(request);
		}
		return Collections.unmodifiableList(requests);
	}
	
	public boolean hasRequest()
	{
		return !m_playlist.isEmpty();
	}
}
