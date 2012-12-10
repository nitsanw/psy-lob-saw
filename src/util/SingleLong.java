package util;



public final class SingleLong {
    private static final long valueOffset;

    static {
	try {
	    valueOffset = UnsafeAccess.unsafe.objectFieldOffset(SingleLong.class
		    .getDeclaredField("value"));
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private long value = -1L;

    public SingleLong() {
    }

    public SingleLong(final long initialValue) {
	setOrdered(initialValue);
    }

    public long get() {
	return value;
    }
    public void set(final long value) {
	this.value = value;
    }
    public long getUnsafe() {
	return UnsafeAccess.unsafe.getLong(this, valueOffset);
    }
    public void setUnsafe(final long value) {
	UnsafeAccess.unsafe.putLong(this, valueOffset, value);
    }
    public void setOrdered(final long value) {
	UnsafeAccess.unsafe.putOrderedLong(this, valueOffset, value);
    }

    public void setVolatile(final long value) {
	UnsafeAccess.unsafe.putLongVolatile(this, valueOffset, value);
    }
    public long getVolatile() {
	return UnsafeAccess.unsafe.getLongVolatile(this, valueOffset);
    }

    public boolean compareAndSet(final long expectedValue, final long newValue) {
	return UnsafeAccess.unsafe.compareAndSwapLong(this, valueOffset,
		expectedValue, newValue);
    }

    public String toString() {
	return Long.toString(getVolatile());
    }
}
