package io.github.codemumbler.fittimer.model;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class FitCountDownTimerTest {

    public static final int TOTAL_TIME = 10000;
    public static final int ONE_SECOND = 1001;
    private FitHandler fitHandler;
    private FitCountDownTimer fitCountDownTimer;
    private MockClock clock;
    private boolean done = false;

    @Before
    public void setUp() {
        clock = new MockClock();
        fitHandler = new MockFitHandler();
        fitCountDownTimer = new FitCountDownTimer(TOTAL_TIME, fitHandler, clock);
        fitCountDownTimer.start();
    }

    @Test
    public void tickUpdatesDisplayValue() throws Exception {
        clock.tick();
        assertRemainingTimeAfterTick(9899);
    }

    @Test
    public void onFinishRanUntilCompletion() throws Exception {
        assertRemainingTimeAtFinish(0);

    }

    @Test
    public void pause() throws Exception {
        fitCountDownTimer.pause();
        assertTickCallbackIsNotCalled();
    }

    @Test
    public void resume() throws Exception {
        fitCountDownTimer.pause();
        clock.tick();
        fitCountDownTimer.start();
        clock.tick();
        assertRemainingTimeAfterTick(9899);
    }

    @Test
    public void onTargetTimeEvent() throws Exception {
        FitHandler.Callback handler = new FitHandler.Callback() {
            @Override
            public void execute(long remainingTime) {
                Assert.assertEquals(9, (long) Math.ceil(remainingTime/1000.0));
                done = true;
            }
        };
        fitHandler.addOnTargetTime(9000, handler);
        clock.setTickSize(ONE_SECOND);
        clock.tick();
        yield();
    }

    @Test
    public void onTargetTimeEventCalledOnce() throws Exception {
        FitHandler.Callback handler = new FitHandler.Callback() {
            @Override
            public void execute(long remainingTime) {
                Assert.assertEquals(9, (long) Math.ceil(remainingTime/1000.0));
                done = true;
            }
        };
        fitHandler.addOnTargetTime(9000, handler);
        clock.setTickSize(ONE_SECOND);
        clock.tick();
        yield();
    }

    @Test
    public void multipleTargetEvents() throws Exception {
        FitHandler.Callback nineSecondHandler = new FitHandler.Callback() {
            @Override
            public void execute(long remainingTime) {
                Assert.assertEquals(9, (long) Math.ceil(remainingTime/1000.0));
                done = true;
            }
        };
        FitHandler.Callback eightSecondHandler = new FitHandler.Callback() {
            @Override
            public void execute(long remainingTime) {
                Assert.assertEquals(8, (long) Math.ceil(remainingTime/1000.0));
                done = true;
            }
        };
        fitHandler.addOnTargetTime(9000, nineSecondHandler);
        fitHandler.addOnTargetTime(8000, eightSecondHandler);
        clock.setTickSize(ONE_SECOND);
        clock.tick();
        yield();
        done = false;
        clock.tick();
        yield();
    }

    @Test
    public void missingTargetEventIsIgnored() throws Exception {
        FitHandler.Callback elevenHandler = new FitHandler.Callback() {
            @Override
            public void execute(long remainingTime) {
                Assert.fail("Should not be called");
            }
        };
        FitHandler.Callback nineHandler = new FitHandler.Callback() {
            @Override
            public void execute(long remainingTime) {
                Assert.assertEquals(9, (long) Math.ceil(remainingTime/1000.0));
                done = true;
            }
        };
        fitHandler.addOnTargetTime(11000, elevenHandler);
        fitHandler.addOnTargetTime(9000, nineHandler);
        while (!done) {
            clock.tick();
            Thread.sleep(10);
        }
    }

    private void assertRemainingTimeAfterTick(final int expectedTime) {
        FitHandler.Callback handler = new FitHandler.Callback() {
            @Override
            public void execute(long remainingTime) {
                Assert.assertEquals(expectedTime, remainingTime);
                done = true;
            }
        };
        fitHandler.onTick(handler);
        yield();
    }

    private void yield() {
        while (!done) {
            Thread.yield();
        }
    }

    private void assertTickCallbackIsNotCalled() throws Exception {
        FitHandler.Callback handler = new FitHandler.Callback() {
            @Override
            public void execute(long remainingTime) {
                Assert.fail("Should not be called");
            }
        };
        fitHandler.onTick(handler);
        // give TimerTask time to be woken up
        for (int wait = 0; wait < 10; wait++) {
            clock.tick();
            Thread.sleep(10);
        }
    }

    private void assertRemainingTimeAtFinish(final int expectedTime) throws Exception {
        FitHandler.Callback handler = new FitHandler.Callback() {
            @Override
            public void execute(long actualTime) {
                Assert.assertEquals(expectedTime, actualTime);
                done = true;
            }
        };
        fitHandler.onFinish(handler);
        while (!done) {
            clock.tick();
            Thread.sleep(10);
        }
    }
}