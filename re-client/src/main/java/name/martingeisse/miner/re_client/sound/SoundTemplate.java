package name.martingeisse.miner.re_client.sound;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * A static sound template, i.e. a fixed buffer with sound data, that can be played. A single template can be re-used
 * to play its sound multiple times, including multiple times at once (TODO this doesn't work yet -- see the play()
 * method).
 */
public final class SoundTemplate implements AutoCloseable {

    private final Clip clip;

    private SoundTemplate(Clip clip) {
        this.clip = clip;
    }

    public void play() {
        // TODO: this won't be able to play the same sound template multiple times at once -- which a template and
        // its play() method are supposed to do -- but for now this is enough.
        clip.setFramePosition(0);
        clip.start();
    }

    @Override
    public void close() {
        clip.close();
    }

    public static SoundTemplate loadFromClasspath(Class<?> anchor, String path) {
        try (InputStream inputStream = anchor.getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IOException("resource not found: " + path);
            }
            try (AudioInputStream ais = AudioSystem.getAudioInputStream(inputStream)) {
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                return new SoundTemplate(clip);
            }
        } catch (Exception e) {
            throw new RuntimeException("could not load sound file " + anchor + " / " + path, e);
        }
    }

}
