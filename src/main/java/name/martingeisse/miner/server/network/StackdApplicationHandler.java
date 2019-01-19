/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.network;

import name.martingeisse.miner.common.network.StackdPacket;
import name.martingeisse.miner.common.network.message.s2c.Hello;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;

/**
 * Netty handler for the application protocol. Any code that
 * is actually application-specific belongs into the {@link StackdServer}
 * subclass and is called by this handler.
 *
 * @param <S> the session type
 */
final class StackdApplicationHandler<S extends StackdSession> extends SimpleChannelHandler {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(StackdApplicationHandler.class);
	
	/**
	 * the server
	 */
	private final StackdServer<S> server;
	
	/**
	 * the session
	 */
	private S session = null;
	
	/**
	 * Constructor.
	 * @param server the server
	 */
	public StackdApplicationHandler(final StackdServer<S> server) {
		this.server = server;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		super.channelConnected(ctx, e);
		session = server.createSession(e.getChannel());
		session.send(new Hello(session.getId()));
		logger.info("client connected: " + session.getId());
		server.onClientConnected(session);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelHandler#channelDisconnected(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		server.internalOnClientDisconnected(session);
		super.channelDisconnected(ctx, e);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

		// premature data packets will be ignored
		if (session == null) {
			return;
		}
		
		// handle the packet
		StackdPacket packet = (StackdPacket)e.getMessage();
		if (logger.isDebugEnabled()) {
			logger.debug("server received packet " + packet.getType() + ": " + packet.readableBytesToString(10));
		}
		server.onRawPacketReceived(session, packet);
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		if (ctx.getChannel().isConnected()) {
			ctx.getChannel().disconnect();
			logger.error("unexpected exception", e.getCause());
		}
		server.internalOnClientDisconnected(session);
	}
	
}
