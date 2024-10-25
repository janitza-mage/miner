package name.martingeisse.miner.client;

import name.martingeisse.gleng.GlWorkUnit;
import name.martingeisse.gleng.Gleng;
import name.martingeisse.gleng.GlengParameters;
import name.martingeisse.gleng.graphics.FixedWidthFont;
import name.martingeisse.gleng.graphics.Texture;
import name.martingeisse.gleng.util.GlengImageResourceUtil;
import name.martingeisse.miner.client.engine.*;
import name.martingeisse.miner.client.engine.gui.Gui;
import name.martingeisse.miner.client.engine.gui.GuiFrameHandler;
import name.martingeisse.miner.client.engine.gui.element.atom.TextLine;
import name.martingeisse.miner.client.engine.gui.element.atom.TextParagraph;
import name.martingeisse.miner.client.engine.gui.element.fill.FillTexture;
import name.martingeisse.miner.client.engine.gui.element.fill.PulseFillColor;
import name.martingeisse.miner.client.engine.gui.util.GuiScale;
import org.lwjgl.opengl.GL11;

public class Main {

    public static void main(String[] args) throws Exception {
        GlengParameters glengParameters = GlengParameters.from("Miner", 800, 600, false, args);
        GlengCallbacksImpl glengCallbacks = new GlengCallbacksImpl();
        Gleng.run(glengParameters, glengCallbacks, () -> {

            GuiFrameHandler guiFrameHandler = new GuiFrameHandler(800, 600);
            initializeGui(guiFrameHandler.getGui());

            var myFrameHandler = new FrameHandler() {

                @Override
                public void handleLogicFrame(LogicFrameContext context) {
                    guiFrameHandler.handleLogicFrame(context);
                }

                @Override
                public void handleGraphicsFrame() {
                    clearScreenWorkUnit.schedule();
                    guiFrameHandler.handleGraphicsFrame();
                }

            };

            var engineParameters = new EngineParameters(glengParameters, null);
            Engine engine = new Engine(engineParameters, glengCallbacks, myFrameHandler);
            engine.executeFrameLoop();
        });
    }

    private static void initializeGui(Gui gui) {
        gui.setDefaultFont(new FixedWidthFont(GlengImageResourceUtil.loadClasspathImageResource("/font.png"), 8, 16));

        // var texture = Texture.loadFromClasspath(TriangleMain.class, "/bricks1.png");
        gui.setRootElement(new TextParagraph().setText("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec mauris metus, iaculis id purus sed, consequat placerat orci. Nulla eu condimentum sapien, ut consequat est. Aliquam hendrerit volutpat ligula, at tincidunt justo ullamcorper a. Nulla feugiat, nisl nec commodo venenatis, quam lorem tincidunt metus, in pharetra nunc ligula a lorem. Etiam pellentesque augue erat, in venenatis tellus laoreet non. Nulla facilisi. Aliquam nec dui vel orci aliquam feugiat. Vestibulum sed sapien accumsan, scelerisque nulla quis, consequat quam. Nulla commodo est non eros interdum malesuada."));
    }

    private static final GlWorkUnit clearScreenWorkUnit = new GlWorkUnit() {
        @Override
        protected void gl__Execute() {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        }
    };
}
