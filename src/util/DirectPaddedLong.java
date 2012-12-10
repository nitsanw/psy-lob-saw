package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import pingpong.PingPongCounter;


public final class DirectPaddedLong implements PingPongCounter{
    private final ByteBuffer buffy = ByteBuffer.allocateDirect(8*15).order(ByteOrder.nativeOrder());
    private final long address = UnsafeDirectByteBuffer.getAddress(buffy)+8*7;

    public DirectPaddedLong() {
    }

    public DirectPaddedLong(final long initialValue) {
	set(initialValue);
    }

    public long get() {
	return UnsafeAccess.unsafe.getLongVolatile(null, address);
    }

    public void set(final long value) {
	UnsafeAccess.unsafe.putLong(address, value);
    }

    public String toString() {
	return Long.toString(get());
    }
}
