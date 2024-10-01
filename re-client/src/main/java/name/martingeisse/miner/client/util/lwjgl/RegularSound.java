/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.util.lwjgl;

import name.martingeisse.miner.client.engine.sound.SoundTemplate;

/**
 * Helper class to play a sound regularly, possibly under
 * specific conditions. Time is measured in milliseconds.
 *
 * If a single step of time skips more than one interval,
 * the sound is still played only once since playing it
 * multiple times would only make it louder.
 */
public final class RegularSound {

	/**
	 * the audio
	 */
	private final SoundTemplate soundTemplate;

	/**
	 * the interval
	 */
	private final long interval;

	/**
	 * the lastPlayed
	 */
	private long lastPlayed;

	/**
	 * Constructor.
	 * @param soundTemplate the sound to play
	 * @param interval the time (in milliseconds) between two
	 * occurences of the sound
	 */
	public RegularSound(SoundTemplate soundTemplate, long interval) {
		this.soundTemplate = soundTemplate;
		this.interval = interval;
		this.lastPlayed = System.currentTimeMillis();
	}

	/**
	 * Adds the time since the last call to the internal time
	 * counters, possibly playing the sound.
	 */
	public void handleActiveTime() {
		long now = System.currentTimeMillis();
		if (now >= lastPlayed + interval) {
			lastPlayed = now;
			soundTemplate.play();
		}
	}

	/**
	 * Resets the internal time counter.
	 */
	public void reset() {
		lastPlayed = System.currentTimeMillis();
	}

}
