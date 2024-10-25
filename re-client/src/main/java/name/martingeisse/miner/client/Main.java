package name.martingeisse.miner.client;

import name.martingeisse.gleng.Gleng;
import name.martingeisse.gleng.GlengParameters;
import name.martingeisse.miner.client.engine.Engine;
import name.martingeisse.miner.client.engine.EngineParameters;
import name.martingeisse.miner.client.engine.GlengCallbacksImpl;
import name.martingeisse.miner.client.engine.gui.Gui;
import name.martingeisse.miner.client.engine.gui.GuiFrameHandler;
import name.martingeisse.miner.client.engine.gui.element.fill.FillColor;
import name.martingeisse.miner.client.engine.gui.util.Color;

public class Main {

    public static void main(String[] args) throws Exception {
        GlengParameters glengParameters = GlengParameters.from("Miner", 800, 600, false, args);
        GlengCallbacksImpl glengCallbacks = new GlengCallbacksImpl();
        Gleng.run(glengParameters, glengCallbacks, () -> {
            GuiFrameHandler guiFrameHandler = new GuiFrameHandler(800, 600);
            initializeGui(guiFrameHandler.getGui());
            var engineParameters = new EngineParameters(glengParameters, null);
            Engine engine = new Engine(engineParameters, glengCallbacks, guiFrameHandler);
            engine.executeFrameLoop();
        });
    }

    private static void initializeGui(Gui gui) {
        gui.setRootElement(new FillColor(Color.CYAN));
    }

}
