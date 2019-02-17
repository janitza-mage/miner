/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.gui.element;

import name.martingeisse.miner.client.util.gui.Gui;

/**
 * This interface is implemented by GUI elements that can have input focus.
 */
public interface IFocusableElement {

	/**
	 * Notifies this element about whether it is now focused. This is
	 * used by {@link Gui#setFocus(IFocusableElement)}
	 * and should not be called by other elements directly.
	 *
	 * @param focused true if focused, false if not
	 */
	public void notifyFocus(boolean focused);

}
