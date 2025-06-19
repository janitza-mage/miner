/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.common.task;

import java.util.concurrent.TimeUnit;

/**
 * Base interface for task-like objects that can be scheduled for execution.
 */
public interface ITaskLike {

	/**
	 * Schedules this task-like object to run ASAP.
	 */
	void schedule();

	/**
	 * Schedules this task-like object to run N milliseconds in the future.
	 *
	 * @param milliseconds the number of milliseconds to run in the future
	 */
	void scheduleRelative(long milliseconds);

	/**
	 * Schedules this task-like object to run in N milliseconds.
	 *
	 * @param delay the delay to wait before execution
	 * @param timeUnit the unit used for the delay
	 */
	void scheduleRelative(long delay, TimeUnit timeUnit);

}
