package name.martingeisse.miner.client.engine.temp_old;

final class GraphicsFrameContextImpl implements GraphicsFrameContext {

    private final Engine engine;
    private final GlWorkerLoop glWorkerLoop;

    GraphicsFrameContextImpl(Engine engine, GlWorkerLoop glWorkerLoop) {
        this.engine = engine;
        this.glWorkerLoop = glWorkerLoop;
    }

    @Override
    public int getWidth() {
        return engine.getWidth();
    }

    @Override
    public int getHeight() {
        return engine.getHeight();
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
