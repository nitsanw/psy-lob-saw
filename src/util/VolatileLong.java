package util;



public final class VolatileLong {
    private static final long valueOffset;

    static {
	try {
	    valueOffset = UnsafeAccess.unsafe.objectFieldOffset(VolatileLong.class
		    .getDeclaredField("value"));
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private volatile long value;

    public VolatileLong() {
    }

    public VolatileLong(final long initialValue) {
	lazySet(initialValue);
    }

    public void set(final long value) {
	this.value = value;
    }
    public long get() {
	return value;
    }
    public long directGet() {
	return UnsafeAccess.unsafe.getLong(this, valueOffset);
    }
    public void directSet(final long value) {
	UnsafeAccess.unsafe.putLong(this, valueOffset, value);
    }
    public void lazySet(final long value) {
	UnsafeAccess.unsafe.putOrderedLong(this, valueOffset, value);
    }

    public boolean compareAndSet(final long expectedValue, final long newValue) {
	return UnsafeAccess.unsafe.compareAndSwapLong(this, valueOffset,
		expectedValue, newValue);
    }

    public String toString() {
	return Long.toString(get());
    }
}
