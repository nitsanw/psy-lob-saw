package pingpong;

import static java.lang.System.out;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import util.Counter;
import util.Counters;

public final class PingPong {
    private static final long ITERATIONS = 10L * 1000L * 1000L;

    private volatile CountDownLatch latch = new CountDownLatch(2);
    private final Counter pingValue;
    private final Counter pongValue;

    public PingPong(Counter pingValue, Counter pongValue) {
	super();
	this.pingValue = pingValue;
	this.pongValue = pongValue;
    }

    public long runOnce() throws InterruptedException {
	pingValue.set(-1L);
	pongValue.set(-1L);
	latch = new CountDownLatch(2);
	Thread pongThread = new Thread(new PongRunner(pingValue, pongValue,
		ITERATIONS));
	Thread pingThread = new Thread(new PingRunner(pingValue, pongValue,
		ITERATIONS));
	pongThread.start();
	pingThread.start();
	latch.await();
	long start = System.nanoTime();
	pongThread.join();
	long duration = System.nanoTime() - start;
	if (pingValue.get() == pongValue.get()) {
	    return duration;
	} else {
	    throw new RuntimeException();
	}
	// duration(ns),op-cost(ns),ops/sec,piV,poV
	// out.printf("%d,%d,%d,%d,%d\n", duration, duration / (ITERATIONS *
	// 2L),
	// (ITERATIONS * 2L * 1000000000L) /
	// duration,pingValue.get(),pongValue.get());
    }

    public final class PingRunner implements Runnable {
	private final Counter pingValue;
	private final Counter pongValue;
	private final long iterations;

	public PingRunner(Counter pingValue, Counter pongValue, long iterations) {
	    super();
	    this.pingValue = pingValue;
	    this.pongValue = pongValue;
	    this.iterations = iterations;
	}

	public void run() {
	    latch.countDown();
	    for (long l = 0; l < iterations; l++) {
		pingValue.set(l);
		while (pongValue.get() != l)
		    ;
	    }
	}
    }

    public final class PongRunner implements Runnable {
	private final Counter pingValue;
	private final Counter pongValue;
	private final long iterations;

	public PongRunner(Counter pingValue, Counter pongValue, long iterations) {
	    super();
	    this.pingValue = pingValue;
	    this.pongValue = pongValue;
	    this.iterations = iterations;
	}

	public void run() {
	    latch.countDown();
	    for (long l = 0; l < iterations; l++) {
		while (pingValue.get() != l)
		    ;
		pongValue.set(l);
	    }
	}
    }

    public static void main(String[] args) throws Exception {
	PingPong experiment = new PingPong(Counters.createCounter(args),
		Counters.createCounter(args));
	long[] times = new long[20];
	for (int i = 0; i < 20; i++) {
	    times[i] = experiment.runOnce();
	}
	Arrays.sort(times);
	out.printf("%s, %s, %s, %d, %d, %d\n", args[0], args[1], args[2],
		times[0], times[10], times[19]);
    }
}
