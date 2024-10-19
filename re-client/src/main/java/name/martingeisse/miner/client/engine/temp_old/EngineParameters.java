package name.martingeisse.miner.client.engine.temp_old;

public record EngineParameters(
        String windowTitle,
        Integer fixedFrameIntervalMilliseconds,
        EngineUserParameters userParameters
) {
}
