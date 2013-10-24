package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class OffHeapPaddedLong {
	private final int CACHE_LINE_SIZE = 64;
	private final ByteBuffer buffy = ByteBuffer.allocateDirect(
	        CACHE_LINE_SIZE + 8 * 15).order(ByteOrder.nativeOrder());
	private final long address;// = UnsafeDirectByteBuffer.getAddress(buffy) + 8

	// * 7;

	public OffHeapPaddedLong() {
		long base = UnsafeDirectByteBuffer.getAddress(buffy);
		// this should align the address to the nearest line
		if ((base & (CACHE_LINE_SIZE - 1)) == 0) {
			address = base + 8 * 7;
		} else {
			address = base + CACHE_LINE_SIZE - (base & (CACHE_LINE_SIZE - 1))
			        + 8 * 7;
		}
	}

	public OffHeapPaddedLong(final long initialValue) {
		this();
		setOrdered(initialValue);
	}

	public long getVolatile() {
		return UnsafeAccess.UNSAFE.getLongVolatile(null, address);
	}

	public void set(final long value) {
		UnsafeAccess.UNSAFE.putLong(address, value);
	}

	public void setOrdered(final long value) {
		UnsafeAccess.UNSAFE.putOrderedLong(null, address, value);
	}

	public void setVolatile(final long value) {
		UnsafeAccess.UNSAFE.putLongVolatile(null, address, value);
	}

	public boolean isPageAligned() {
		return (address & (UnsafeAccess.UNSAFE.pageSize() - 1)) == 0;
	}

	/**
	 * This assumes cache line is 64b
	 */
	public boolean isCacheAligned() {
		return (address & (CACHE_LINE_SIZE - 1)) == 0;
	}

	public String toString() {
		return Long.toString(getVolatile());
	}
}
