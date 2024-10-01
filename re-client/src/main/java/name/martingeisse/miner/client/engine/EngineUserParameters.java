package name.martingeisse.miner.client.engine;

public record EngineUserParameters(int width, int height, boolean fullscreen) {

    public static EngineUserParameters parseCommandLine(String[] args, EngineUserParameters defaults) {
        int width = defaults.width();
        int height = defaults.height();
        boolean fullscreen = defaults.fullscreen();
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
        return new EngineUserParameters(width, height, fullscreen);
    }

}
