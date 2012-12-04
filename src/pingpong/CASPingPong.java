package pingpong;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.out;

public final class CASPingPong {
	private static final long ITERATIONS = 100L*1000L*1000L;

	private static AtomicLong pingPongValue = new AtomicLong(-1L);

	private static final CountDownLatch latch = new CountDownLatch(2);

	public static void main(final String[] args) throws Exception {
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
		out.println("pingPongValue = " + pingPongValue);
	}

	public static final class PingRunner implements Runnable {
		public void run() {
			latch.countDown();
			for (long l = 0; l < ITERATIONS * 2; l += 2) {
				while (!pingPongValue.compareAndSet(l - 1,l))
					;
			}
			// falseShareProtection[7] = pingValue.get();
		}
	}

	public static final class PongRunner implements Runnable {
		public void run() {
			latch.countDown();
			for (long l = 1; l < ITERATIONS * 2 - 2; l += 2) {
				while (!pingPongValue.compareAndSet(l - 1,l))
					;
			}
			// falseShareProtection[0] = pingValue.get();
		}
	}
}
