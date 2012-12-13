package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class DirectLong {
    private final int CACHE_LINE_SIZE = 64;

    private final ByteBuffer buffy = ByteBuffer.allocateDirect(CACHE_LINE_SIZE + 8).order(
	    ByteOrder.nativeOrder());
    private final long address;

    public DirectLong() {
	long base = UnsafeDirectByteBuffer.getAddress(buffy);
	// this should align the address to the nearest line
	if((base & (CACHE_LINE_SIZE-1)) == 0){
	    address = base;
	}
	else{
	    address = base + CACHE_LINE_SIZE - (base & (CACHE_LINE_SIZE - 1));
	}
    }

    public DirectLong(final long initialValue) {
	this();
	set(initialValue);
    }

    public long getVolatile() {
	return UnsafeAccess.unsafe.getLongVolatile(null, address);
    }

    public void set(final long value) {
	UnsafeAccess.unsafe.putLong(address, value);
    }

    public void setOrdered(final long value) {
	UnsafeAccess.unsafe.putOrderedLong(null, address, value);
    }

    public void setVolatile(final long value) {
	UnsafeAccess.unsafe.putLongVolatile(null, address, value);
    }

    public String toString() {
	return Long.toString(getVolatile());
    }
}
