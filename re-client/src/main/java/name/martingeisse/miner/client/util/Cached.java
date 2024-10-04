package name.martingeisse.miner.client.util;

import java.util.function.Supplier;

public class Cached<T> {

    private final Supplier<T> factory;
    private T cached;

    public Cached(Supplier<T> factory) {
        this.factory = factory;
    }

    public final T get() {
        if (cached == null) {
            cached = factory.get();
        }
        return cached;
    }

    public final void invalidate() {
        cached = null;
    }

}
