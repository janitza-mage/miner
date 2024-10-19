package name.martingeisse.miner.client.engine.temp_old;

import java.util.List;

public interface LogicFrameContext {

    // ----------------------------------------------------------------------------------------------------------------
    // general
    // ----------------------------------------------------------------------------------------------------------------

    int getWidth();

    int getHeight();

    double getTimeDelta();

    void setFrameHandler(FrameHandler frameHandler);

    void shutdown();

    // ----------------------------------------------------------------------------------------------------------------
    // keyboard
    // ----------------------------------------------------------------------------------------------------------------

    boolean isKeyDown(int key);

    boolean isKeyNewlyDown(int key);

    boolean isKeyNewlyUp(int key);

    List<KeyboardEvent> getKeyboardEvents();

    // ----------------------------------------------------------------------------------------------------------------
    // mouse
    // ----------------------------------------------------------------------------------------------------------------

    void setMouseCursorEnabled(boolean enabled);

    boolean isMouseButtonDown(int button);

    boolean isMouseButtonNewlyDown(int button);

    boolean isMouseButtonNewlyUp(int button);

    double getMouseX();

    double getMouseY();

    double getMouseDx();

    double getMouseDy();

}
