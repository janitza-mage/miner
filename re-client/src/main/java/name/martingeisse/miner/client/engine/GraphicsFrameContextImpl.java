package name.martingeisse.miner.client.engine;

final class GraphicsFrameContextImpl implements GraphicsFrameContext {

    private final GlWorkerLoop glWorkerLoop;

    GraphicsFrameContextImpl(GlWorkerLoop glWorkerLoop) {
        this.glWorkerLoop = glWorkerLoop;
    }

    @Override
    public void schedule(GlWorkUnit workUnit) {
        glWorkerLoop.schedule(workUnit);
    }

    @Override
    public void scheduleBeginSideEffectsMarker() {
        glWorkerLoop.scheduleBeginSideEffectsMarker();
    }

    @Override
    public void scheduleEndSideEffectsMarker() {
        glWorkerLoop.scheduleEndSideEffectsMarker();
    }

}
