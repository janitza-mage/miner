/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.network;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.PlayerListUpdate;
import name.martingeisse.miner.common.network.s2c.SingleSectionModificationEvent;
import name.martingeisse.miner.common.section.SectionId;
import name.martingeisse.miner.server.world.WorldSubsystem;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The main server class. Typically, a single instance of this class
 * is used on the server side, and it is actually an application-specific
 * subclass of this class.
 */
public class StackdServer {

	private final ConcurrentHashMap<StackdSession, StackdSession> sessions;
	private final WorldSubsystem worldSubsystem;

	public StackdServer() {
		this.sessions = new ConcurrentHashMap<>();
		this.worldSubsystem = new WorldSubsystem();

		worldSubsystem.addListener(sectionIds -> {
			for (SectionId sectionId : sectionIds) {
				broadcast(new SingleSectionModificationEvent(sectionId));
			}
		});

		Timer timer = new Timer(true);
		timer.schedule(new AvatarUpdateSender(), 0, 200);

		// TODO for testing
		try {
			// worldSubsystem.initializeWorldWithHeightField();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * Returns a collection that contains all sessions. This collection is "live" and will be changed
	 * concurrently by other threads.
	 */
	public final Set<StackdSession> getSessions() {
		return sessions.keySet();
	}

	public WorldSubsystem getWorldSubsystem() {
		return worldSubsystem;
	}

	/**
	 * Creates a new session and returns it.
	 */
	public final StackdSession createSession(final ServerEndpoint endpoint) {
		final StackdSession session = new StackdSession(this, endpoint);
		sessions.put(session, session);
		return session;
	}

	/**
	 * This method is called when a client channel has been disconnected.
	 */
	final void removeSession(final StackdSession session) {
		if (session != null) {
			sessions.remove(session);
		}
	}

	/**
	 * Sends the specified message to all clients.
	 */
	public final void broadcast(final Message message) {
		for (final StackdSession session : sessions.keySet()) {
			session.send(message);
		}
	}

	/**
	 * Sends updates for position, orientation and name of all avatars to all clients.
	 */
	private class AvatarUpdateSender extends TimerTask {

		@Override
		public void run() {
			for (final StackdSession recipientSession : sessions.keySet()) {
				List<PlayerListUpdate.Element> elements = new ArrayList<>();
				for (StackdSession avatarSession : getSessions()) {
					if (avatarSession != recipientSession) {
						Avatar avatar = avatarSession.getAvatar();
						if (avatar != null) {
							elements.add(new PlayerListUpdate.Element(avatar.getPosition(), avatar.getOrientation(), avatar.getName()));
						}
					}
				}
				if (elements.size() > 0) {
					recipientSession.send(new PlayerListUpdate(ImmutableList.copyOf(elements)));
				}
			}
		}

	}

}
