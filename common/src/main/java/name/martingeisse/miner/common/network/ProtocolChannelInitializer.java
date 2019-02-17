/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

/**
 * Creates a handler pipeline for newly connected clients.
 */
public abstract class ProtocolChannelInitializer extends ChannelInitializer {

	@Override
	protected void initChannel(Channel channel) throws Exception {
		// addLast() adds a handler that gets executed last for inbound messages but *first* for outbound
		channel.pipeline().addLast(MessageCodec.createFrameDecoder());
		channel.pipeline().addLast(new MessageCodec());
		ProtocolEndpoint protocolEndpoint = createProtocolEndpoint();
		channel.pipeline().addLast(protocolEndpoint.createNettyHandler());
	}

	protected abstract ProtocolEndpoint createProtocolEndpoint();

}
