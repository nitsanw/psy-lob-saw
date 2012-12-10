package catchup;

import static java.lang.System.out;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import util.PaddedLong;

public final class SequenceOrderedSetCatchup {
    private static final long ITERATIONS = 100L * 1000L * 1000L;
    private static final long[] values = new long[(int) ITERATIONS];
    private static PaddedLong pingValue = new PaddedLong(-1L);
    private static long pongValue = -1L;

    private static final CountDownLatch latch = new CountDownLatch(2);

    public static void main(final String[] args) throws Exception {
	for (int i = 0; i < 20; i++) {
	    pingValue.setVolatile(-1);
	    pongValue = -1;
	    runOnce();
	}
    }

    private static void runOnce() throws InterruptedException {
	Thread pongThread = new Thread(new PongRunner());
	Thread pingThread = new Thread(new PingRunner());
	pongThread.start();
	pingThread.start();

	latch.await();

	long start = System.nanoTime();

	pongThread.join();

	long duration = System.nanoTime() - start;

	out.printf("duration %,d (ns);", duration);
	out.printf("%,d ns/op;", duration / ITERATIONS);
	out.printf("%,d ops/s;", (ITERATIONS * 1000000000L) / duration);
	out.println("pingValue = " + pingValue + ", pongValue = " + pongValue);
    }

    public static final class PingRunner implements Runnable {
	public void run() {
	    latch.countDown();
	    for (long l = 0; l < ITERATIONS; l++) {
		values[(int) l] = l;
		pingValue.setOrdered(l);
	    }
	}
    }

    public static final class PongRunner implements Runnable {
	public void run() {
	    latch.countDown();
	    long executions = 0;
	    while (pongValue < ITERATIONS - 1) {
		executions++;
		long l = pingValue.getVolatile();
		while (pongValue < l) {
		    pongValue++;
		    if (values[(int) pongValue] != pongValue)
			System.out.print("sds");
		}
	    }
	    out.printf("executions %,d;\t", executions);
	}
    }
}
