/**
 * Copyright (c) 2010 Martin Geisse
 *
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.world;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import name.martingeisse.miner.common.Constants;
import name.martingeisse.miner.common.cubes.Cubes;
import name.martingeisse.miner.common.cubes.UniformCubes;
import name.martingeisse.miner.common.section.SectionDataId;
import name.martingeisse.miner.server.world.entry.InteractiveSectionImageCacheEntry;
import name.martingeisse.miner.server.world.entry.SectionCubesCacheEntry;
import name.martingeisse.miner.server.world.entry.SectionDataCacheEntry;
import name.martingeisse.miner.server.world.storage.AbstractSectionStorage;
import org.apache.commons.collections.iterators.ArrayIterator;
import org.apache.commons.lang3.NotImplementedException;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * This class is the front-end to section storage. It maintains a cache of recently
 * used section-related objects as well as an {@link AbstractSectionStorage} in the
 * background that handles actual storage of the sections.
 * 
 * Note that since objects are just cached, not permanently stored, fetching an
 * object may return a different object than before. This implies that code should
 * not hold on an old instance to avoid concurrent modification on two different
 * section objects, with save operations overwriting each other's changes.
 * 
 * The cache keeps section-related data objects using their {@link SectionDataId}.
 * Each entry is the actual data, wrapped in a {@link SectionDataCacheEntry}
 * subclass instance.
 * 
 * TODO save on evict
 *
 * TODO read-modify-write is not totally thread safe
 *
 * TODO should this class or the enclosing WorldSubsystem handle modification listeners?
 * - if the WorldSubsystem handles listeners then it's ugly, logic is separated. Thread safety is also
 *   hard to achieve. (but the latter could be achieved by modifying world data, *then* sending a notification
 *   to all listeners).
 * - related to how read-modify-write is implemented
 *
 * Solution: What I need is a cache that locks a cache key on get and demands an explicit unlock. Simple reads in
 * response to a modification listener may get an arbitrary state since they are not thread-safe anyway, but anything
 * that modifies a cache entry has to make the read-modify-write atomic.
 *
 * Step 1: Design the interface of this class around that.
 * - non-synchronized reads should not be able to modify the section -> do not allow that
 * - synchronized read-modify-write should use a callback or explicit unlock (decide which one or both)
 * - should modification listeners be part of this class?
 *   Probably. Otherwise each modification logic in WorldSubsystem would have to notify listeners
 *   (prone to errors)
 * - are accumulating multi-section read jobs part of this class? Note that Cassandra supports async
 *   loading but Guava cache does not. Cassandra would finish an async load in its own thread but allows
 *   to specify an executor (e.g. the TaskSystem) for that.
 *   CON: outside logic would know when a "fast lane read" is necessary, bypassing the accumulating read
 *   PRO: read-modify-write cycles could use the same logic
 *   CON: this is hardly necessary, since modifications typically affect sections that are already cached since the
 *        modifications are caused by a player who is in those sections
 *   CON: such logic simply does not belong there
 *
 * Step 2: Consider building that around a LoadingCache, e.g. with an additional save queue and extra synchronization
 * purely around modifications (most accesses will be non-synchronized reads!)
 *
 *
 */
public final class SectionWorkingSet {

	/**
	 * the storage
	 */
	private final AbstractSectionStorage storage;

	/**
	 * the cache
	 */
	private final LoadingCache<SectionDataId, SectionDataCacheEntry> cache;

	/**
	 * Constructor.
	 * @param storageFolder the storage folder to use for actually storing sections
	 */
	public SectionWorkingSet(final AbstractSectionStorage storageFolder) {
		this.storage = storageFolder;
		this.cache = CacheBuilder.newBuilder().maximumSize(1000).build(new CacheLoader<SectionDataId, SectionDataCacheEntry>() {

			@Override
			public SectionDataCacheEntry load(final SectionDataId sectionDataId) throws Exception {
				final byte[] data = storage.loadSectionRelatedObject(sectionDataId);
				return createOrUnserialize(sectionDataId, data);
			}

			@Override
			public Map<SectionDataId, SectionDataCacheEntry> loadAll(final Iterable<? extends SectionDataId> keys) throws Exception {
				final ArrayList<SectionDataId> ids = new ArrayList<SectionDataId>();
				for (final SectionDataId key : keys) {
					ids.add(key);
				}
				final Map<SectionDataId, byte[]> dataMap = storage.loadSectionRelatedObjects(ids);
				final Map<SectionDataId, SectionDataCacheEntry> result = new HashMap<SectionDataId, SectionDataCacheEntry>();
				for (SectionDataId key : keys) {
					result.put(key, createOrUnserialize(key, dataMap.get(key)));
				}
				return result;
			}

		});
	}

	/**
	 * Getter method for the storage.
	 * @return the storage
	 */
	public AbstractSectionStorage getStorage() {
		return storage;
	}

	/**
	 * Removes all cached objects.
	 */
	public void clearCache() {
		cache.invalidateAll();
	}

	/**
	 * Returns a single object, loading it if necessary.
	 * 
	 * @param sectionDataId the section data ID
	 * @return the section-related object
	 */
	public SectionDataCacheEntry get(final SectionDataId sectionDataId) {
		try {
			return cache.get(sectionDataId);
		} catch (final ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Returns a single object if present in the cache, null if not present.
	 * 
	 * @param sectionDataId the section data ID
	 * @return the section-related object or null
	 */
	public SectionDataCacheEntry getIfPresent(final SectionDataId sectionDataId) {
		return cache.getIfPresent(sectionDataId);
	}

	/**
	 * Returns multiple objects, loading them if necessary.
	 * 
	 * @param sectionDataIds the section data IDs
	 * @return the section-related objects
	 */
	public ImmutableMap<SectionDataId, SectionDataCacheEntry> getAll(final SectionDataId... sectionDataIds) {
		try {
			return cache.getAll(new Iterable<SectionDataId>() {
				@Override
				@SuppressWarnings("unchecked")
				public Iterator<SectionDataId> iterator() {
					return new ArrayIterator(sectionDataIds);
				}
			});
		} catch (final ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Of multiple objects, returns those that are already present in the cache.
	 * 
	 * @param sectionDataIds the section data IDs
	 * @return the cached section-related objects
	 */
	public ImmutableMap<SectionDataId, SectionDataCacheEntry> getAllPresent(final SectionDataId... sectionDataIds) {
		return cache.getAllPresent(new Iterable<SectionDataId>() {
			@Override
			@SuppressWarnings("unchecked")
			public Iterator<SectionDataId> iterator() {
				return new ArrayIterator(sectionDataIds);
			}
		});
	}
	
	/**
	 * Returns multiple objects, loading them if necessary.
	 * 
	 * @param sectionDataIds the section data IDs
	 * @return the section-related objects
	 */
	public ImmutableMap<SectionDataId, SectionDataCacheEntry> getAll(final Iterable<SectionDataId> sectionDataIds) {
		try {
			return cache.getAll(sectionDataIds);
		} catch (final ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Of multiple objects, returns those that are already present in the cache.
	 * 
	 * @param sectionDataIds the section data IDs
	 * @return the section-related objects
	 */
	public ImmutableMap<SectionDataId, SectionDataCacheEntry> getAllPresent(final Iterable<SectionDataId> sectionDataIds) {
		return cache.getAllPresent(sectionDataIds);
	}

	/**
	 * Pre-caches the objects for the specified IDs.
	 * 
	 * @param sectionDataIds the section data IDs
	 */
	public final void precache(final SectionDataId[] sectionDataIds) {

		// step through the IDs and only keep those for which the object isn't yet cached
		final List<SectionDataId> missingIds = new ArrayList<>();
		for (final SectionDataId sectionDataId : sectionDataIds) {
			if (cache.getIfPresent(sectionDataId) == null) {
				missingIds.add(sectionDataId);
			}
		}
		if (missingIds.isEmpty()) {
			return;
		}

		// load the section-related objects and store them in the cache
		final Map<SectionDataId, byte[]> datas = storage.loadSectionRelatedObjects(missingIds);
		for (final Map.Entry<SectionDataId, byte[]> dataEntry : datas.entrySet()) {
			final SectionDataId sectionDataId = dataEntry.getKey();
			final SectionDataCacheEntry entry = createOrUnserialize(sectionDataId, dataEntry.getValue());
			cache.asMap().putIfAbsent(sectionDataId, entry);
		}

	}

	/**
	 * Calls createDefault() or unserialize(), depending on whether the data
	 * argument is null.
	 */
	private SectionDataCacheEntry createOrUnserialize(final SectionDataId sectionDataId, final byte[] data) {
		if (data == null) {
			return createDefault(sectionDataId);
		} else {
			return unserializeForLoad(sectionDataId, data);
		}
	}

	/**
	 * Creates a default object for an ID that does not have that object in storage.
	 * 
	 * Note: there is currently no way to mark such objects as dirty before they have
	 * been placed into the cache, in case this function creates an object in a
	 * non-deterministic way. Just calling markModified() on the returned object
	 * is NOT correct as it creates a race condition with the worker thread that
	 * actually saves objects.
	 * --
	 * In case this gets implemented, the object should recognize that it was not added to
	 * the cache yet and handle this in markModified() (that is, add a task item when it
	 * later gets added). This should not require another method because the caller could
	 * still use markModified() accidentally which would be incorrect.
	 * 
	 * @param sectionDataId the section data ID
	 * @return the cache entry
	 */
	private SectionDataCacheEntry createDefault(final SectionDataId sectionDataId) {
		switch (sectionDataId.getType()) {

		case DEFINITIVE:
			return new SectionCubesCacheEntry(this, sectionDataId, new UniformCubes((byte)0));

		case INTERACTIVE:
			return new InteractiveSectionImageCacheEntry(this, sectionDataId, null);

		case VIEW_LOD_0:
			throw new NotImplementedException("LOD0 not implemented");

		default:
			throw new IllegalArgumentException("invalid section data type in: " + sectionDataId);

		}
	}

	/**
	 * Creates a cached object from a serialized representation.
	 * 
	 * @param sectionDataId the section data ID
	 * @param data the loaded data
	 * @return the cache entry
	 */
	protected SectionDataCacheEntry unserializeForLoad(final SectionDataId sectionDataId, final byte[] data) {
		switch (sectionDataId.getType()) {

		case DEFINITIVE: {
			final Cubes sectionCubes = Cubes.createFromCompressedData(Constants.SECTION_SIZE, data);
			return new SectionCubesCacheEntry(this, sectionDataId, sectionCubes);
		}

		case INTERACTIVE:
			return new InteractiveSectionImageCacheEntry(this, sectionDataId, data);

		case VIEW_LOD_0:
			throw new NotImplementedException("LOD0 not implemented");

		default:
			throw new IllegalArgumentException("invalid section data type in: " + sectionDataId);

		}
	}

}
