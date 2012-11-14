package util;
import java.nio.Buffer;
import java.nio.CharBuffer;


public class UnsafeCharBuffer {
    private static final long hbFieldOffset;
    private static final long offsetFieldOffset;
    private static final long capacityFieldOffset;
    private static final long markFieldOffset;

    static
    {
        try
        {

            hbFieldOffset =
                UnsafeAccess.unsafe.objectFieldOffset(CharBuffer.class
                    .getDeclaredField("hb"));
            offsetFieldOffset =
                UnsafeAccess.unsafe.objectFieldOffset(CharBuffer.class
                    .getDeclaredField("offset"));
            capacityFieldOffset =
                UnsafeAccess.unsafe.objectFieldOffset(Buffer.class
                    .getDeclaredField("capacity"));
            markFieldOffset =
                UnsafeAccess.unsafe.objectFieldOffset(Buffer.class
                    .getDeclaredField("mark"));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public static void wrap(CharBuffer buffy,char[] chars, int offset, int length){
        UnsafeAccess.unsafe.putObject(buffy,hbFieldOffset,chars);
        UnsafeAccess.unsafe.putInt(buffy,offsetFieldOffset,0); // see CharBuffer.wrap
                                                          // doc
        UnsafeAccess.unsafe.putInt(buffy,capacityFieldOffset,chars.length);
        UnsafeAccess.unsafe.putInt(buffy,markFieldOffset,-1);
        buffy.position(offset);
        buffy.limit(offset + length);
    }
}
