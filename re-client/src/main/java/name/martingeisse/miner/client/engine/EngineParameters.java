package name.martingeisse.miner.client.engine;

public record EngineParameters(
        String windowTitle,
        Integer fixedFrameIntervalMilliseconds,
        EngineUserParameters userParameters
) {
}
