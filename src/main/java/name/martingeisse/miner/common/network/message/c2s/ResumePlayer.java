/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message.c2s;

import name.martingeisse.miner.common.network.message.Message;
import name.martingeisse.miner.common.network.message.MessageDecodingException;
import org.jboss.netty.buffer.ChannelBuffer;

/**
 *
 */
public final class ResumePlayer extends Message {

	public static final int MAX_TOKEN_LENGTH = 10_000;

	private final byte[] token;

	public ResumePlayer(byte[] token) {
		this(token, true);
	}

	private ResumePlayer(byte[] token, boolean copy) {
		if (token.length > MAX_TOKEN_LENGTH) {
			throw new IllegalArgumentException("token too long");
		}
		this.token = copy ? token.clone() : token;
	}

	/**
	 * Note: Copies the token to retain immutability, so don't call this method unnecessarily.
	 */
	public byte[] getToken() {
		return token.clone();
	}

	@Override
	protected int getPacketBodySize() {
		return token.length + 2;
	}

	@Override
	protected void encodeBody(ChannelBuffer buffer) {
		// Although the length may currently be derived from the packet size, the packet still
		// contains the length explicitly so we can add other fields.
		buffer.writeShort(token.length);
		buffer.writeBytes(token);
	}

	public static ResumePlayer decodeBody(ChannelBuffer buffer) throws MessageDecodingException {
		int length = buffer.readShort();
		if (length < 0) {
			throw new MessageDecodingException("negative token length");
		}
		if (length > MAX_TOKEN_LENGTH) {
			throw new MessageDecodingException("token too long");
		}
		byte[] token = new byte[length];
		buffer.readBytes(token);
		return new ResumePlayer(token, false);
	}

}
