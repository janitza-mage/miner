package name.martingeisse.miner.re_client;

import name.martingeisse.miner.client.engine.graphics.Texture;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL15.glEnable;
import static org.lwjgl.opengl.GL15.glTexCoordPointer;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.*;

public class MyIntro {

    public static void main(String[] args) {

        // initialize
        if (!glfwInit()) {
            throw new RuntimeException("could not initialize GLFW");
        }
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        long window = glfwCreateWindow(800, 600, "MyIntro", NULL, NULL);
        glfwMakeContextCurrent(window);
        createCapabilities();

        // load texture
        Texture texture = Texture.loadFromClasspath(MyIntro.class, "/bricks1.png");

        // create and fill JVM buffer
        FloatBuffer buffer = memAllocFloat(3 * 2);
        buffer.put(-0.5f).put(-0.5f);
        buffer.put(+0.5f).put(-0.5f);
        buffer.put(+0.0f).put(+0.5f);
        buffer.flip();

        // create and fill OpenGL buffer
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        // respects position and limit but does not modify the position
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        // free the JVM buffer
        memFree(buffer);

        // register a mouse callback for demonstration purposes.
        // Note: returned value si a resource that should be freed!
        //noinspection resource
        glfwSetMouseButtonCallback(window, (long win, int button, int action, int mods) -> {
            if (action == GLFW_PRESS) {
                System.out.println("Pressed!");
            }
        });

        // prepare texture
        glEnable(GL_TEXTURE_2D);
        texture.bind();

        // draw rotating triangle until stopped by the user
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(2, GL_FLOAT, 0, 0L);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();
            glDrawArrays(GL_TRIANGLES, 0, 3);
            glfwSwapBuffers(window);
        }

        // shutdown
        glfwTerminate();

    }

}
