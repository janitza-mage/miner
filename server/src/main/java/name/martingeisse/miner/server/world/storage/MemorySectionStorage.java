/**
 * Copyright (c) 2013 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.world.storage;

import name.martingeisse.miner.common.section.SectionDataId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Pure in-memory section storage.
 */
public final class MemorySectionStorage extends AbstractSectionStorage {

	/**
	 * the storageMap
	 */
	private final ConcurrentMap<SectionDataId, byte[]> storageMap = new ConcurrentHashMap<>();

	@Override
	public byte[] loadSectionRelatedObject(final SectionDataId id) {
		return storageMap.get(id);
	}

	@Override
	public Map<SectionDataId, byte[]> loadSectionRelatedObjects(final Collection<? extends SectionDataId> ids) {
		final Map<SectionDataId, byte[]> result = new HashMap<>();
		for (final SectionDataId id : ids) {
			result.put(id, storageMap.get(id));
		}
		return result;
	}

	@Override
	public void saveSectionRelatedObject(final SectionDataId id, final byte[] data) {
		storageMap.put(id, data);
	}

	@Override
	public void deleteSectionRelatedObject(final SectionDataId id) {
		storageMap.remove(id);
	}

}
