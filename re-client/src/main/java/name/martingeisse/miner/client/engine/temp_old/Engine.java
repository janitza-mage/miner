package name.martingeisse.miner.client.engine.temp_old;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * This class must be used from the main thread (which then becomes the OpenGL thread). It will call frame handlers
 * in the game logic thread, but those should interact with the engine solely through the {@link LogicFrameContext}
 * interface.
 */
public final class Engine implements AutoCloseable {

    private final List<AutoCloseable> closeables = new ArrayList<>();
    private final long fixedFrameIntervalNanoseconds;
    private final double fixedFrameIntervalSeconds;
    private final LogicFrameContextImpl gameLogicContext;
    private final GraphicsFrameContextImpl graphicsFrameContext;
    private final long windowId;
    private final GlWorkerLoop glWorkerLoop;
    private final List<GlWorkUnit> initializationWorkUnits = new ArrayList<>();
    private final int width, height;
    private FrameHandler frameHandler;

    public Engine(EngineParameters engineParameters, String[] commandLineArguments, FrameHandler initialFrameHandler) {
        if (engineParameters.fixedFrameIntervalMilliseconds() == null) {
            this.fixedFrameIntervalNanoseconds = -1;
            this.fixedFrameIntervalSeconds = -1;
        } else if (engineParameters.fixedFrameIntervalMilliseconds() <= 0) {
            throw new IllegalArgumentException("fixed frame interval must be positive");
        } else {
            this.fixedFrameIntervalNanoseconds = engineParameters.fixedFrameIntervalMilliseconds() * 1_000_000L;
            this.fixedFrameIntervalSeconds = engineParameters.fixedFrameIntervalMilliseconds() * 1E-3;
        }
        this.frameHandler = initialFrameHandler;
        this.width = engineParameters.userParameters().width();
        this.height = engineParameters.userParameters().height();

        // initialize GLFW / OpenGL
        if (!glfwInit()) {
            throw new RuntimeException("could not initialize GLFW");
        }
        closeables.add(() -> glfwTerminate());
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        windowId = glfwCreateWindow(
                engineParameters.userParameters().width(),
                engineParameters.userParameters().height(),
                engineParameters.windowTitle(),
                NULL,
                NULL
        );
        closeables.add(() -> glfwDestroyWindow(windowId));
        glfwMakeContextCurrent(windowId);
        createCapabilities();

        // initialize the GL worker loop and game code contexts
        this.glWorkerLoop = new GlWorkerLoop(windowId);
        this.gameLogicContext = new LogicFrameContextImpl(this);
        this.graphicsFrameContext = new GraphicsFrameContextImpl(this, glWorkerLoop);

        // install event handlers
        closeables.add(glfwSetKeyCallback(windowId, (long windowId, int key, int scancode, int action, int mods) -> {
            gameLogicContext.addKeyboardEvent(new KeyboardEvent(key, scancode, action, mods));
        }));
        closeables.add(glfwSetCursorPosCallback(windowId, (long windowId, double x, double y) -> {
            gameLogicContext.setIncomingMousePosition(x, y);
        }));
        closeables.add(glfwSetMouseButtonCallback(windowId, (long windowId, int button, int action, int mods) -> {
            gameLogicContext.addMouseButtonEvent(new MouseButtonEvent(button, action, mods));
        }));

    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void addInitializationWorkUnit(GlWorkUnit workUnit) {
        initializationWorkUnits.add(workUnit);
    }

    public void run() throws InterruptedException{

        // run the initialization work units
        glWorkerLoop.scheduleBeginSideEffectsMarker();
        for (GlWorkUnit workUnit : initializationWorkUnits) {
            glWorkerLoop.schedule(workUnit);
        }
        initializationWorkUnits.clear();
        glWorkerLoop.scheduleEndSideEffectsMarker();

        // create the game thread
        new Thread(() -> {
            executeFrameLoop();
            glWorkerLoop.scheduleShutdown();
        }, "Game").start();

        // make the calling thread become the OpenGL thread
        Thread.currentThread().setName("OpenGL");
        glWorkerLoop.workAndWait();

    }

    private void executeFrameLoop() {
        long previousLogicFrameTime = System.nanoTime();
        while (!gameLogicContext.isShutdownRequested() && !glfwWindowShouldClose(windowId)) {

            // system stuff
            glfwPollEvents();

            // logic frame
            long now = System.nanoTime();
            if (fixedFrameIntervalNanoseconds < 0) {
                gameLogicContext.processEvents();
                gameLogicContext.setTimeDelta((now - previousLogicFrameTime) * 1E-9);
                previousLogicFrameTime = now;
                frameHandler.handleLogicFrame(gameLogicContext);
            } else {
                while (now >= previousLogicFrameTime + fixedFrameIntervalNanoseconds) {
                    gameLogicContext.processEvents();
                    gameLogicContext.setTimeDelta(fixedFrameIntervalSeconds);
                    previousLogicFrameTime += fixedFrameIntervalNanoseconds;
                    frameHandler.handleLogicFrame(gameLogicContext);
                }
            }

            // graphics frame
            if (!glWorkerLoop.isOverloaded()) {
                frameHandler.handleGraphicsFrame(graphicsFrameContext);
                glWorkerLoop.scheduleFrameBoundary();
            }

        }
    }

    @Override
    public void close() {
        while (!closeables.isEmpty()) {
            AutoCloseable closeable = closeables.removeLast();
            try {
                closeable.close();
            } catch (Exception e) {
                // ignore, so we can close the other ones
            }
        }
    }

    long getWindowId() {
        return windowId;
    }

    void setFrameHandler(FrameHandler frameHandler) {
        this.frameHandler = frameHandler;
    }

}
