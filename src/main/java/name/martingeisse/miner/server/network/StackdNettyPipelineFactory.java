/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.network;

import name.martingeisse.miner.common.network.StackdPacketCodec;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;

/**
 * Creates a handler pipeline for newly connected clients.
 */
public class StackdNettyPipelineFactory implements ChannelPipelineFactory {

	/**
	 * the server
	 */
	private final StackdServer server;
	
	/**
	 * Constructor.
	 * @param server the application server
	 */
	public StackdNettyPipelineFactory(StackdServer server) {
		this.server = server;
	}


	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() {
		ChannelHandler frameCodec = StackdPacketCodec.createFrameCodec();
		ChannelHandler packetCodec = new StackdPacketCodec();
		ChannelHandler applicationHandler = new StackdApplicationHandler(server);
		return Channels.pipeline(frameCodec, packetCodec, applicationHandler);
	}
	
}
