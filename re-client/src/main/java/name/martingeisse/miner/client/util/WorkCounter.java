/**
 * Copyright (c) 2011 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util;

/**
 * Utility class to count how often a piece of work was done. Used to optimize performance.
 * <p>
 * Work is measured in cycles, and each cycle is subdivided in a number of ticks. A tick could, for example, be a
 * graphics frame, with a cycle being roughly a second (note though that this class assumes a fixed number of ticks,
 * not a fixed time interval, per cycle).
 * <p>
 * This class then counts the number of times some work is performed, represented by calls to {@link #countWork()}.
 * Such a piece of work could be rendering a vertex buffer, or a memory allocation. Whenever a tick is completed,
 * the {@link #tick()} method must be called. This class will detect when the number of ticks corresponds to a full
 * cycle, and call {@link #onCycle()}. The subclass can then react to the accumulated data. This base class will
 * provide access to the min/max/average number of work units per tick, as well as the full collected stats.
 * <p>
 * Note that all output values are only valid during the call to {@link #onCycle()}.
 */
public abstract class WorkCounter {

	// configuration
	private final int ticksPerCycle;

	// internal state
	private int workCounter;
	private int tickCounter;

	// output
	private final int[] stats;
	private int minimumWork;
	private int maximumWork;
	private int averageWork;

	public WorkCounter(final int ticksPerCycle) {
		if (ticksPerCycle < 1) {
			throw new IllegalArgumentException("ticksPerCycle argument must be at least 1");
		}
		this.ticksPerCycle = ticksPerCycle;
		this.workCounter = 0;
		this.tickCounter = 0;
		this.stats = new int[ticksPerCycle];
		this.minimumWork = 0;
		this.maximumWork = 0;
		this.averageWork = 0;
	}

	public final int getTicksPerCycle() {
		return ticksPerCycle;
	}

	/**
	 * Counts a unit of work.
	 */
	public void countWork() {
		workCounter++;
	}

	/**
	 * Counts a unit of time.
	 */
	public void tick() {
		stats[tickCounter] = workCounter;
		workCounter = 0;
		tickCounter++;
		if (tickCounter == ticksPerCycle) {
			tickCounter = 0;
			minimumWork = Integer.MAX_VALUE;
			maximumWork = Integer.MIN_VALUE;
			averageWork = 0;
			for (int element : stats) {
				if (element < minimumWork) {
					minimumWork = element;
				}
				if (element > maximumWork) {
					maximumWork = element;
				}
				averageWork += element;
			}
			averageWork /= ticksPerCycle;
			onCycle();
		}
	}

	/**
	 * This method is called whenever the specified period is completed, after stats have been updated. Subclasses can
	 * override this method to react to such updates. The default implementation does nothing.
	 */
	protected abstract void onCycle();

	public final int[] getStats() {
		return stats;
	}

	public final int getMinimumWork() {
		return minimumWork;
	}

	public final int getMaximumWork() {
		return maximumWork;
	}

	public final int getAverageWork() {
		return averageWork;
	}

	/**
	 * Work counter implementation that logs min/max/average to the console. The full stats are not printed by default.
	 */
	public static class WorkLogger extends WorkCounter {

		private final String description;

		/**
		 * Constructor.
		 * @param ticksPerCycle the ticksPerCycle
		 * @param description a string that is printed with every change message
		 */
		public WorkLogger(int ticksPerCycle, String description) {
			super(ticksPerCycle);
			this.description = description;
		}

		@Override
		protected void onCycle() {
			System.out.println(
					description +
					": min = " + getMinimumWork() +
					", max = " + getMaximumWork() +
					", avg = " + getAverageWork()
			);
		}

	}

}
