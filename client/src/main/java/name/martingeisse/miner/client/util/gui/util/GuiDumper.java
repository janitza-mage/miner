/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.util.gui.util;

import name.martingeisse.miner.client.util.gui.Gui;
import name.martingeisse.miner.client.util.gui.GuiElement;

/**
 *
 */
public final class GuiDumper {

	public static void dump(Gui gui) {
		dump(gui.getRootElement());
	}

	public static void dump(GuiElement element) {
		dump(element, 0);
	}

	public static void dump(GuiElement element, int indent) {
		indent(indent);
		System.out.println(element.getClass() + " (" + element.getAbsoluteX() + ", " + element.getAbsoluteY() + ")");
		for (GuiElement child : element.getChildren()) {
			dump(child, indent + 1);
		}
	}

	private static void indent(int indent) {
		for (int i = 0; i < indent; i++) {
			System.out.print("  ");
		}
	}

}
