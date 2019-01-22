/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.network;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * Creates a handler pipeline for newly connected clients.
 */
public abstract class ProtocolPipelineFactory implements ChannelPipelineFactory {

	@Override
	public ChannelPipeline getPipeline() {
		ChannelHandler frameCodec = MessageCodec.createFrameCodec();
		ChannelHandler packetCodec = new MessageCodec();
		ProtocolEndpoint protocolEndpoint = createProtocolEndpoint();
		ChannelHandler applicationHandler = protocolEndpoint.createNettyHandler();
		return Channels.pipeline(frameCodec, packetCodec, applicationHandler);
	}

	protected abstract ProtocolEndpoint createProtocolEndpoint();

}
