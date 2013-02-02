package lazyset;

import util.MemoryLong;
import util.OffHeapLong;
import util.VolatileLong;
import util.VolatileLong2;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

public class MemoryPutCostBenchmark extends SimpleBenchmark {
    private static final int VAR_NUM = 10;
    @Param({ "offheap", "volatile1", "volatile2" })
    String counterType;
    private MemoryLong[] counter = new MemoryLong[VAR_NUM];

    @Override
    protected void setUp() throws Exception {
	if (counterType.equals("volatile1"))
	    for (int i = 0; i < counter.length; i++)
		counter[i] = new VolatileLong();
	else if (counterType.equals("volatile2"))
	    for (int i = 0; i < counter.length; i++)
		counter[i] = new VolatileLong2();
	else if (counterType.equals("offheap"))
	    for (int i = 0; i < counter.length; i++)
		counter[i] = new OffHeapLong();
	else
	    throw new IllegalArgumentException();
    }

    public int timePutVolatile(final int reps) throws InterruptedException {
	for (int i = 0; i < counter.length; i++)
	    counter[i].directSet(0L);
	Thread t = waitForLastCounter(reps);

	for (int i = 0; i < reps; i++) {
	    for (int j = 0; j < counter.length; j++)
		counter[j].volatileSet(i);
	}
	t.join();
	return (int) counter[counter.length - 1].volatileGet();
    }

    private Thread waitForLastCounter(final int reps) {
	Thread t = new Thread(new Runnable() {
	    @Override
	    public void run() {
		while (counter[counter.length - 1].volatileGet() < reps - 1)
		    Thread.yield();
	    }
	});
	t.start();
	return t;
    }

    public int timePutLazy(final int reps) throws InterruptedException {
	for (int i = 0; i < counter.length; i++)
	    counter[i].directSet(0L);
	Thread t = waitForLastCounter(reps);

	for (int i = 0; i < reps; i++) {
	    for (int j = 0; j < counter.length; j++)
		counter[j].lazySet(i);
	}
	t.join();
	return (int) counter[counter.length - 1].volatileGet();
    }

    public int timePutDirect(final int reps) throws InterruptedException {
	for (int i = 0; i < counter.length; i++)
	    counter[i].directSet(0L);
	Thread t = waitForLastCounter(reps);

	for (int i = 0; i < reps; i++) {
	    for (int j = 0; j < counter.length; j++)
		counter[j].directSet(i);
	}
	t.join();
	return (int) counter[counter.length - 1].volatileGet();
    }

    public static void main(String[] args) throws Exception {
	Runner.main(MemoryPutCostBenchmark.class, args);
    }
}
