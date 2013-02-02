package page;
import static java.lang.System.out;
import static util.UnsafeDirectByteBuffer.PAGE_SIZE;
import static util.UnsafeDirectByteBuffer.allocateAlignedByteBuffer;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public final class TestSequentialIoPerf {
    public static final long FILE_SIZE = PAGE_SIZE * 100L * 1000L;
    public static final String FILE_NAME = "test.dat";
    public static final byte[] BLANK_PAGE = new byte[PAGE_SIZE];

    public static void main(final String[] arg) throws Exception {

	for (final PerfTestCase testCase : testCases) {
	    for (int i = 0; i < 20; i++) {
		String fileName = FILE_NAME+i;

//		preallocateTestFile(fileName);
		System.gc();
		long writeDurationMs = testCase.test(PerfTestCase.Type.WRITE,
			fileName);

		System.gc();
		long readDurationMs = testCase.test(PerfTestCase.Type.READ,
			fileName);

		long bytesReadPerSec = (FILE_SIZE * 1000L) / readDurationMs;
		long bytesWrittenPerSec = (FILE_SIZE * 1000L) / writeDurationMs;

		out.format("%s\twrite=%,d\tread=%,d bytes/sec\n",
			testCase.getName(), bytesWrittenPerSec, bytesReadPerSec);
		deleteFile(fileName);
	    }
	}
	
    }

    private static void preallocateTestFile(final String fileName)
	    throws Exception {
	RandomAccessFile file = new RandomAccessFile(fileName, "rw");

	for (long i = 0; i < FILE_SIZE; i += PAGE_SIZE) {
	    file.write(BLANK_PAGE, 0, PAGE_SIZE);
	}

	file.close();
    }

    private static void deleteFile(final String testFileName) throws Exception {
	File file = new File(testFileName);
	if (!file.delete()) {
	    out.println("Failed to delete test file=" + testFileName);
	    out.println("Windows does not allow mapped files to be deleted.");
	}
    }

    public abstract static class PerfTestCase {
	public enum Type {
	    READ, WRITE
	}

	private final String name;
	private int checkSum;

	public PerfTestCase(final String name) {
	    this.name = name;
	}

	public String getName() {
	    return name;
	}

	public long test(final Type type, final String fileName) {
	    long start = System.currentTimeMillis();

	    try {
		switch (type) {
		case WRITE: {
		    checkSum = testWrite(fileName);
		    break;
		}

		case READ: {
		    final int checkSum = testRead(fileName);
		    if (checkSum != this.checkSum) {
			final String msg = getName() + " expected="
				+ this.checkSum + " got=" + checkSum;
			throw new IllegalStateException(msg);
		    }
		    break;
		}
		}
	    } catch (Exception ex) {
		ex.printStackTrace();
	    }

	    return System.currentTimeMillis() - start;
	}

	public abstract int testWrite(final String fileName) throws Exception;

	public abstract int testRead(final String fileName) throws Exception;
    }

    private static PerfTestCase[] testCases = {
	new PerfTestCase("BufferedChannelFileU") {
		public int testWrite(final String fileName) throws Exception {
		    FileChannel channel = new RandomAccessFile(fileName, "rw")
			    .getChannel();
		    ByteBuffer buffer = allocateAlignedByteBuffer(
			    2 * PAGE_SIZE, PAGE_SIZE);
		    buffer.position(PAGE_SIZE / 2);
		    buffer.limit(PAGE_SIZE / 2  + PAGE_SIZE);
		    buffer = buffer.slice().order(ByteOrder.nativeOrder());
		    int checkSum = 0;
		    int pos = 0;
		    for (long i = 0; i < FILE_SIZE; i++) {
			byte b = (byte) i;
			checkSum += b;
			buffer.put(pos++, b);

			if (pos == PAGE_SIZE) {
			    channel.write(buffer);
			    buffer.position(0);
			    pos = 0;
			}
		    }

		    channel.close();

		    return checkSum;
		}

		public int testRead(final String fileName) throws Exception {
		    FileChannel channel = new RandomAccessFile(fileName, "rw")
			    .getChannel();
		    ByteBuffer buffer = allocateAlignedByteBuffer(
			    2 * PAGE_SIZE, PAGE_SIZE);
		    buffer.position(PAGE_SIZE / 2);
		    buffer.limit(PAGE_SIZE / 2 + PAGE_SIZE);
		    buffer = buffer.slice().order(ByteOrder.nativeOrder());
		    int checkSum = 0;

		    while (-1 != (channel.read(buffer))) {
			buffer.flip();

			while (buffer.hasRemaining()) {
			    checkSum += buffer.get();
			}

			buffer.clear();
		    }

		    return checkSum;
		}
	    },
		new PerfTestCase("BufferedChannelFileU1") {
		public int testWrite(final String fileName) throws Exception {
		    FileChannel channel = new RandomAccessFile(fileName, "rw")
			    .getChannel();
		    ByteBuffer buffer = allocateAlignedByteBuffer(
			    2 * PAGE_SIZE, PAGE_SIZE);
		    buffer.position(PAGE_SIZE / 2 + 1);
		    buffer.limit(PAGE_SIZE / 2 + 1 + PAGE_SIZE);
		    buffer = buffer.slice().order(ByteOrder.nativeOrder());
		    int checkSum = 0;
		    int pos = 0;
		    for (long i = 0; i < FILE_SIZE; i++) {
			byte b = (byte) i;
			checkSum += b;
			buffer.put(pos++, b);

			if (pos == PAGE_SIZE) {
			    channel.write(buffer);
			    buffer.position(0);
			    pos = 0;
			}
		    }

		    channel.close();

		    return checkSum;
		}

		public int testRead(final String fileName) throws Exception {
		    FileChannel channel = new RandomAccessFile(fileName, "rw")
			    .getChannel();
		    ByteBuffer buffer = allocateAlignedByteBuffer(
			    2 * PAGE_SIZE, PAGE_SIZE);
		    buffer.position(PAGE_SIZE / 2 + 1);
		    buffer.limit(PAGE_SIZE / 2 + 1 + PAGE_SIZE);
		    buffer = buffer.slice().order(ByteOrder.nativeOrder());
		    int checkSum = 0;

		    while (-1 != (channel.read(buffer))) {
			buffer.flip();

			while (buffer.hasRemaining()) {
			    checkSum += buffer.get();
			}

			buffer.clear();
		    }

		    return checkSum;
		}
	    },


	    // new PerfTestCase("BufferedStreamFile")
	    // {
	    // public int testWrite(final String fileName) throws Exception
	    // {
	    // int checkSum = 0;
	    // OutputStream out =
	    // new BufferedOutputStream(new
	    // FileOutputStream(fileName),PAGE_SIZE);
	    //
	    // for (long i = 0; i < FILE_SIZE; i++)
	    // {
	    // byte b = (byte)i;
	    // checkSum += b;
	    // out.write(b);
	    // }
	    //
	    // out.close();
	    //
	    // return checkSum;
	    // }
	    //
	    // public int testRead(final String fileName) throws Exception
	    // {
	    // int checkSum = 0;
	    // InputStream in =
	    // new BufferedInputStream(new FileInputStream(fileName),PAGE_SIZE);
	    //
	    // int b;
	    // while (-1 != (b = in.read()))
	    // {
	    // checkSum += (byte)b;
	    // }
	    //
	    // in.close();
	    //
	    // return checkSum;
	    // }
	    // },

	    new PerfTestCase("BufferedChannelFileA") {
		public int testWrite(final String fileName) throws Exception {
		    FileChannel channel = new RandomAccessFile(fileName, "rw")
			    .getChannel();
		    ByteBuffer buffer = allocateAlignedByteBuffer(PAGE_SIZE,
			    PAGE_SIZE);
		    int checkSum = 0;
		    int pos = 0;
		    for (long i = 0; i < FILE_SIZE; i++) {
			byte b = (byte) i;
			checkSum += b;
			buffer.put(pos++, b);

			if (pos == PAGE_SIZE) {
			    channel.write(buffer);
			    buffer.position(0);
			    pos = 0;
			}
		    }

		    channel.close();

		    return checkSum;
		}

		public int testRead(final String fileName) throws Exception {
		    FileChannel channel = new RandomAccessFile(fileName, "rw")
			    .getChannel();
		    ByteBuffer buffer = allocateAlignedByteBuffer(PAGE_SIZE,
			    PAGE_SIZE);
		    int checkSum = 0;

		    while (-1 != (channel.read(buffer))) {
			buffer.flip();

			while (buffer.hasRemaining()) {
			    checkSum += buffer.get();
			}

			buffer.clear();
		    }

		    return checkSum;
		}
	    },  new PerfTestCase("BufferedChannelFileH") {
		public int testWrite(final String fileName) throws Exception {
		    FileChannel channel = new RandomAccessFile(fileName, "rw")
			    .getChannel();
		    ByteBuffer buffer = ByteBuffer.allocate(PAGE_SIZE);
		    int checkSum = 0;
		    int pos = 0;
		    for (long i = 0; i < FILE_SIZE; i++) {
			byte b = (byte) i;
			checkSum += b;
			buffer.put(pos++, b);

			if (pos == PAGE_SIZE) {
			    channel.write(buffer);
			    buffer.position(0);
			    pos = 0;
			}
		    }

		    channel.close();

		    return checkSum;
		}

		public int testRead(final String fileName) throws Exception {
		    FileChannel channel = new RandomAccessFile(fileName, "rw")
			    .getChannel();
		    ByteBuffer buffer = ByteBuffer.allocate(PAGE_SIZE);
		    int checkSum = 0;

		    while (-1 != (channel.read(buffer))) {
			buffer.flip();

			while (buffer.hasRemaining()) {
			    checkSum += buffer.get();
			}

			buffer.clear();
		    }

		    return checkSum;
		}
	    },
	    new PerfTestCase("RandomAccessFile") {
		public int testWrite(final String fileName) throws Exception {
		    RandomAccessFile file = new RandomAccessFile(fileName, "rw");
		    final byte[] buffer = new byte[PAGE_SIZE];
		    int pos = 0;
		    int checkSum = 0;

		    for (long i = 0; i < FILE_SIZE; i++) {
			byte b = (byte) i;
			checkSum += b;

			buffer[pos++] = b;
			if (PAGE_SIZE == pos) {
			    file.write(buffer, 0, PAGE_SIZE);
			    pos = 0;
			}
		    }

		    file.close();

		    return checkSum;
		}

		public int testRead(final String fileName) throws Exception {
		    RandomAccessFile file = new RandomAccessFile(fileName, "r");
		    final byte[] buffer = new byte[PAGE_SIZE];
		    int checkSum = 0;
		    int bytesRead;

		    while (-1 != (bytesRead = file.read(buffer))) {
			for (int i = 0; i < bytesRead; i++) {
			    checkSum += buffer[i];
			}
		    }

		    file.close();

		    return checkSum;
		}
	    },
    };
}