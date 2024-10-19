/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.client.engine;

import name.martingeisse.gleng.GlWorkUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * This class maintains a fixed set of {@link GlWorkUnit}s for a fixed
 * set of subjects of type T. The subjects must be specified at construction
 * of this object, and one work unit is created per subject. Later, the
 * work units can be scheduled as a batch, and will invoke the
 * {@link #handleSubject(Object)} method in the GL worker thread for each
 * subject. The work units can also be scheduled individually by specifying
 * their subject, which is the reason why this class doesn't use a single
 * big work unit for all subjects.
 *
 * @param <T> the subject type
 */
public abstract class FixedSubjectsWorkUnits<T> {

	private final GlWorkUnit[] workUnits;
	private final Map<T, GlWorkUnit> workUnitBySubject;

	public FixedSubjectsWorkUnits(T[] subjects) {
		workUnits = new GlWorkUnit[subjects.length];
		workUnitBySubject = new HashMap<>();
		for (int i = 0; i < subjects.length; i++) {
			workUnits[i] = new MyWorkUnit(subjects[i]);
			workUnitBySubject.put(subjects[i], workUnits[i]);
		}
	}

	/**
	 * Schedules all work units.
	 */
	public final void schedule() {
		for (GlWorkUnit glWorkUnit : workUnits) {
			glWorkUnit.schedule();
		}
	}

	/**
	 * Schedules the work unit for a single subject.
	 */
	public final void schedule(T subject) {
		GlWorkUnit workUnit = workUnitBySubject.get(subject);
		if (workUnit == null) {
			throw new IllegalArgumentException("no work unit for subject: " + subject);
		}
		workUnit.schedule();
	}

	/**
	 * Handles a subject. This method gets called in the GL worker thread.
	 * @param subject the subject
	 */
	protected abstract void handleSubject(T subject);

	/**
	 *
	 */
	final class MyWorkUnit extends GlWorkUnit {

		final T subject;

		MyWorkUnit(T subject) {
			this.subject = subject;
		}

		@Override
		public void gl__Execute() {
			handleSubject(subject);
		}

	}
}
