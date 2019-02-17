/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.world.storage;

import name.martingeisse.miner.common.section.SectionDataId;

import java.util.Collection;
import java.util.Map;

/**
 * Base class for section storage implementations that are responsible
 * for actually storing section-related objects.
 */
public abstract class AbstractSectionStorage {

	/**
	 * Loads a single section-related object.
	 *
	 * @param id the ID of the object to load
	 * @return the loaded object
	 */
	public abstract byte[] loadSectionRelatedObject(SectionDataId id);

	/**
	 * Loads multiple section-related objects.
	 *
	 * @param ids the IDs of the objects to load
	 * @return the loaded objects
	 */
	public abstract Map<SectionDataId, byte[]> loadSectionRelatedObjects(Collection<? extends SectionDataId> ids);

	/**
	 * Saves a section-related object.
	 *
	 * @param id the ID of the object to save
	 * @param data the object to store
	 */
	public abstract void saveSectionRelatedObject(SectionDataId id, byte[] data);

	/**
	 * Deletes a section-related object from storage.
	 *
	 * @param id the ID of the object to delete
	 */
	public abstract void deleteSectionRelatedObject(SectionDataId id);

}
