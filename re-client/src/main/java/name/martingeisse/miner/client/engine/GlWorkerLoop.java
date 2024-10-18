/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

/**
 * This class must be used by the OpenGL worker thread to handle work units. Work units are scheduled by calling
 * {@link #schedule(GlWorkUnit)}.
 * <p>
 * The work queue will stop when requested to do so by {@link #schedule(GlWorkUnit)}. This schedules a special
 * work unit that stops the loop when executed.
 * <p>
 * This class measures the current workload in frames. The caller must invoke {@link #scheduleFrameBoundary()}
 * between frames to support this. If the workload exceeds the soft workload limit, then {@link #isOverloaded()}
 * returns true and the caller should stop rendering until no longer overloaded.
 * <p>
 * If the client continues rendering, the workload will eventually exceed the hard workload limit. This causes this
 * worker loop to skip work units. The caller can mark work units as unskippable by inserting the appropriate
 * markers; this is used for work units that set up textures and buffers, so their side effects won't get lost.
 */
final class GlWorkerLoop {

	// constants
	private static final int SOFT_WORKLOAD_LIMIT = 2;
	private static final int HARD_WORKLOAD_LIMIT = 3;

	// global state
	private final BlockingQueue<GlWorkUnit> queue = new LinkedBlockingQueue<>();
	private final AtomicInteger workload = new AtomicInteger(0);

	// OpenGL thread-local state
	private final long windowId;
	private final long openglTimeBase;
	private boolean insideSideEffects = false;
	private boolean wantsToSkip = false;
	private boolean shutdownRequested = false;

	public GlWorkerLoop(long windowId) {
		this.windowId = windowId;
		this.openglTimeBase = System.currentTimeMillis();
	}

	// ----------------------------------------------------------------------------------------------------------------
	// control (globally callable)
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Checks whether this worker loop is currently overloaded by comparing
	 * the current workload and the frame skip threshold.
	 * @return true if overloaded, false if not
	 */
	boolean isOverloaded() {
		return (workload.get() >= SOFT_WORKLOAD_LIMIT);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// scheduling (globally callable)
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Schedules a work unit for execution.
	 * @param workUnit the work unit
	 */
	void schedule(GlWorkUnit workUnit) {
		queue.add(workUnit);
	}

	/**
	 * Schedules a special work unit that stops the loop when executed.
	 */
	void scheduleShutdown() {
		queue.add(new GlWorkUnit() {
			@Override
			public void execute() {
				shutdownRequested = true;
			}
		});
	}

	/**
	 * Schedules a frame boundary "work unit".
	 */
	void scheduleFrameBoundary() {
		queue.add(FRAME_BOUNDARY);
		workload.incrementAndGet();
	}

	/**
	 *
	 */
	void scheduleBeginSideEffectsMarker() {
		queue.add(BEGIN_SIDE_EFFECTS);
	}

	/**
	 *
	 */
	void scheduleEndSideEffectsMarker() {
		queue.add(END_SIDE_EFFECTS);
	}

	// ----------------------------------------------------------------------------------------------------------------
	// execution
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * Executes work units, waiting for new ones when the queue becomes empty.
	 * This method must only be called by the OpenGL thread.
	 *
	 * @throws InterruptedException if interrupted while waiting
	 */
	void workAndWait() throws InterruptedException {
		while (!shutdownRequested) {
			execute(queue.take());
		}
	}

	/**
	 * This method must only be called by the OpenGL thread.
	 * @param workUnit the work unit to execute
	 */
	void execute(GlWorkUnit workUnit) {
		if (insideSideEffects || !wantsToSkip) {
			workUnit.execute();
		}
	}

	// ----------------------------------------------------------------------------------------------------------------
	// special work units
	// ----------------------------------------------------------------------------------------------------------------

	/**
	 * This WU is inserted between frames to measure the workload and to ensure that only whole frames get skipped.
	 */
	private final GlWorkUnit FRAME_BOUNDARY = new GlWorkUnit() {
		@Override
		public void execute() {
			if (insideSideEffects) {
				throw new IllegalStateException("OpenGL side effects not properly terminated");
			}
			glfwSwapBuffers(windowId);
			wantsToSkip = (workload.decrementAndGet() >= HARD_WORKLOAD_LIMIT);
			if (wantsToSkip) {
				System.out.println("low-level skip frame");
			}
			openglTimeMilliseconds = (int) (System.currentTimeMillis() - openglTimeBase);
		}
	};

	/**
	 * Marks the WUs following this WU as having side effects, and thus "not skippable".
	 */
	private final GlWorkUnit BEGIN_SIDE_EFFECTS = new GlWorkUnit() {
		@Override
		public void execute() {
			if (insideSideEffects) {
				throw new IllegalStateException("OpenGL side effects not properly terminated");
			}
			insideSideEffects = true;
		}
	};

	/**
	 * Marks the WUs following this WU as no longer having side effects, and thus "skippable".
	 */
	private final GlWorkUnit END_SIDE_EFFECTS = new GlWorkUnit() {
		@Override
		public void execute() {
			if (!insideSideEffects) {
				throw new IllegalStateException("OpenGL side effects not properly started");
			}
			insideSideEffects = false;
		}
	};

}
