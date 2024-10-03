package name.martingeisse.miner.client.util.gui.util;

import name.martingeisse.miner.client.engine.GlWorkUnit;
import name.martingeisse.miner.client.engine.GraphicsFrameContext;

import java.util.function.Supplier;

public final class WorkUnitCache {

    private final Supplier<GlWorkUnit> supplier;
    private GlWorkUnit cached;

    public WorkUnitCache(Supplier<GlWorkUnit> supplier) {
        this.supplier = supplier;
    }

    public void schedule(GraphicsFrameContext context) {
        if (cached == null) {
            cached = supplier.get();
        }
        context.schedule(cached);
    }

    public void invalidate() {
        cached = null;
    }

}
