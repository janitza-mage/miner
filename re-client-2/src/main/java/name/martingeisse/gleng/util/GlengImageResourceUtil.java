package name.martingeisse.gleng.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public final class GlengImageResourceUtil {

    // prevent instantiation
    private GlengImageResourceUtil() {
    }

    public static BufferedImage loadClasspathImageResource(String path) {
        return GlengResourceUtil.loadClasspathResource("image", path, ImageIO::read);
    }

    public static BufferedImage loadClasspathImageResource(Class<?> anchor, String path) {
        return GlengResourceUtil.loadClasspathResource("image", anchor, path, ImageIO::read);
    }

}
