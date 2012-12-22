package lazyset;

import util.MemoryLong;
import util.VolatileLong;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

public class MemoryPutCostBenchmark extends SimpleBenchmark {
    @Param(value = "offheap")
    String counterType;
    private MemoryLong counter;

    @Override
    protected void setUp() throws Exception {
	if (counterType.equals("volatile1"))
	    counter = new VolatileLong();
	else if (counterType.equals("volatile2"))
	    counter = new VolatileLong();
	else if (counterType.equals("offheap"))
	    counter = new VolatileLong();
	else
	    throw new IllegalArgumentException();
    }

    public int timePutVolatile(final int reps) throws InterruptedException {
	counter.directSet(0L);
	Thread t = new Thread(new Runnable() {
	    @Override
	    public void run() {
		while (counter.volatileGet() < reps - 1)
		    Thread.yield();
	    }
	});
	t.start();
	for (int i = 0; i < reps; i++) {
	    counter.volatileSet(i);
	}
	t.join();
	return (int) counter.volatileGet();
    }

    public int timePutDirect(final int reps) throws InterruptedException {
	counter.directSet(0L);
	Thread t = new Thread(new Runnable() {
	    @Override
	    public void run() {
		while (counter.volatileGet() < reps - 1)
		    Thread.yield();
	    }
	});
	t.start();
	for (int i = 0; i < reps; i++) {
	    counter.directSet(i);
	}
	t.join();
	return (int) counter.volatileGet();
    }

    public int timePutLazy(final int reps) throws InterruptedException {
	counter.directSet(0L);
	Thread t = new Thread(new Runnable() {
	    @Override
	    public void run() {
		while (counter.volatileGet() < reps - 1)
		    Thread.yield();
	    }
	});
	t.start();
	for (int i = 0; i < reps; i++) {
	    counter.lazySet(i);
	}
	t.join();
	return (int) counter.volatileGet();
    }
}
