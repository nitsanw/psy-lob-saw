package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class OffHeapLong implements MemoryLong {
	private final ByteBuffer buffy = ByteBuffer.allocateDirect(8).order(
	        ByteOrder.nativeOrder());
	private final long address = UnsafeDirectByteBuffer.getAddress(buffy);

	public OffHeapLong() {
		this(0L);
	}

	public OffHeapLong(final long initialValue) {
		directSet(initialValue);
	}

	public long volatileGet() {
		return UnsafeAccess.UNSAFE.getLongVolatile(null, address);
	}

	public void directSet(final long value) {
		UnsafeAccess.UNSAFE.putLong(address, value);
	}

	public long directGet() {
		return UnsafeAccess.UNSAFE.getLong(address);
	}

	public void lazySet(final long value) {
		UnsafeAccess.UNSAFE.putOrderedLong(null, address, value);
	}

	public void volatileSet(final long value) {
		UnsafeAccess.UNSAFE.putLongVolatile(null, address, value);
	}

	public boolean compareAndSet(final long expectedValue, final long newValue) {
		return UnsafeAccess.UNSAFE.compareAndSwapLong(null, address,
		        expectedValue, newValue);
	}

	public String toString() {
		return Long.toString(volatileGet());
	}
}
