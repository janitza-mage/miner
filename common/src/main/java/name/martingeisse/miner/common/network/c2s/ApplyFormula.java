/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s;

import name.martingeisse.miner.common.logic.CraftingFormula;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.MessageDecodingException;

import java.nio.ByteBuffer;

/**
 *
 */
public final class ApplyFormula extends Message {

	private final CraftingFormula formula;

	public ApplyFormula(CraftingFormula formula) {
		this.formula = formula;
	}

	public CraftingFormula getFormula() {
		return formula;
	}

	@Override
	protected int getExpectedBodySize() {
		return 4;
	}

	@Override
	protected void encodeBody(ByteBuffer buffer) {
		buffer.putInt(formula.ordinal());
	}

	public static ApplyFormula decodeBody(ByteBuffer buffer) throws MessageDecodingException {
		return new ApplyFormula(CraftingFormula.ALL.get(buffer.getInt()));
	}

}
