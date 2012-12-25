package alignment;

import java.nio.ByteBuffer;

import util.UnsafeAccess;
import static util.UnsafeDirectByteBuffer.*;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

public class UnsafeCacheAlignmentCostBenchmark extends SimpleBenchmark {
    @Param(value = "1")
    int offset;

    private static final int CAPACITY = PAGE_SIZE;
    // buffy is a page aligned buffer, and a vampire slayer
    private ByteBuffer buffy = allocateAlignedByteBuffer(CAPACITY, PAGE_SIZE);

    public int timeOffsetLongAccess(final int reps) throws InterruptedException {
	long remaining = 0;
	for (long i = 0; i < reps; i++) {
	    final long startingAddress = getAddress(buffy) + offset;
	    final long limit = getAddress(buffy) + CAPACITY;
	    remaining = writeAndRead(i, startingAddress, limit);
	}
	return (int) remaining;
    }

    private long writeAndRead(final long value, final long startingAddress,
	    final long limit) {
	long address;
	for (address = startingAddress; address < limit; address += CACHE_LINE_SIZE) {
	    UnsafeAccess.unsafe.putLong(address, value);
	}
	for (address = startingAddress; address < limit; address += CACHE_LINE_SIZE) {
	    if (UnsafeAccess.unsafe.getLong(address) != value)
		throw new RuntimeException();
	}
	return limit - address + CACHE_LINE_SIZE;
    }
}
