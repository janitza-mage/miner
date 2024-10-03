package name.martingeisse.miner.re_client;

import name.martingeisse.miner.client.engine.*;
import name.martingeisse.miner.client.engine.graphics.Texture;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL15.glEnable;
import static org.lwjgl.opengl.GL15.glTexCoordPointer;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;
import static org.lwjgl.system.MemoryUtil.memFree;

public class MyIntro {

    private static Texture texture;

    public static void main(String[] args) throws Exception {
        EngineUserParameters engineUserParameters = EngineUserParameters.parseCommandLine(args,
                new EngineUserParameters(800, 600, false)
        );
        EngineParameters parameters = new EngineParameters("MyIntro", null, engineUserParameters);
        try (Engine engine = new Engine(parameters, args, new FrameHandler() {

            @Override
            public void handleLogicFrame(LogicFrameContext context) {
                if (context.isMouseButtonNewlyDown(GLFW_MOUSE_BUTTON_LEFT)) {
                    System.out.println("Pressed!");
                }
                if (context.isKeyDown(GLFW_KEY_ESCAPE)) {
                    context.shutdown();
                }
            }

            @Override
            public void handleGraphicsFrame(GraphicsFrameContext context) {
                context.schedule(new GlWorkUnit() {
                    @Override
                    public void execute() {
                        glEnable(GL_TEXTURE_2D);
                        texture.glBindTexture();
                        glEnableClientState(GL_VERTEX_ARRAY);
                        glVertexPointer(2, GL_FLOAT, 0, 0L);
                        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
                        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
                        glDrawArrays(GL_TRIANGLES, 0, 3);
                    }
                });
            }

        })) {
            engine.addInitializationWorkUnit(new GlWorkUnit() {
                @Override
                public void execute() {

                    // load texture
                    texture = Texture.loadFromClasspath(MyIntro.class, "/bricks1.png");

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

                }
            });
            engine.run();
        }

    }

}
