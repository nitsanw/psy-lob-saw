package catchup;

import static java.lang.System.out;

import java.util.concurrent.CountDownLatch;

import util.Counter;
import util.Counters;
import util.DirectLong;

public final class Catchup {
    private static final long ITERATIONS = 10L * 1000L * 1000L;
    private static final long[] values = new long[(int) ITERATIONS];
    private static Counter catchValue;
    private static long catchupValue = -1L;

    private static volatile CountDownLatch latch = new CountDownLatch(2);

    public Catchup(Counter counterType) throws Exception {
	catchValue = counterType;
	for (int i = 0; i < 20; i++) {
	    catchValue.set(-1);
	    catchupValue = -1;
	    runOnce();
	}
    }

    private static void runOnce() throws InterruptedException {
	latch = new CountDownLatch(2);
	Thread pongThread = new Thread(new CatchingUp());
	Thread pingThread = new Thread(new CatchMe());
	pongThread.start();
	pingThread.start();

	latch.await();

	long start = System.nanoTime();

	pongThread.join();

	long duration = System.nanoTime() - start;

	// executions,duration(ns),ns/op,ops/sec,catchV,coughtV
	out.printf("duration %,d (ns);", duration);
	out.printf("%,d ns/op;", duration / ITERATIONS);
	out.printf("%,d ops/s;", (ITERATIONS * 1000000000L) / duration);
	out.println("pingValue = " + catchValue + ", pongValue = " + catchupValue);
    }

    public static final class CatchMe implements Runnable {
	public void run() {
	    latch.countDown();
	    for (long l = 0; l < ITERATIONS; l++) {
		values[(int) l] = l;
		catchValue.set(l);
	    }
	}
    }

    public static final class CatchingUp implements Runnable {
	public void run() {
	    latch.countDown();
	    long executions = 0;
	    while (catchupValue < ITERATIONS - 1) {
		executions++;
		long l = catchValue.get();
		while (catchupValue < l) {
		    catchupValue++;
		    if (values[(int) catchupValue] != catchupValue)
			System.out.println("Abort!");
		}
	    }
	    out.printf("executions %,d;\t", executions);
	}
    }
    public static void main(String[] args) throws Exception {
	new Catchup(Counters.createCounter(args));
    }
}
