package util;


import java.nio.CharBuffer;


public class UnsafeString {
    private static final long valueFieldOffset;
    private static final long countFieldOffset;
    private static final long offsetFieldOffset;

    static
    {
        try
        {
            valueFieldOffset =
                UnsafeAccess.unsafe.objectFieldOffset(String.class
                    .getDeclaredField("value"));
            countFieldOffset =
                UnsafeAccess.unsafe.objectFieldOffset(String.class
                    .getDeclaredField("count"));
            offsetFieldOffset =
                UnsafeAccess.unsafe.objectFieldOffset(String.class
                    .getDeclaredField("offset"));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public final static String buildUnsafe(char[] chars){
        String mutable = new String();// an empty string to hack
        UnsafeAccess.unsafe.putObject(mutable,valueFieldOffset,chars);
        UnsafeAccess.unsafe.putInt(mutable,countFieldOffset,chars.length);
        return mutable;
    }
    public final static String buildUnsafe(char[] chars, int offset, int length){
        String mutable = new String();// an empty string to hack
        UnsafeAccess.unsafe.putObject(mutable,valueFieldOffset,chars);
        UnsafeAccess.unsafe.putInt(mutable,countFieldOffset,length);
        UnsafeAccess.unsafe.putInt(mutable,offsetFieldOffset,offset);
        return mutable;
    }
    public final static char[] getChars(String s){
        return (char[])UnsafeAccess.unsafe.getObject(s,valueFieldOffset);
    }
    public final static int getOffset(String s){
        return UnsafeAccess.unsafe.getInt(s,offsetFieldOffset);
    }
    public final static CharBuffer getStringAsCharBuffer(String s){
        CharBuffer buffy = CharBuffer.wrap(getChars(s),getOffset(s),s.length());
        return buffy;
    }
    public final static void wrapStringWithCharBuffer(String s, CharBuffer buffy){
        UnsafeCharBuffer.wrap(buffy,getChars(s),getOffset(s),s.length());
    }
}
