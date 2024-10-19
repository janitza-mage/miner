package name.martingeisse.miner.client.engine;

import com.google.common.util.concurrent.AtomicDouble;
import name.martingeisse.gleng.GlengCallbacks;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class GlengCallbacksImpl implements GlengCallbacks {

    private final BlockingQueue<KeyboardEvent> keyboardEventQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<MouseButtonEvent> mouseButtonEventQueue = new LinkedBlockingQueue<>();
    private final AtomicDouble incomingMouseX = new AtomicDouble(0);
    private final AtomicDouble incomingMouseY = new AtomicDouble(0);

    @Override
    public void onKeyEvent(int key, int scancode, int action, int mods) {
        keyboardEventQueue.add(new KeyboardEvent(key, scancode, action, mods));
    }

    @Override
    public void onMousePositionEvent(double x, double y) {
        incomingMouseX.set(x);
        incomingMouseY.set(y);
    }

    @Override
    public void onMouseButtonEvent(int button, int action, int mods) {
        mouseButtonEventQueue.add(new MouseButtonEvent(button, action, mods));
    }

    void drainEventQueues(List<KeyboardEvent> keyboardEventDestination, List<MouseButtonEvent> mouseButtonEventDestination) {
        keyboardEventQueue.drainTo(keyboardEventDestination);
        mouseButtonEventQueue.drainTo(mouseButtonEventDestination);
    }

    double getIncomingMouseX() {
        return incomingMouseX.get();
    }

    double getIncomingMouseY() {
        return incomingMouseY.get();
    }

}
