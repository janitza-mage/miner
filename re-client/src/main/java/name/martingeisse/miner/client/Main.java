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
import name.martingeisse.miner.client.engine.gui.control.TextField;
import name.martingeisse.miner.client.engine.gui.element.atom.Spacer;
import name.martingeisse.miner.client.engine.gui.element.atom.TextLine;
import name.martingeisse.miner.client.engine.gui.element.collection.OverlayStack;
import name.martingeisse.miner.client.engine.gui.element.collection.VerticalLayout;
import name.martingeisse.miner.client.engine.gui.element.fill.FillTexture;
import name.martingeisse.miner.client.engine.gui.element.wrapper.Border;
import name.martingeisse.miner.client.engine.gui.util.AreaAlignment;
import name.martingeisse.miner.client.engine.gui.util.HorizontalAlignment;
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

        var texture = Texture.loadFromClasspath(TriangleMain.class, "/bricks1.png");

        var layout = new VerticalLayout();
        layout.setAlignment(HorizontalAlignment.LEFT);
        layout.addElement(new Spacer(20));
        layout.addElement(new TextLine().setText("Hello"));
        layout.addElement(new Border(new TextLine().setText("worldddd")).setThickness(5));
        layout.addElement(new TextLine().setText("again"));
        layout.addElement(new TextField().setValue("editable text field one"));
        layout.addElement(new TextLine().setText("again"));
        layout.addElement(new TextField().setValue("editable text field two"));

        var stack = new OverlayStack();
        stack.setAlignment(AreaAlignment.TOP_LEFT);
        stack.addElement(new FillTexture(texture));
        stack.addElement(layout);

        gui.setRootElement(stack);
    }

    private static final GlWorkUnit clearScreenWorkUnit = new GlWorkUnit() {
        @Override
        protected void gl__Execute() {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        }
    };
}
