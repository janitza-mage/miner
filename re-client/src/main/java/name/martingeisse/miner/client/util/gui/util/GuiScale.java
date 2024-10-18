package name.martingeisse.miner.client.util.gui.util;

public record GuiScale(int heightPixels) {

    /**
     * Total height of the screen in GUI units. This value is fixed to be resolution-independent, and the width is
     * determined from the aspect ratio.
     */
    public static final int HEIGHT_UNITS = 100000;

    /**
     * The "normal" grid to align things. The total height is 100 grid clicks.
     */
    public static final int GRID = 1000;

    /**
     * The "mini" grid to align things. The total height is 1000 mini-grid clicks.
     */
    public static final int MINI_GRID = 100;

    /**
     * Converts coordinate units to pixels.
     */
    public int unitsToPixelsInt(int units) {
        return (int) (units * (long) heightPixels / HEIGHT_UNITS);
    }

    /**
     * Converts pixels to coordinate units.
     */
    public int pixelsToUnitsInt(int pixels) {
        return (int) (pixels * (long) HEIGHT_UNITS / heightPixels);
    }

    /**
     * Converts coordinate units to pixels.
     */
    public float unitsToPixelsFloat(float units) {
        return units * heightPixels / HEIGHT_UNITS;
    }

    /**
     * Converts pixels to coordinate units.
     */
    public float pixelsToUnitsFloat(float pixels) {
        return pixels * HEIGHT_UNITS / heightPixels;
    }

    /**
     * Converts coordinate units to pixels.
     */
    public double unitsToPixelsDouble(double units) {
        return units * heightPixels / HEIGHT_UNITS;
    }

    /**
     * Converts pixels to coordinate units.
     */
    public double pixelsToUnitsDouble(double pixels) {
        return pixels * HEIGHT_UNITS / heightPixels;
    }

}
