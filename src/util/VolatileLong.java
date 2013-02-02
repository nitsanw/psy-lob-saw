package util;

public final class VolatileLong implements MemoryLong {
	private static final long valueOffset;

	static {
		try {
			valueOffset = UnsafeAccess.unsafe
			        .objectFieldOffset(VolatileLong.class
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

	@Override
	public long volatileGet() {
		return value;
	}

	@Override
	public long directGet() {
		return UnsafeAccess.unsafe.getLong(this, valueOffset);
	}

	@Override
	public void directSet(final long value) {
		UnsafeAccess.unsafe.putLong(this, valueOffset, value);
	}

	@Override
	public void lazySet(final long value) {
		UnsafeAccess.unsafe.putOrderedLong(this, valueOffset, value);
	}

	@Override
	public void volatileSet(final long value) {
		this.value = value;
	}

	@Override
	public boolean compareAndSet(final long expectedValue, final long newValue) {
		return UnsafeAccess.unsafe.compareAndSwapLong(this, valueOffset,
		        expectedValue, newValue);
	}

	public String toString() {
		return Long.toString(volatileGet());
	}
}
