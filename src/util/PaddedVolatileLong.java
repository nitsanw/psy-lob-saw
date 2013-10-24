package util;

public final class PaddedVolatileLong {
	private static final long valueOffset;

	static {
		try {
			final int base = UnsafeAccess.UNSAFE.arrayBaseOffset(long[].class);
			final int scale = UnsafeAccess.UNSAFE.arrayIndexScale(long[].class);
			valueOffset = base + (scale * 7);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final long[] paddedValue = new long[15];

	public PaddedVolatileLong() {
	}

	public PaddedVolatileLong(final long initialValue) {
		setOrdered(initialValue);
	}

	public long get() {
		return UnsafeAccess.UNSAFE.getLong(paddedValue, valueOffset);
	}

	public void set(final long value) {
		UnsafeAccess.UNSAFE.putLong(paddedValue, valueOffset, value);
	}

	public void setOrdered(final long value) {
		UnsafeAccess.UNSAFE.putOrderedLong(paddedValue, valueOffset, value);
	}

	public void setVolatile(final long value) {
		UnsafeAccess.UNSAFE.putLongVolatile(paddedValue, valueOffset, value);
	}

	public long getVolatile() {
		return UnsafeAccess.UNSAFE.getLongVolatile(paddedValue, valueOffset);
	}

	public boolean compareAndSet(final long expectedValue, final long newValue) {
		return UnsafeAccess.UNSAFE.compareAndSwapLong(paddedValue, valueOffset,
		        expectedValue, newValue);
	}

	public String toString() {
		return Long.toString(getVolatile());
	}
}
