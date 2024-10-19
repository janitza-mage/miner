/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.gleng;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;

/**
 * This class must be used by the OpenGL worker thread to handle work units. Work units are scheduled by calling
 * {@link #schedule(GlWorkUnit)}.
 * <p>
 * The work queue will stop when requested to do so by {@link #scheduleShutdown()}. This schedules a special
 * work unit that stops the loop when executed.
 * <p>
 * Gleng uses double buffering. To see any rendered images, the current frame must be finished by calling
 * {@link #scheduleFrameBoundary()}.
 * <p>
 * This class measures the current workload in frames. If the workload exceeds the soft workload limit, then
 * {@link #isOverloaded()} returns true and the caller should stop rendering until no longer overloaded.
 * <p>
 * If the client continues rendering, the workload will eventually exceed the hard workload limit. This causes this
 * worker loop to skip any skippable work units. The application should mark pure rendering work units as skippable
 * to support this; unskippable work units, on the other hand, are used for things with side effects, such as setting
 * up textures and buffers.
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
			public void gl__Execute() {
				shutdownRequested = true;
			}
		});
	}

	/**
	 * Schedules a frame boundary "work unit".
	 */
	void scheduleFrameBoundary() {
		workload.incrementAndGet();
		queue.add(FRAME_BOUNDARY);
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
			GlWorkUnit workUnit = queue.take();
			if (!wantsToSkip || !workUnit.isSkippable()) {
				workUnit.gl__Execute();
			}
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
		public void gl__Execute() {
			glfwSwapBuffers(windowId);
			wantsToSkip = (workload.decrementAndGet() >= HARD_WORKLOAD_LIMIT);
			if (wantsToSkip) {
				System.out.println("low-level skip frame");
			}
			openglTimeMilliseconds = (int) (System.currentTimeMillis() - openglTimeBase);
		}
	};

}
