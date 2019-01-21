/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.protocol;

import name.martingeisse.miner.common.network.message.Message;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;

/**
 *
 */
public abstract class ProtocolEndpoint {

	private static Logger logger = Logger.getLogger(ProtocolEndpoint.class);

	private Channel channel;

	SimpleChannelHandler createNettyHandler() {
		return new NettyHandler();
	}

	protected abstract void onConnect();
	protected abstract void onDisconnect();
	protected abstract void onMessage(Message message);

	protected void onDisconnectAfterException(Throwable t) {
		onDisconnect();
	}

	/**
	 * Sends a network packet to the opposite endpoint. The packet object should be considered invalid afterwards
	 * (hence "destructive") since this method will assemble header fields in the packet and alter its reader/writer
	 * index, possibly asynchronous to the calling thread.
	 */
	public final void sendPacketDestructive(StackdPacket packet) {
		if (logger.isDebugEnabled()) {
			logger.debug(getClass().getSimpleName() + " is going to send packet " + packet.getType() + ": " + packet.readableBytesToString(10));
		}
		if (channel == null) {
			logger.error("trying to send packet while not connected");
		} else {
			channel.write(packet);
		}
	}

	public final void send(Message message) {
		sendPacketDestructive(message.encodePacket());
	}

	private final class NettyHandler extends SimpleChannelHandler {

		@Override
		public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			super.channelConnected(ctx, e);
			channel = e.getChannel();
			ProtocolEndpoint.this.onConnect();
		}

		@Override
		public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
			channel = null;
			ProtocolEndpoint.this.onDisconnect();
			super.channelDisconnected(ctx, e);
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
			StackdPacket packet = (StackdPacket)e.getMessage();
			if (logger.isDebugEnabled()) {
				logger.debug(getClass().getSimpleName() + " received packet " + packet.getType() + ": " + packet.readableBytesToString(10));
			}
			onMessage(Message.decodePacket(packet));
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
			if (ctx.getChannel().isConnected()) {
				ctx.getChannel().disconnect();
				logger.error(getClass().getSimpleName() + " got unexpected exception", e.getCause());
			}
			channel = null;
			ProtocolEndpoint.this.onDisconnectAfterException(e.getCause());
		}

	}

}
