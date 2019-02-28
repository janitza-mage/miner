package name.martingeisse.miner.common.logic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.miner.common.cubetype.CubeType;
import name.martingeisse.miner.common.cubetype.CubeTypes;

/**
 *
 */
public enum CraftingFormula {

	MELT_SNOW(ImmutableMap.of(CubeTypes.CUBE_TYPES[78], 2), CubeTypes.CUBE_TYPES[9]),
	CUT_WOOD(ImmutableMap.of(CubeTypes.CUBE_TYPES[17], 1), CubeTypes.CUBE_TYPES[5]);

	public static final ImmutableList<CraftingFormula> ALL = ImmutableList.copyOf(values());

	private final ImmutableMap<CubeType, Integer> billOfMaterials;
	private final ImmutableMap<CubeType, Integer> products;

	CraftingFormula(ImmutableMap<CubeType, Integer> billOfMaterials, ImmutableMap<CubeType, Integer> products) {
		for (int value : billOfMaterials.values()) {
			if (value < 1) {
				throw new IllegalArgumentException();
			}
		}
		this.billOfMaterials = billOfMaterials;
		this.products = products;
	}

	CraftingFormula(ImmutableMap<CubeType, Integer> billOfMaterials, CubeType productType, int productQuantity) {
		this(billOfMaterials, ImmutableMap.of(productType, productQuantity));
	}

	CraftingFormula(ImmutableMap<CubeType, Integer> billOfMaterials, CubeType productType) {
		this(billOfMaterials, productType, 1);
	}

	public ImmutableMap<CubeType, Integer> getBillOfMaterials() {
		return billOfMaterials;
	}

	public ImmutableMap<CubeType, Integer> getProducts() {
		return products;
	}

}
