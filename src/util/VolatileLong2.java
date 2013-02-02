package util;

public final class VolatileLong2 implements MemoryLong {
	private static final long valueOffset;

	static {
		try {
			valueOffset = UnsafeAccess.unsafe
			        .objectFieldOffset(VolatileLong2.class
			                .getDeclaredField("value"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private long value;

	public VolatileLong2() {
	}

	public VolatileLong2(final long initialValue) {
		lazySet(initialValue);
	}

	public long volatileGet() {
		return UnsafeAccess.unsafe.getLong(this, valueOffset);
	}

	public long directGet() {
		return value;
	}

	public void directSet(final long value) {
		this.value = value;
	}

	public void lazySet(final long value) {
		UnsafeAccess.unsafe.putOrderedLong(this, valueOffset, value);
	}

	public void volatileSet(final long value) {
		UnsafeAccess.unsafe.putLongVolatile(this, valueOffset, value);
	}

	public boolean compareAndSet(final long expectedValue, final long newValue) {
		return UnsafeAccess.unsafe.compareAndSwapLong(this, valueOffset,
		        expectedValue, newValue);
	}

	public String toString() {
		return Long.toString(volatileGet());
	}
}
