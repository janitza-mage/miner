package name.martingeisse.miner.client.engine;

import name.martingeisse.gleng.Gleng;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

final class LogicFrameContextImpl implements LogicFrameContext {

    private final Engine engine;
    private final GlengCallbacksImpl glengCallbacks;
    private double timeDelta = 0;
    private boolean shutdownRequested = false;

    private final List<KeyboardEvent> currentKeyboardEvents = new ArrayList<>();
    private final boolean[] currentKeyStates = new boolean[GLFW_KEY_LAST + 1];
    private final boolean[] previousKeyStates = new boolean[GLFW_KEY_LAST + 1];

    private final List<MouseButtonEvent> currentMouseButtonEvents = new ArrayList<>();
    private final boolean[] currentMouseButtonStates = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];
    private final boolean[] previousMouseButtonStates = new boolean[GLFW_MOUSE_BUTTON_LAST + 1];

    private boolean mouseCursorEnabled = true;
    private double currentMouseX = 0;
    private double currentMouseY = 0;
    private double previousMouseX = 0;
    private double previousMouseY = 0;

    public LogicFrameContextImpl(Engine engine, GlengCallbacksImpl glengCallbacks) {
        this.engine = engine;
        this.glengCallbacks = glengCallbacks;
    }

    void processEvents() {

        // fetch events
        currentKeyboardEvents.clear();
        currentMouseButtonEvents.clear();
        glengCallbacks.drainEventQueues(currentKeyboardEvents, currentMouseButtonEvents);

        // update mouse position
        if (mouseCursorEnabled) {
            previousMouseX = currentMouseX;
            previousMouseY = currentMouseY;
        } else {
            glfwSetCursorPos(Gleng.getWindowId(), 0, 0);
            previousMouseX = 0;
            previousMouseY = 0;
        }
        currentMouseX = glengCallbacks.getIncomingMouseX();
        currentMouseY = glengCallbacks.getIncomingMouseY();

        // update key state
        System.arraycopy(currentKeyStates, 0, previousKeyStates, 0, currentKeyStates.length);
        for (KeyboardEvent event : currentKeyboardEvents) {
            currentKeyStates[event.key()] = (event.action() == GLFW_PRESS || event.action() == GLFW_REPEAT);
        }

        // update mouse button state
        System.arraycopy(currentMouseButtonStates, 0, previousMouseButtonStates, 0, currentMouseButtonStates.length);
        for (MouseButtonEvent event : currentMouseButtonEvents) {
            currentMouseButtonStates[event.button()] = (event.action() == GLFW_PRESS || event.action() == GLFW_REPEAT);
        }

    }

    // ----------------------------------------------------------------------------------------------------------------
    // general
    // ----------------------------------------------------------------------------------------------------------------

    @Override
    public int getWidth() {
        return engine.getEngineParameters().glengParameters().width();
    }

    @Override
    public int getHeight() {
        return engine.getEngineParameters().glengParameters().height();
    }

    void setTimeDelta(double timeDelta) {
        this.timeDelta = timeDelta;
    }

    @Override
    public double getTimeDelta() {
        return timeDelta;
    }

    @Override
    public void setFrameHandler(FrameHandler frameHandler) {
        engine.setFrameHandler(frameHandler);
    }

    @Override
    public void shutdown() {
        shutdownRequested = true;
    }

    boolean isShutdownRequested() {
        return shutdownRequested;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // keyboard
    // ----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean isKeyDown(int key) {
        return currentKeyStates[key];
    }

    @Override
    public boolean isKeyNewlyDown(int key) {
        return currentKeyStates[key] && !previousKeyStates[key];
    }

    @Override
    public boolean isKeyNewlyUp(int key) {
        return !currentKeyStates[key] && previousKeyStates[key];
    }

    @Override
    public List<KeyboardEvent> getKeyboardEvents() {
        return currentKeyboardEvents;
    }

    // ----------------------------------------------------------------------------------------------------------------
    // mouse
    // ----------------------------------------------------------------------------------------------------------------


    @Override
    public void setMouseCursorEnabled(boolean enabled) {
        this.mouseCursorEnabled = enabled;
    }

    @Override
    public boolean isMouseButtonDown(int button) {
        return currentMouseButtonStates[button];
    }

    @Override
    public boolean isMouseButtonNewlyDown(int button) {
        return currentMouseButtonStates[button] && !previousMouseButtonStates[button];
    }

    @Override
    public boolean isMouseButtonNewlyUp(int button) {
        return !currentMouseButtonStates[button] && previousMouseButtonStates[button];
    }

    @Override
    public double getMouseX() {
        return currentMouseX;
    }

    @Override
    public double getMouseY() {
        return currentMouseY;
    }

    @Override
    public double getMouseDx() {
        return currentMouseX - previousMouseX;
    }

    @Override
    public double getMouseDy() {
        return currentMouseY - previousMouseY;
    }

}
