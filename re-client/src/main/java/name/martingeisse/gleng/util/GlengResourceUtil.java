package name.martingeisse.gleng.util;

import java.io.InputStream;

public final class GlengResourceUtil {

    private static void checkAbsolutePath(String path) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("classpath resource path must start with a slash or needs an anchor class: " + path);
        }
    }

    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String path) {
            super("classpath resource not found: " + path);
        }
        public ResourceNotFoundException(Class<?> anchor, String path) {
            super("classpath resource not found: " + anchor + " / " + path);
        }
    }

    public static class MalformedResourceException extends RuntimeException {
        public MalformedResourceException(String type, String path) {
            super("malformed classpath " + type + " resource: " + path);
        }
        public MalformedResourceException(String type, Class<?> anchor, String path) {
            super("malformed classpath " + type + " resource: " + anchor + " / " + path);
        }
    }

    // prevent instantiation
    private GlengResourceUtil() {
    }

    public static InputStream openClasspathResource(String path) {
        checkAbsolutePath(path);
        InputStream inputStream = GlengResourceUtil.class.getResourceAsStream(path);
        if (inputStream == null) {
            throw new ResourceNotFoundException(path);
        }
        return inputStream;
    }

    public static InputStream openClasspathResource(Class<?> anchor, String path) {
        InputStream inputStream = anchor.getResourceAsStream(path);
        if (inputStream == null) {
            throw new ResourceNotFoundException(anchor, path);
        }
        return inputStream;
    }

    public interface Loader<T> {
        T load(InputStream inputStream) throws Exception;
    }

    public static <T> T loadClasspathResource(String type, String path, Loader<T> loader) {
        try (InputStream inputStream = openClasspathResource(path)) {
            return loader.load(inputStream);
        } catch (Exception e) {
            throw new MalformedResourceException(type, path);
        }
    }

    public static <T> T loadClasspathResource(String type, Class<?> anchor, String path, Loader<T> loader) {
        try (InputStream inputStream = openClasspathResource(anchor, path)) {
            return loader.load(inputStream);
        } catch (Exception e) {
            throw new MalformedResourceException(type, anchor, path);
        }
    }

}
