package atomicity;

import java.io.IOException;
import java.lang.reflect.Field;

import sun.misc.Unsafe;
import util.UnsafeAccess;

public class UnalignedAtomicWriteTest {
    private static final Unsafe UNSAFE;
    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /** 0 - ordered, 1 - volatile, 2 - CAS */
    private static final int WRITE_TYPE = Integer.getInteger("write.type", 0);
    /** 0 - volatile, 1 - CAS */
    private static final int READ_TYPE = Integer.getInteger("read.type", 0);
    /**
     * any offset should be > 0 and < line size, and such that long write is not
     * aligned
     */
    private static final int OFFSET = Integer.getInteger("offset", 4);
    private static final int CACHE_LINE_SIZE = 64;
    private static final long SOME_MEMORY = UNSAFE.allocateMemory(CACHE_LINE_SIZE * 3);
    private static final long LINE_BOUNDARY = CACHE_LINE_SIZE + SOME_MEMORY
            + (CACHE_LINE_SIZE - (SOME_MEMORY % CACHE_LINE_SIZE));
    private static final long UNALIGNED_ADDRESS = LINE_BOUNDARY - OFFSET;
    private static final long BROKEN1 = -1L >>> ((OFFSET % 8) * 8);
    private static final long BROKEN2 = -1L << ((OFFSET % 8) * 8);

    public static void main(String[] args) throws Exception {
        if (LINE_BOUNDARY % CACHE_LINE_SIZE != 0) {
            throw new IllegalStateException("Line boundary not aligned");
        }
        if (UNALIGNED_ADDRESS % 8 == 0) {
            throw new IllegalStateException("Unaligned address is aligned");
        }
        if ((BROKEN1 | BROKEN2) != -1L) {
            throw new IllegalStateException("Broken values should complete to -1L");
        }
        if (OFFSET > CACHE_LINE_SIZE || OFFSET < 0) {
            throw new IllegalStateException("Offset should be up to cache line size and positive");
        }
        System.out.printf("unaligned address:%d cache line at:%d write type:%d\n", UNALIGNED_ADDRESS, LINE_BOUNDARY,
                WRITE_TYPE);
        UNSAFE.putLong(UNALIGNED_ADDRESS, 0);
        new Thread() {
            public void run() {
                while (true) {
                    if (READ_TYPE == 0) {
                        inlineMeReadVolatile();
                    } else {
                        inlineMeReadCAS();
                    }
                }
            }

            private void inlineMeReadVolatile() {
                long observed = UNSAFE.getLongVolatile(null, UNALIGNED_ADDRESS);
                if (observed != 0L && observed != -1L) {
                    System.out.printf("WTF:%d", observed);
                    System.exit(-1);
                }
            }

            private void inlineMeReadCAS() {
                if (UNSAFE.compareAndSwapLong(null, UNALIGNED_ADDRESS, BROKEN1, BROKEN1)) {
                    System.out.printf("WTF:%d", BROKEN1);
                    System.exit(-1);
                } else if (UNSAFE.compareAndSwapLong(null, UNALIGNED_ADDRESS, BROKEN2, BROKEN2)) {
                    System.out.printf("WTF:%d", BROKEN2);
                    System.exit(-1);
                }
            }
        }.start();
        new Thread() {
            public void run() {
                while (true) {
                    switch (WRITE_TYPE) {
                    case 0:
                        inlineMeWriteOrdered();
                        break;
                    case 1:
                        inlineMeWriteVolatile();
                        break;
                    case 2:
                        inlineMeWriteCAS();
                        break;
                    default:
                        System.exit(-1);
                    }
                }
            }

            private void inlineMeWriteOrdered() {
                UNSAFE.putOrderedLong(null, UNALIGNED_ADDRESS, -1L);
                UNSAFE.putOrderedLong(null, UNALIGNED_ADDRESS, 0L);
            }

            private void inlineMeWriteVolatile() {
                UNSAFE.putLongVolatile(null, UNALIGNED_ADDRESS, -1L);
                UNSAFE.putLongVolatile(null, UNALIGNED_ADDRESS, 0L);
            }

            private void inlineMeWriteCAS() {
                UNSAFE.compareAndSwapLong(null, UNALIGNED_ADDRESS, 0L, -1L);
                UNSAFE.compareAndSwapLong(null, UNALIGNED_ADDRESS, -1L, 0L);
            }
        }.start();

        System.in.read();
        System.out.print("All is well!");
        System.exit(0);
    }

}
