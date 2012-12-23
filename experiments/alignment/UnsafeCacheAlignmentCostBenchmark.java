package alignment;

import java.nio.ByteBuffer;

import util.UnsafeAccess;
import util.UnsafeDirectByteBuffer;

import com.google.caliper.SimpleBenchmark;

public class UnsafeCacheAlignmentCostBenchmark extends SimpleBenchmark {

	private static final int CAPACITY = 16 * 4096;
	// buffy is a page aligned 4 page sized buffer, and a vampire slayer
	private ByteBuffer buffy = UnsafeDirectByteBuffer
			.allocateAlignedByteBuffer(CAPACITY, 4096);

	public int timeBrokenLong(final int reps) throws InterruptedException {
		long remaining=0;
		for (long i = 0; i < reps; i++) {
			final long startingAddress = UnsafeDirectByteBuffer.getAddress(buffy) + 60;
			final long limit = UnsafeDirectByteBuffer.getAddress(buffy) + CAPACITY;
			remaining=writeAndRead(i, startingAddress, limit);
		}
		return (int)remaining;
	}

	private long  writeAndRead(final long value, final long startingAddress,
			final long limit) {
		long address = startingAddress;
		do {
			UnsafeAccess.unsafe.putLong(address, value);
			address += UnsafeDirectByteBuffer.CACHE_LINE_SIZE;
		} while (address < limit);
		address = startingAddress;
		do {
			if (UnsafeAccess.unsafe.getLong(address) != value)
				throw new RuntimeException();
			address += UnsafeDirectByteBuffer.CACHE_LINE_SIZE;
		} while (address < limit);
		return  limit- address + UnsafeDirectByteBuffer.CACHE_LINE_SIZE;
	}

	public int timeFirstLong(final int reps) throws InterruptedException {

		long remaining=0;
		for (long i = 0; i < reps; i++) {
			final long startingAddress = UnsafeDirectByteBuffer.getAddress(buffy);
			final long limit = UnsafeDirectByteBuffer.getAddress(buffy) + CAPACITY;
			remaining=writeAndRead(i, startingAddress, limit);
		}
		return (int)remaining;
	}

	public int timeLastLong(final int reps) throws InterruptedException {

		long remaining=0;
		for (long i = 0; i < reps; i++) {
			final long startingAddress = UnsafeDirectByteBuffer.getAddress(buffy) + 56;
			final long limit = UnsafeDirectByteBuffer.getAddress(buffy) + CAPACITY;
			remaining=writeAndRead(i, startingAddress, limit);
		}
		return (int)remaining;
	}
	
	public int timeMisalignedLong(final int reps) throws InterruptedException {

		long remaining=0;
		for (long i = 0; i < reps; i++) {
			final long startingAddress = UnsafeDirectByteBuffer.getAddress(buffy) + 1;
			final long limit = UnsafeDirectByteBuffer.getAddress(buffy) + CAPACITY;
			remaining=writeAndRead(i, startingAddress, limit);
		}
		return (int)remaining;
	}
}
