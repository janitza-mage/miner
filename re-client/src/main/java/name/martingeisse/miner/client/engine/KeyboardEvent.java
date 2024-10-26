package name.martingeisse.miner.client.engine;

import org.lwjgl.glfw.GLFW;

public record KeyboardEvent(Type type, int codeOrCharacter, int mods) {

    public enum Type {
        KEY_DOWN,
        KEY_UP,
        KEY_REPEAT,
        CHARACTER;

        static Type fromGlfwAction(int action) {
            if (action == GLFW.GLFW_PRESS) {
                return KEY_DOWN;
            } else if (action == GLFW.GLFW_RELEASE) {
                return KEY_UP;
            } else if (action == GLFW.GLFW_REPEAT) {
                return KEY_REPEAT;
            } else {
                return null;
            }
        }

    }

}
