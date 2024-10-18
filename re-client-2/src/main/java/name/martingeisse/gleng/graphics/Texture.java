package name.martingeisse.gleng.graphics;

import name.martingeisse.gleng.gl_util.GlTextureUtil;
import name.martingeisse.gleng.work_units.WorkUnits;
import org.lwjgl.opengl.GL11C;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;

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

    public static Texture fromBufferedImage(BufferedImage bufferedImage) {
        int id = WorkUnits.scheduleAndWait(() -> GlTextureUtil.createTexture(bufferedImage));
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
