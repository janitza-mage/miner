package name.martingeisse.miner.client.engine.temp_old;

public interface GraphicsFrameContext {

    int getWidth();
    int getHeight();

    /**
     * Schedules an OpenGL work unit for execution.
     *
     * @param workUnit the work unit
     */
    void schedule(GlWorkUnit workUnit);

    /**
     * Schedules an OpenGL pseudo-work unit that marks the beginning of a sequence of work units that have cross-frame
     * side effects such as creating and setting up textures and buffers. Such work units are considered unskippable
     * even in high-load situations.
     */
    void scheduleBeginSideEffectsMarker();

    /**
     * Schedules an OpenGL pseudo-work unit that marks the end of a sequence of work units that have cross-frame
     * side effects.
     */
    void scheduleEndSideEffectsMarker();

}
