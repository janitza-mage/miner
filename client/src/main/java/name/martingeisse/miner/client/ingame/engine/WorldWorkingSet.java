/**
 * Copyright (c) 2012 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.ingame.engine;

import name.martingeisse.miner.client.util.ray_action.SystemResourceNode;
import name.martingeisse.miner.common.collision.CompositeCollider;
import name.martingeisse.miner.common.collision.IAxisAlignedCollider;
import name.martingeisse.miner.common.section.SectionId;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Represents the "working set" (i.e. currently visible sections) of a potentially huge
 * cube matrix world. This class supports loading parts of the world dynamically. It
 * does NOT support "parallel dimensions", i.e. separate cube matrices -- use a
 * separate instance for that.
 *
 * TODO: dispose of system resources; allow passing a parent resource node to the constructor
 */
public final class WorldWorkingSet {

	/**
	 * the logger
	 */
	private static Logger logger = Logger.getLogger(WorldWorkingSet.class);

	private final SectionRenderer sectionRenderer;

	/**
	 * the engineParameters
	 */
	private final EngineParameters engineParameters;

	/**
	 * the systemResourceNode
	 */
	private final SystemResourceNode systemResourceNode;

	/**
	 * the renderableSections
	 */
	private final Map<SectionId, InteractiveSection> interactiveSections;

	/**
	 * the renderableSectionsLoadedQueue
	 */
	private final ConcurrentLinkedQueue<InteractiveSection> interactiveSectionsLoadedQueue;

	/**
	 * the combined collider
	 */
	private final IAxisAlignedCollider compositeCollider;

	/**
	 * the renderUnits
	 */
	private RenderUnit[] renderUnits;

	/**
	 * Constructor.
	 * @param engineParameters static engine parameters, such as strategies
	 */
	public WorldWorkingSet(final EngineParameters engineParameters) {
		this.sectionRenderer = new SectionRenderer();
		this.engineParameters = engineParameters;
		this.systemResourceNode = new SystemResourceNode();
		this.interactiveSections = new HashMap<>();
		this.interactiveSectionsLoadedQueue = new ConcurrentLinkedQueue<>();
		this.compositeCollider = new CompositeCollider(interactiveSections.values());
		this.renderUnits = null;
	}

	public SectionRenderer getSectionRenderer() {
		return sectionRenderer;
	}

	/**
	 * Getter method for the engineParameters.
	 * @return the engineParameters
	 */
	public EngineParameters getEngineParameters() {
		return engineParameters;
	}

	/**
	 * Getter method for the systemResourceNode.
	 * @return the systemResourceNode
	 */
	public SystemResourceNode getSystemResourceNode() {
		return systemResourceNode;
	}

	public Map<SectionId, InteractiveSection> getInteractiveSections() {
		return interactiveSections;
	}

	public ConcurrentLinkedQueue<InteractiveSection> getInteractiveSectionsLoadedQueue() {
		return interactiveSectionsLoadedQueue;
	}

	/**
	 * Getter method for the composite collider that represents the world.
	 * @return the composite collider
	 */
	public IAxisAlignedCollider getCompositeCollider() {
		return compositeCollider;
	}

	/**
	 * Draws the working set, using the currently installed transformation. The specified
	 * viewer position is used only for culling; it doesn't affect the transformation.
	 *
	 * @param frameRenderParameters per-frame rendering parameters
	 */
	public void draw(final FrameRenderParameters frameRenderParameters) {

		// prepare
		if (renderUnits == null) {
			List<RenderUnit> renderUnitList = new ArrayList<>();
			for (final InteractiveSection interactiveSection : interactiveSections.values()) {
				interactiveSection.prepare(sectionRenderer);
				for (RenderUnit renderUnit : interactiveSection.getRenderUnits()) {
					renderUnitList.add(renderUnit);
				}
			}
			renderUnits = renderUnitList.toArray(new RenderUnit[renderUnitList.size()]);
			Arrays.sort(renderUnits, new Comparator<RenderUnit>() {
				@Override
				public int compare(RenderUnit unit1, RenderUnit unit2) {
					if (unit1.getTextureIndex() != unit2.getTextureIndex()) {
						return unit1.getTextureIndex() - unit2.getTextureIndex();
					}
					if (unit1.getTextureCoordinateGenerationDirection() != unit2.getTextureCoordinateGenerationDirection()) {
						return unit1.getTextureCoordinateGenerationDirection().ordinal() - unit2.getTextureCoordinateGenerationDirection().ordinal();
					}
					if (unit1.getBackfaceCullingDirection() == unit2.getBackfaceCullingDirection()) {
						return 0;
					}
					if (unit1.getBackfaceCullingDirection() == null) {
						return -1;
					}
					if (unit2.getBackfaceCullingDirection() == null) {
						return +1;
					}
					return unit1.getBackfaceCullingDirection().ordinal() - unit2.getBackfaceCullingDirection().ordinal();
				}
			});
			logger.info("working set now has " + renderUnits.length + " render units");
		}

		// render
		sectionRenderer.onBeforeRenderWorkingSet(this, frameRenderParameters);
		for (RenderUnit renderUnit : renderUnits) {
			renderUnit.draw(sectionRenderer, engineParameters, frameRenderParameters, sectionRenderer.getGlWorkerLoop());
		}
		sectionRenderer.onAfterRenderWorkingSet(this, frameRenderParameters);

	}

	/**
	 * Actually adds loaded sections from the "loaded" queues to the working set.
	 */
	public void acceptLoadedSections() {
		{
			boolean modified = false;
			InteractiveSection interactiveSection;
			while ((interactiveSection = interactiveSectionsLoadedQueue.poll()) != null) {
				interactiveSections.put(interactiveSection.getSectionId(), interactiveSection);
				modified = true;
			}
			if (modified) {
				markRenderModelsModified();
			}
		}
	}

	/**
	 * Checks whether all interactive sections around the specified center are present in the working
	 * set. Currently using "city block distance" (checking a rectangular region), not
	 * euclidian distance (which would check a sphere).
	 *
	 * @param center the center section
	 * @param radius the "radius"
	 * @return true if all render models are present, false if some are missing
	 */
	public boolean hasAllInteractiveSections(SectionId center, final int radius) {
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					final int cx = center.getX() + dx, cy = center.getY() + dy, cz = center.getZ() + dz;
					final SectionId id = new SectionId(cx, cy, cz);
					if (!interactiveSections.containsKey(id)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * This method should be called when render models have been added or removed.
	 *
	 * TODO: there should be high-level modification methods, and they should do this themselves.
	 */
	public void markRenderModelsModified() {

		// TODO dispose of the VBOs!!!
		renderUnits = null;

	}

}
