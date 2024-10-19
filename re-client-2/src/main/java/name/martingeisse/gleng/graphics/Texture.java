package name.martingeisse.gleng.graphics;

import name.martingeisse.gleng.gl_util.GlTextureUtil;
import name.martingeisse.gleng.util.GlengResourceUtil;
import name.martingeisse.gleng.work_units.WorkUnits;
import org.lwjgl.opengl.GL11C;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

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

    public static Texture loadFromClasspath(String path) {
        return GlengResourceUtil.loadClasspathResource("texture", path, in -> fromBufferedImage(ImageIO.read(in)));
    }

    public static Texture loadFromClasspath(Class<?> anchor, String path) {
        return GlengResourceUtil.loadClasspathResource("texture", anchor, path, in -> fromBufferedImage(ImageIO.read(in)));
    }

}
