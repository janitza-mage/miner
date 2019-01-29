/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.server.world;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.section.SectionId;

/**
 *
 */
public interface WorldModificationListener {

	void onSectionsModified(ImmutableList<SectionId> sectionIds);

}
