/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.game;

import external.BCrypt;
import name.martingeisse.api.handler.jsonapi.JsonApiException;
import name.martingeisse.miner.server.Databases;
import name.martingeisse.miner.server.postgres_entities.QUserAccountRow;
import name.martingeisse.miner.server.postgres_entities.UserAccountRow;
import name.martingeisse.miner.server.util.database.postgres.PostgresConnection;

/**
 *
 */
public final class UserAccountRepository {

	public static final UserAccountRepository INSTANCE = new UserAccountRepository();

	private static final String DUMMY_PASSWORD_HASH = "$2a$12$xxyZikdRn7HxYXlOqOvaXOLmtpaXoLxFjPDQpiSYZSZNxbzeV68Xy";

	public UserAccount login(String username, String password) {

		// fetch the user record
		UserAccountRow userAccountRow;
		try (PostgresConnection connection = Databases.main.newConnection()) {
			QUserAccountRow qua = QUserAccountRow.UserAccount;
			userAccountRow = connection.query().select(qua).from(qua).where(qua.username.eq(username)).fetchOne();
		}

		// Check the password. Even if we found no user account we check against a pre-generated
		// dummy hash to produce similar timing, to prevent timing attacks.
		if (!BCrypt.checkpw(password, userAccountRow == null ? DUMMY_PASSWORD_HASH : userAccountRow.getPasswordHash())) {
			throw new JsonApiException(1, "Invalid username or password");
		}

		return new UserAccount(userAccountRow.getId());
	}

}
