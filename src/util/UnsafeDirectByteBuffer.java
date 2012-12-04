package util;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class UnsafeDirectByteBuffer {
    private static final long addressOffset;

    static {
	try {
	    addressOffset = UnsafeAccess.unsafe.objectFieldOffset(Buffer.class
		    .getDeclaredField("address"));
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public static long getAddress(ByteBuffer buffy) {
	return UnsafeAccess.unsafe.getLong(buffy, addressOffset);
    }

    /**
     * put byte and skip position update and boundary checks
     * 
     * @param buffy
     * @param b
     */
    public static void putByte(long address, int position, byte b) {
	UnsafeAccess.unsafe.putByte(address + (position << 0), b);
    }

    public static void putByte(long address, byte b) {
	UnsafeAccess.unsafe.putByte(address, b);
    }
}
