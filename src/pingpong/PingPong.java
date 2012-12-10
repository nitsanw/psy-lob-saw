package pingpong;

import static java.lang.System.out;

import java.util.concurrent.CountDownLatch;

public final class PingPong {
    private static final long WARMUP_ITERATIONS = 100L * 1000L;
    private static final long ITERATIONS = 10L * 1000L * 1000L;


    private static volatile CountDownLatch latch = new CountDownLatch(2);

    public PingPong(PingPongCounter pingValue, PingPongCounter pongValue)
	    throws Exception {
	Thread pongThread = new Thread(new PongRunner(pingValue, pongValue, WARMUP_ITERATIONS));
	Thread pingThread = new Thread(new PingRunner(pingValue, pongValue, WARMUP_ITERATIONS));
	pongThread.start();
	pingThread.start();
	latch.await();
	pongThread.join();
	pingValue.set(-1L);
	pongValue.set(-1L);
	latch = new CountDownLatch(2);
	pongThread = new Thread(new PongRunner(pingValue, pongValue, ITERATIONS));
	pingThread = new Thread(new PingRunner(pingValue, pongValue, ITERATIONS));
	pongThread.start();
	pingThread.start();
	latch.await();
	long start = System.nanoTime();
	pongThread.join();
	long duration = System.nanoTime() - start;

	// duration(ns),op-cost(ns),ops/sec,piV,poV
	out.printf("%d,\t%d,\t%d,\t%d,\t%d\n", duration, duration / (ITERATIONS * 2L), 
		(ITERATIONS * 2L * 1000000000L) / duration,pingValue.get(),pongValue.get());
    }

    public static final class PingRunner implements Runnable {
	private final PingPongCounter pingValue;
	private final PingPongCounter pongValue;
	private final long iterations;
	public PingRunner(PingPongCounter pingValue, PingPongCounter pongValue, long iterations) {
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

    public static final class PongRunner implements Runnable {
	private final PingPongCounter pingValue;
	private final PingPongCounter pongValue;
	private final long iterations;
	public PongRunner(PingPongCounter pingValue, PingPongCounter pongValue, long iterations) {
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
}
