/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.BufferUtil;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 * TODO encrypt the stream OR this message OR authenticate using challenge-response, but DON'T send unencrypted
 * passwords!
 *
 *
 */
public final class LoginRequest extends Message {

	private final String username;
	private final String password;

	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	protected int getExpectedBodySize() {
		return -1;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
		BufferUtil.encodeString(username, buffer);
		BufferUtil.encodeString(password, buffer);
	}

	public static LoginRequest decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new LoginRequest(BufferUtil.decodeString(buffer), BufferUtil.decodeString(buffer));
	}

}

