/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network;

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

	public final void send(Message message) {
		if (channel == null) {
			logger.error("trying to send packet while not connected");
		} else {
			channel.write(message);
		}
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
			onMessage((Message) e.getMessage());
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
