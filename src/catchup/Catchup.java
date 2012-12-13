package catchup;

import static java.lang.System.out;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import util.Counter;
import util.Counters;

public final class Catchup {
    private static final long ITERATIONS = 100L * 1000L * 1000L;
    
    private static final long[] values = new long[(int) ITERATIONS];
    private Counter producerIndex;
    private long consumerIndex = -1L;

    private volatile CountDownLatch latch = new CountDownLatch(2);

    public Catchup(Counter counterType) throws Exception {
	producerIndex = counterType;
    }

    private long runOnce() throws InterruptedException {
	producerIndex.set(-1);
	consumerIndex = -1;
	Arrays.fill(values, 0, (int) ITERATIONS, 0L);
	latch = new CountDownLatch(2);
	Thread pongThread = new Thread(new Consumer());
	Thread pingThread = new Thread(new Producer());
	pongThread.start();
	pingThread.start();
	
	latch.await();

	long start = System.nanoTime();

	pongThread.join();

	long duration = System.nanoTime() - start;

	return duration;
	// executions,duration(ns),ns/op,ops/sec,catchV,coughtV
//	out.printf("duration %,d (ns);", duration);
//	out.printf("%,d ns/op;", duration / ITERATIONS);
//	out.printf("%,d ops/s;", (ITERATIONS * 1000000000L) / duration);
//	out.println("pingValue = " + catchValue + ", pongValue = " + catchupValue);
    }

    final class Producer implements Runnable {
	public void run() {
	    latch.countDown();
	    for (long l = 0; l < ITERATIONS; l++) {
		values[(int) l] = l;
		producerIndex.set(l);
	    }
	}
    }

    final class Consumer implements Runnable {
	public void run() {
	    latch.countDown();
	    long executions = 0;
	    while (consumerIndex < ITERATIONS - 1) {
		executions++;
		long l = producerIndex.get();
		while (consumerIndex < l) {
		    consumerIndex++;
		    if (values[(int) l] != l)
			System.out.println("Abort!");
		}
	    }
//	    out.printf("executions %,d;\t", executions);
	}
    }
    public static void main(String[] args) throws Exception {
	Catchup experiment = new Catchup(Counters.createCounter(args));
	long[] times = new long[20];
	for (int i = 0; i < 20; i++) {
	    times[i] = experiment.runOnce();
	}
	Arrays.sort(times);
	out.printf("%s, %s, %s, %d, %d, %d\n",args[0],args[1],args[2],times[0],times[10],times[19]);
    }
}
