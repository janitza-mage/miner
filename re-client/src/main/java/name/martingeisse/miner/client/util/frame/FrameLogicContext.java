package name.martingeisse.miner.client.util.frame;

public interface FrameLogicContext {

    boolean isKeyDown(int key);

    boolean isKeyNewlyDown(int key);

    boolean isKeyNewlyUp(int key);

    boolean isMouseButtonDown(int button);

    boolean isMouseButtonNewlyDown(int button);

    boolean isMouseButtonNewlyUp(int button);

    double getMouseX();

    double getMouseY();

    double getMouseDx();

    double getMouseDy();

}
