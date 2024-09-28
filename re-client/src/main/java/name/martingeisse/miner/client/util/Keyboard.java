package name.martingeisse.miner.client.util;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Keyboard instances are single-threaded.
 * Only a single instance is currently supported. Multiple instances would steal events from each other.
 */
public final class Keyboard {

    public record Event(int key, int scancode, int action, int mods) {}

    private static final BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<>();

    private static void onKey(long windowId, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE) {
            glfwSetWindowShouldClose(windowId, true);
        } else if (key >= 0) {
            eventQueue.add(new Event(key, scancode, action, mods));
        }
    }

    public static void installCallback(long windowId) {
        glfwSetKeyCallback(windowId, Keyboard::onKey);
    }

    private final boolean[] currentKeyStates = new boolean[GLFW_KEY_LAST + 1];
    private final boolean[] previousKeyStates = new boolean[GLFW_KEY_LAST + 1];

    public void update() {
        ArrayList<Event> incomingEvents = new ArrayList<>();
        eventQueue.drainTo(incomingEvents);
        System.arraycopy(currentKeyStates, 0, previousKeyStates, 0, currentKeyStates.length);
        for (Event event : incomingEvents) {
            currentKeyStates[event.key] = (event.action == GLFW_PRESS || event.action == GLFW_REPEAT);
        }
    }

    public boolean isDown(int key) {
        return currentKeyStates[key];
    }

    public boolean isNewlyDown(int key) {
        return currentKeyStates[key] && !previousKeyStates[key];
    }

    public boolean isNewlyUp(int key) {
        return !currentKeyStates[key] && previousKeyStates[key];
    }

}
