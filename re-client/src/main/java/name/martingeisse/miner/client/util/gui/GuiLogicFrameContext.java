package name.martingeisse.miner.client.util.gui;

import name.martingeisse.miner.client.engine.FrameHandler;
import name.martingeisse.miner.client.engine.KeyboardEvent;
import name.martingeisse.miner.client.engine.LogicFrameContext;

import java.util.List;

public interface GuiLogicFrameContext extends LogicFrameContext {

    boolean isMouseObscured();

    static GuiLogicFrameContext from(LogicFrameContext logicFrameContext, boolean isMouseObscured) {
        return new GuiLogicFrameContext() {

            @Override
            public boolean isMouseObscured() {
                return isMouseObscured;
            }

            @Override
            public int getWidth() {
                return logicFrameContext.getWidth();
            }

            @Override
            public int getHeight() {
                return logicFrameContext.getHeight();
            }

            @Override
            public double getTimeDelta() {
                return logicFrameContext.getTimeDelta();
            }

            @Override
            public void setFrameHandler(FrameHandler frameHandler) {
                logicFrameContext.setFrameHandler(frameHandler);
            }

            @Override
            public void shutdown() {
                logicFrameContext.shutdown();
            }

            @Override
            public boolean isKeyDown(int key) {
                return logicFrameContext.isKeyDown(key);
            }

            @Override
            public boolean isKeyNewlyDown(int key) {
                return logicFrameContext.isKeyNewlyDown(key);
            }

            @Override
            public boolean isKeyNewlyUp(int key) {
                return logicFrameContext.isKeyNewlyUp(key);
            }

            @Override
            public List<KeyboardEvent> getKeyboardEvents() {
                return logicFrameContext.getKeyboardEvents();
            }

            @Override
            public void setMouseCursorEnabled(boolean enabled) {
                logicFrameContext.setMouseCursorEnabled(enabled);
            }

            @Override
            public boolean isMouseButtonDown(int button) {
                return logicFrameContext.isMouseButtonDown(button);
            }

            @Override
            public boolean isMouseButtonNewlyDown(int button) {
                return logicFrameContext.isMouseButtonNewlyDown(button);
            }

            @Override
            public boolean isMouseButtonNewlyUp(int button) {
                return logicFrameContext.isMouseButtonNewlyUp(button);
            }

            @Override
            public double getMouseX() {
                return logicFrameContext.getMouseX();
            }

            @Override
            public double getMouseY() {
                return logicFrameContext.getMouseY();
            }

            @Override
            public double getMouseDx() {
                return logicFrameContext.getMouseDx();
            }

            @Override
            public double getMouseDy() {
                return logicFrameContext.getMouseDy();
            }

        };
    }
}
