package name.martingeisse.miner.client.engine;

import name.martingeisse.gleng.GlengParameters;

public record EngineParameters(
        GlengParameters glengParameters,
        Integer fixedLogicFrameIntervalMilliseconds
) {
}
