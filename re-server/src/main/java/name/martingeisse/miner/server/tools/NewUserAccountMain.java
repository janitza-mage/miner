/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.tools;

import name.martingeisse.miner.server.jooq.Tables;
import name.martingeisse.miner.server.password.PasswordHashingUtil;
import name.martingeisse.miner.server.persistence.DatabaseImpl;

public class NewUserAccountMain {

	public static void main(String[] args) {

		String username = "test";
		String password = "test";

		var database = new DatabaseImpl();
		database.runVoid(dsl -> {
			var row = dsl.newRecord(Tables.userAccount);
			row.setUsername(username);
			row.setPasswordHash(PasswordHashingUtil.hashPassword(password));
			row.store();
		});
	}

}
