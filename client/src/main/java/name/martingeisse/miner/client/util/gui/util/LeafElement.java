package name.martingeisse.miner.client.util.gui.util;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.client.util.gui.GuiElement;

/**
 * This class makes it easier to implement leaf elements by implementing all children-related methods as no-ops.
 * Note that not all leaf elements inherit from this class.
 */
public abstract class LeafElement extends GuiElement {

    @Override
    public final ImmutableList<GuiElement> getChildren() {
        return ImmutableList.of();
    }

    // not final, because there might be other reasons than children to react to position changes
    @Override
    protected void onAbsolutePositionChanged(int absoluteX, int absoluteY) {
    }

}
