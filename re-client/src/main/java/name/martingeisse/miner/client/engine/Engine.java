package name.martingeisse.miner.client.engine;

import name.martingeisse.gleng.Gleng;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/**
 * This class must be used from the main thread (which then becomes the OpenGL thread). It will call frame handlers
 * in the game logic thread, but those should interact with the engine solely through the {@link LogicFrameContext}
 * interface.
 */
public final class Engine {

    private final EngineParameters engineParameters;
    private FrameHandler frameHandler;
    private final long fixedLogicFrameIntervalNanoseconds;
    private final double fixedLogicFrameIntervalSeconds;
    private final LogicFrameContextImpl logicFrameContext;

    public Engine(EngineParameters engineParameters, GlengCallbacksImpl glengCallbacks, FrameHandler initialFrameHandler) {
        this.engineParameters = engineParameters;
        this.frameHandler = initialFrameHandler;
        if (engineParameters.fixedLogicFrameIntervalMilliseconds() == null) {
            this.fixedLogicFrameIntervalNanoseconds = -1;
            this.fixedLogicFrameIntervalSeconds = -1;
        } else if (engineParameters.fixedLogicFrameIntervalMilliseconds() <= 0) {
            throw new IllegalArgumentException("fixed frame interval must be positive");
        } else {
            this.fixedLogicFrameIntervalNanoseconds = engineParameters.fixedLogicFrameIntervalMilliseconds() * 1_000_000L;
            this.fixedLogicFrameIntervalSeconds = engineParameters.fixedLogicFrameIntervalMilliseconds() * 1E-3;
        }
        this.logicFrameContext = new LogicFrameContextImpl(this, glengCallbacks);
    }

    public EngineParameters getEngineParameters() {
        return engineParameters;
    }

    public FrameHandler getFrameHandler() {
        return frameHandler;
    }

    public void setFrameHandler(FrameHandler frameHandler) {
        this.frameHandler = frameHandler;
    }

    public void executeFrameLoop() {
        long previousLogicFrameTime = System.nanoTime();
        while (!logicFrameContext.isShutdownRequested() && !glfwWindowShouldClose(Gleng.getWindowId())) {

            // system stuff
            glfwPollEvents();

            // logic frame
            long now = System.nanoTime();
            if (fixedLogicFrameIntervalNanoseconds < 0) {
                logicFrameContext.processEvents();
                logicFrameContext.setTimeDelta((now - previousLogicFrameTime) * 1E-9);
                previousLogicFrameTime = now;
                frameHandler.handleLogicFrame(logicFrameContext);
            } else {
                while (now >= previousLogicFrameTime + fixedLogicFrameIntervalNanoseconds) {
                    logicFrameContext.processEvents();
                    logicFrameContext.setTimeDelta(fixedLogicFrameIntervalSeconds);
                    previousLogicFrameTime += fixedLogicFrameIntervalNanoseconds;
                    frameHandler.handleLogicFrame(logicFrameContext);
                }
            }

            // graphics frame
            if (!Gleng.isOverloaded()) {
                frameHandler.handleGraphicsFrame();
                Gleng.scheduleEndOfFrame();
            }

        }
    }


}
