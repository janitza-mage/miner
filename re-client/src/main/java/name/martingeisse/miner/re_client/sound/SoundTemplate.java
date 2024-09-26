package name.martingeisse.miner.re_client.sound;

import java.io.IOException;
import java.io.InputStream;

/**
 * A static sound template, i.e. a fixed buffer with sound data, that can be played.
 */
public final class SoundTemplate {

    public static SoundTemplate loadClasspathOgg(Class<?> anchor, String path) throws IOException {
        try (InputStream inputStream = anchor.getResourceAsStream(path)) {
            // return AudioLoader.getAudio("OGG", inputStream);
            return new SoundTemplate();
        }
    }

    public void play() {
        // TODO
    }

}
