/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.game;

import name.martingeisse.miner.common.network.c2s.request.CreatePlayerRequest;
import name.martingeisse.miner.common.network.c2s.request.DeletePlayerRequest;
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;
import name.martingeisse.miner.common.util.UserVisibleMessageException;

/**
 *
 */
public final class UserAccount {

	private final long id;

	public UserAccount(long id) {
		this.id = id;
	}

	public LoginResponse getLoginResponse() {
		throw new UserVisibleMessageException("NOT IMPLEMENTED");
	}

	public void createPlayer(CreatePlayerRequest request) {
		throw new UserVisibleMessageException("NOT IMPLEMENTED");
	}

	public void deletePlayer(DeletePlayerRequest request) {
		throw new UserVisibleMessageException("NOT IMPLEMENTED");
	}

}
