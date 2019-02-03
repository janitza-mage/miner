/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.s2c.response;

import io.netty.buffer.ByteBuf;
import name.martingeisse.miner.common.network.MessageDecodingException;

/**
 *
 */
public final class CreatePlayerResponse extends Response {

	private final LoginResponse.Element playerData;

	public CreatePlayerResponse(LoginResponse.Element playerData) {
		this.playerData = playerData;
	}

	public LoginResponse.Element getPlayerData() {
		return playerData;
	}

	@Override
	protected int getExpectedBodySize() {
		return -1;
	}

	@Override
	protected void encodeBody(ByteBuf buffer) {
		playerData.encode(buffer);
	}

	public static CreatePlayerResponse decodeBody(ByteBuf buffer) throws MessageDecodingException {
		return new CreatePlayerResponse(LoginResponse.Element.decode(buffer));
	}

}
