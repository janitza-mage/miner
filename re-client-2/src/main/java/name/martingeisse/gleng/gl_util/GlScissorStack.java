package name.martingeisse.gleng.gl_util;

import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Maintains a stack of scissor rectangles. This is useful because OpenGL alone can only set the scissor rectangle to
 * a new size, but not restore it to its previous size.
 */
public final class GlScissorStack {

    private record Rectangle(int x, int y, int width, int height) {
    }

    private static final Deque<Rectangle> stack = new ArrayDeque<>();

    public static void gl__Push(int x, int y, int width, int height) {
        stack.push(new Rectangle(x, y, width, height));
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x, y, width, height);
    }

    public static void gl__Pop() {
        stack.pop();
        if (stack.isEmpty()) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            Rectangle rectangle = stack.peek();
            GL11.glScissor(rectangle.x(), rectangle.y(), rectangle.width(), rectangle.height());
        }
    }

}
