package name.martingeisse.gleng;

/**
 * These callbacks will be called from the OpenGL thread, so they must be properly isolated from the application thread.
 */
public interface GlengCallbacks {

    void onKeyEvent(int key, int scancode, int action, int mods);

    void onMousePositionEvent(double x, double y);

    void onMouseButtonEvent(int button, int action, int mods);

}
