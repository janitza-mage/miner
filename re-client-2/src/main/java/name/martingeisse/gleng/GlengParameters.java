package name.martingeisse.gleng;

public record GlengParameters(String windowTitle, int width, int height, boolean fullscreen) {

    public static GlengParameters from(
            String windowTitle,
            int defaultWidth,
            int defaultHeight,
            boolean defaultFullscreen,
            String[] args
    ) {
        int width = defaultWidth;
        int height = defaultHeight;
        boolean fullscreen = defaultFullscreen;
        for (final String arg : args) {
            switch (arg) {

                case "-fs" -> fullscreen = true;

                case "-640" -> {
                    width = 640;
                    height = 480;
                }

                case "-800" -> {
                    width = 800;
                    height = 600;
                }

                case "-1024" -> {
                    width = 1024;
                    height = 768;
                }

                case "-1280" -> {
                    width = 1280;
                    height = 720;
                }

                case "-1680" -> {
                    width = 1680;
                    height = 1050;
                }

            }
        }
        return new GlengParameters(windowTitle, width, height, fullscreen);
    }

}
