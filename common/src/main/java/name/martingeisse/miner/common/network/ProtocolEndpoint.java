/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

/**
 *
 */
public abstract class ProtocolEndpoint {

	private static Logger logger = Logger.getLogger(ProtocolEndpoint.class);

	private Channel channel;

	ChannelHandler createNettyHandler() {
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
			// Note: exceptionCaught() is only called for inbound messages. For outbound messages, we have
			// to add a listener explicitly.
			channel.writeAndFlush(message).addListener(future -> {
				if (!future.isSuccess()) {
					logger.error("exception while sending network message", future.cause());
				}
			});

		}
	}

	private final class NettyHandler extends ChannelInboundHandlerAdapter {

		@Override
		public void channelActive(ChannelHandlerContext context) throws Exception {
			super.channelActive(context);
			channel = context.channel();
			ProtocolEndpoint.this.onConnect();
		}

		@Override
		public void channelInactive(ChannelHandlerContext context) throws Exception {
			channel = null;
			ProtocolEndpoint.this.onDisconnect();
			super.channelInactive(context);
		}

		@Override
		public void channelRead(ChannelHandlerContext context, Object payload) throws Exception {
			onMessage((Message) payload);
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
			logger.error("unexpected exception in Netty thread", cause);
			if (context.channel().isActive()) {
				context.channel().disconnect();
				logger.error(getClass().getSimpleName() + " got unexpected exception", cause);
			}
			channel = null;
			ProtocolEndpoint.this.onDisconnectAfterException(cause);
		}

	}

}
