/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.ingame.logic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.miner.client.ingame.Ingame;
import name.martingeisse.miner.client.ingame.gui.InventoryDependentPage;
import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.logic.CraftingFormula;
import name.martingeisse.miner.common.logic.EquipmentSlot;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public final class Inventory {

	public static final Inventory INSTANCE = new Inventory();

	private ImmutableList<InventorySlot> slots = ImmutableList.of();
	private ImmutableMap<EquipmentSlot, InventorySlot> equippedItems = ImmutableMap.of();
	private ImmutableMap<CubeType, Integer> totals = ImmutableMap.of();

	public ImmutableList<InventorySlot> getSlots() {
		return slots;
	}

	public ImmutableMap<EquipmentSlot, InventorySlot> getEquippedItems() {
		return equippedItems;
	}

	public ImmutableMap<CubeType, Integer> getTotals() {
		return totals;
	}

	public int getTotal(CubeType type) {
		Integer total = totals.get(type);
		return (total == null ? 0 : total);
	}

	public void setSlots(ImmutableList<InventorySlot> slots) {
		if (slots == null) {
			throw new IllegalArgumentException("slots cannot be null");
		}
		this.slots = slots;
		updateDerived();
		if (Ingame.get().isGuiOpen() && Ingame.get().getGui().getRootElement() instanceof InventoryDependentPage) {
			InventoryDependentPage page = (InventoryDependentPage) Ingame.get().getGui().getRootElement();
			page.onInventoryChanged();
		}
	}

	void updateDerived() {
		Map<EquipmentSlot, InventorySlot> equippedMap = new HashMap<>();
		Map<CubeType, Integer> totalsMap = new HashMap<>();
		for (InventorySlot slot : slots) {
			if (slot.isEquipped()) {
				equippedMap.put(slot.getType().getEquipmentSlot(), slot);
			}
			int quantity = slot.getQuantity();
			totalsMap.compute(slot.getType(), (ignored, total) -> total == null ? quantity : (total + quantity));
		}
		this.equippedItems = ImmutableMap.copyOf(equippedMap);
		this.totals = ImmutableMap.copyOf(totalsMap);
	}

	public int getPossibleApplications(CraftingFormula formula) {
		int applications = Integer.MAX_VALUE;
		for (Map.Entry<CubeType, Integer> bomEntry : formula.getBillOfMaterials().entrySet()) {
			applications = Math.min(applications, getTotal(bomEntry.getKey()) / bomEntry.getValue());
		}
		return applications;
	}

	public ImmutableList<Pair<CraftingFormula, Integer>> getAllApplicableFormulas() {
		return getApplicableFormulas(CraftingFormula.ALL);
	}

	public ImmutableList<Pair<CraftingFormula, Integer>> getApplicableFormulasFor(CubeType craftingStation) {
		return getApplicableFormulas(craftingStation.getSupportedCraftingFormulas());
	}

	public ImmutableList<Pair<CraftingFormula, Integer>> getApplicableFormulas(ImmutableList<CraftingFormula> supportedFormulas) {
		List<Pair<CraftingFormula, Integer>> result = new ArrayList<>();
		for (CraftingFormula formula : supportedFormulas) {
			int applications = getPossibleApplications(formula);
			if (applications > 0) {
				result.add(Pair.of(formula, applications));
			}
		}
		return ImmutableList.copyOf(result);
	}

}
