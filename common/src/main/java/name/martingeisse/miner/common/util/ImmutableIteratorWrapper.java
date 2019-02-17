/**
 * Copyright (c) 2015 Martin Geisse
 */

package name.martingeisse.miner.common.util;

import org.apache.commons.collections.iterators.UnmodifiableIterator;

import java.util.Iterator;

/**
 * This class wraps another iterator to block off the {@link #remove()} method.
 * It is like {@link UnmodifiableIterator}, just with a type parameter.
 * @param <T> the element type
 */
public final class ImmutableIteratorWrapper<T> implements Iterator<T> {

	/**
	 * the wrapped
	 */
	private final Iterator<T> wrapped;

	/**
	 * Constructor.
	 * @param wrapped the wrapped iterator
	 */
	public ImmutableIteratorWrapper(Iterator<T> wrapped) {
		this.wrapped = wrapped;
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return wrapped.hasNext();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public T next() {
		return wrapped.next();
	}

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
