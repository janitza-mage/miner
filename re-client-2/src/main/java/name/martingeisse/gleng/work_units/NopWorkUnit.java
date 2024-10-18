package name.martingeisse.gleng.work_units;

import name.martingeisse.gleng.GlWorkUnit;

public final class NopWorkUnit extends GlWorkUnit {

    public static final NopWorkUnit INSTANCE = new NopWorkUnit();

    @Override
    public void execute() {
    }

}
