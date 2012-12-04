package pingpong;

import util.UnsafeAccess;

public final class Sequence {
    private static final long valueOffset;

    static {
	try {
	    final int base = UnsafeAccess.unsafe.arrayBaseOffset(long[].class);
	    final int scale = UnsafeAccess.unsafe.arrayIndexScale(long[].class);
	    valueOffset = base + (scale * 7);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private final long[] paddedValue = new long[15];

    public Sequence() {
    }

    public Sequence(final long initialValue) {
	setOrdered(initialValue);
    }

    public long get() {
	return UnsafeAccess.unsafe.getLongVolatile(paddedValue, valueOffset);
    }

    public void setOrdered(final long value) {
	UnsafeAccess.unsafe.putOrderedLong(paddedValue, valueOffset, value);
    }

    public void addOrdered(final long delta) {
	UnsafeAccess.unsafe.putOrderedLong(paddedValue, valueOffset, get()
		+ delta);
    }

    public void setVolatile(final long value) {
	UnsafeAccess.unsafe.putLongVolatile(paddedValue, valueOffset, value);
    }

    public boolean compareAndSet(final long expectedValue, final long newValue) {
	return UnsafeAccess.unsafe.compareAndSwapLong(paddedValue, valueOffset,
		expectedValue, newValue);
    }

    public long incrementAndGet() {
	return addAndGet(1L);
    }

    public long getAndIncrement() {
	return getAndAdd(1L);
    }

    public long getAndAdd(final long increment) {
	long expectedVal;
	long newVal;
	do {
	    expectedVal = get();
	    newVal = expectedVal + increment;
	} while (!compareAndSet(expectedVal, newVal));
	return expectedVal;
    }

    public long addAndGet(final long increment) {
	long expectedVal;
	long newVal;
	do {
	    expectedVal = get();
	    newVal = expectedVal + increment;
	} while (!compareAndSet(expectedVal, newVal));
	return expectedVal;
    }

    public String toString() {
	return Long.toString(get());
    }
}
