/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.util;

/**
 * This exception type signals an error message that should be displayed to the user.
 */
public class UserVisibleMessageException extends RuntimeException {

	/**
	 * Constructor.
	 * @param message the exception message
	 */
	public UserVisibleMessageException(String message) {
		super(message);
	}

}
