package name.martingeisse.gleng.graphics;

import name.martingeisse.gleng.work_units.WorkUnits;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11C;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11C.*;

public final class Texture {

    private final int id;
    private final int width;
    private final int height;

    private Texture(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Invokes glBindTexture(). To be called from the OpenGL thread.
     */
    public void glBindTexture() {
        GL11C.glBindTexture(GL_TEXTURE_2D, id);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // factory methods
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Creates an OpenGL texture. To be called from the OpenGL thread.
     */
    public static int glXXX_register(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        ByteBuffer data = BufferUtils.createByteBuffer(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = bufferedImage.getRGB(x, y);
                data.put((byte) (argb >> 16));
                data.put((byte) (argb >> 8));
                data.put((byte) argb);
                data.put((byte) (argb >> 24));
            }
        }
        data.flip();
        int id = glGenTextures();
        GL11C.glBindTexture(GL_TEXTURE_2D, id);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
        return id;
    }

    public static Texture fromBufferedImage(BufferedImage bufferedImage) {
        int id = WorkUnits.scheduleAndWait(() -> glXXX_register(bufferedImage));
        return new Texture(id, bufferedImage.getWidth(), bufferedImage.getHeight());
    }

    public static Texture loadFromClasspath(Class<?> anchor, String path) {
        BufferedImage bufferedImage;
        try (InputStream inputStream = anchor.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("resource not found: " + path);
            }
            bufferedImage = ImageIO.read(inputStream);
        } catch (Exception e) {
            throw new RuntimeException("could not load texture file " + anchor + " / " + path, e);
        }
        return fromBufferedImage(bufferedImage);
    }

}
