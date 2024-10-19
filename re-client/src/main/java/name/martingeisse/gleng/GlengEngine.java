package name.martingeisse.gleng;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

final class GlengEngine implements AutoCloseable {

    final List<AutoCloseable> closeables = new ArrayList<>();
    final GlengParameters parameters;
    final GlengCallbacks callbacks;
    final Runnable gameMain;
    final long windowId;
    final GlWorkerLoop glWorkerLoop;

    GlengEngine(GlengParameters parameters, GlengCallbacks callbacks, Runnable gameMain) {
        this.parameters = parameters;
        this.callbacks = callbacks;
        this.gameMain = gameMain;

        // initialize GLFW / OpenGL
        if (!glfwInit()) {
            throw new RuntimeException("could not initialize GLFW");
        }
        closeables.add(() -> glfwTerminate());

        // create the window
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        windowId = GLFW.glfwCreateWindow(
                parameters.width(),
                parameters.height(),
                parameters.windowTitle(),
                NULL,
                NULL
        );
        closeables.add(() -> glfwDestroyWindow(windowId));
        glfwMakeContextCurrent(windowId);
        createCapabilities();

        // initialize the GL worker loop and game code contexts
        this.glWorkerLoop = new GlWorkerLoop(windowId);

        // install event handlers
        closeables.add(glfwSetKeyCallback(windowId, (long windowId, int key, int scancode, int action, int mods) -> {
            callbacks.onKeyEvent(key, scancode, action, mods);
        }));
        closeables.add(glfwSetCursorPosCallback(windowId, (long windowId, double x, double y) -> {
            callbacks.onMousePositionEvent(x, y);
        }));
        closeables.add(glfwSetMouseButtonCallback(windowId, (long windowId, int button, int action, int mods) -> {
            callbacks.onMouseButtonEvent(button, action, mods);
        }));

    }

    void run() throws InterruptedException {

        // create the game thread
        new Thread(() -> {
            gameMain.run();
            glWorkerLoop.scheduleShutdown();
        }, "Game").start();

        // make the calling thread become the OpenGL thread
        Thread.currentThread().setName("OpenGL");
        glWorkerLoop.workAndWait();

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

}
