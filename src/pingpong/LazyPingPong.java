package pingpong;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.out;

public final class LazyPingPong
{
    private static final long ITERATIONS = 100L * 1000L * 1000L;

    private static AtomicLong pingValue = new AtomicLong(-1L);
    private static AtomicLong pongValue = new AtomicLong(-1L);

    private static final CountDownLatch latch = new CountDownLatch(2);

    public static void main(final String[] args)
        throws Exception
    {
        Thread pongThread = new Thread(new PongRunner());
        Thread pingThread = new Thread(new PingRunner());
        pongThread.start();
        pingThread.start();

        latch.await();

        long start = System.nanoTime();

        pongThread.join();

        long duration = System.nanoTime() - start;

        out.printf("duration %,d (ns)\n", duration);
        out.printf("%,d ns/op\n", duration / (ITERATIONS * 2L));
        out.printf("%,d ops/s\n", (ITERATIONS * 2L * 1000000000L) / duration);
        out.println("pingValue = " + pingValue + ", pongValue = " + pongValue);
    }

    public static final class PingRunner implements Runnable
    {
        public void run()
        {
            latch.countDown();
            for(long l=0;l<ITERATIONS;l++){
            	pingValue.lazySet(l);
            	while(pongValue.get() != l);
            }
        }
    }

    public static final class PongRunner implements Runnable
    {
        public void run()
        {
            latch.countDown();
            for(long l=0;l<ITERATIONS;l++){
            	while(pingValue.get() != l);
            	pongValue.lazySet(l);
            }
        }
    }
}
