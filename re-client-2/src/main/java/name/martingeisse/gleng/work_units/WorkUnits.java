package name.martingeisse.gleng.work_units;

import name.martingeisse.gleng.GlWorkUnit;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public final class WorkUnits {

    private static final GlWorkUnit NOP_WORK_UNIT = new GlWorkUnit() {
        @Override
        public void execute() {
        }
    };

    public static GlWorkUnit nop() {
        return NOP_WORK_UNIT;
    }

    public static void scheduleAndForget(Runnable body) {
        new GlWorkUnit() {
            @Override
            public void execute() {
                body.run();
            }
        }.schedule();
    }

    public static void scheduleAndWait(Runnable body) {
        final CountDownLatch latch = new CountDownLatch(1);
        new GlWorkUnit() {
            @Override
            public void execute() {
                body.run();
                latch.countDown();
            }
        }.schedule();
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted", e);
        }
    }

    public static <T> T scheduleAndWait(Supplier<T> body) {
        final AtomicReference<T> result = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        new GlWorkUnit() {
            @Override
            public void execute() {
                result.set(body.get());
                latch.countDown();
            }
        }.schedule();
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted", e);
        }
        return result.get();
    }

}
