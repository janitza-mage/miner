/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.util.gui.control;

import name.martingeisse.miner.client.util.gui.GuiElement;
import name.martingeisse.miner.client.util.gui.element.AbstractListElement;
import name.martingeisse.miner.client.util.gui.element.VerticalLayout;

import java.util.List;
import java.util.function.Supplier;

/**
 *
 */
public abstract class ListView<T> extends Control {

	private final Supplier<List<T>> dataProvider;

	public ListView(Supplier<List<T>> dataProvider) {
		this(new VerticalLayout(), dataProvider);
	}

	public ListView(AbstractListElement listElement, Supplier<List<T>> dataProvider) {
		this.dataProvider = dataProvider;
		setControlRootElement(listElement);
		update();
	}

	public AbstractListElement getListElement() {
		return (AbstractListElement) getControlRootElement();
	}

	public void update() {
		AbstractListElement listElement = getListElement();
		listElement.clearElements();
		for (T dataElement : dataProvider.get()) {
			listElement.addElement(createGuiElement(dataElement));
		}
	}

	protected abstract GuiElement createGuiElement(T dataElement);

}
