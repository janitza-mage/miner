package name.martingeisse.gleng;

public interface GlengCallbacks {

    void onKeyEvent(int key, int scancode, int action, int mods);

    void onMousePositionEvent(double x, double y);

    void onMouseButtonEvent(int button, int action, int mods);

}
