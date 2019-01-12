/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.message;

/**
 *
 */
public class MessageDecodingException extends Exception {

	public MessageDecodingException() {
	}

	public MessageDecodingException(String message) {
		super(message);
	}

	public MessageDecodingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MessageDecodingException(Throwable cause) {
		super(cause);
	}

}
