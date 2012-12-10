package util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import pingpong.PingPongCounter;


public final class DirectLong implements PingPongCounter{
    private final ByteBuffer buffy = ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder());
    private final long address = UnsafeDirectByteBuffer.getAddress(buffy);

    public DirectLong() {
    }

    public DirectLong(final long initialValue) {
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
