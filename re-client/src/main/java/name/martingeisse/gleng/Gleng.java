package name.martingeisse.gleng;

public final class Gleng {

    static GlengEngine engine;

    /**
     * This must be called from the main method / main thread. It eventually spawns the application thread and turns
     * the calling thread into the OpenGL thread.
     */
    public static void run(GlengParameters parameters, GlengCallbacks callbacks, Runnable gameMain) throws InterruptedException {
        if (engine != null) {
            throw new IllegalStateException("Gleng instance already exists");
        }
        engine = new GlengEngine(parameters, callbacks, gameMain);
        try {
            engine.run();
        } finally {
            engine.close();
            engine = null;
        }
    }

    /**
     * This method should be called by the application thread, and if it returns true, work should be skipped as much
     * as possible.
     * <p>
     * Relation to skippable work units: When the system is becoming overloaded, the first signal to skip work is
     * this method, so work gets skipped at the application layer and not submitted to the OpenGL thread. If the
     * system gets even more overloaded, Gleng will start to skip any skippable work units.
     */
    public static boolean isOverloaded() {
        return engine.glWorkerLoop.isOverloaded();
    }

    /**
     * Schedules a special work unit to finish the current frame, swapping buffers. This work unit is also used to
     * measure the current system load and decide whether the application and/or Gleng itself should skip work.
     */
    public static void scheduleEndOfFrame() {
        engine.glWorkerLoop.scheduleFrameBoundary();
    }

}
