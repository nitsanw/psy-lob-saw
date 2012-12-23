package alignment;

import java.nio.ByteBuffer;

import util.UnsafeDirectByteBuffer;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

public class TypeAlignmentCostBenchmark extends SimpleBenchmark {
	@Param(value = "1")
	int offset;
	// buffy is a page aligned 16 page sized buffer, and a vampire slayer
	private ByteBuffer buffy = UnsafeDirectByteBuffer
			.allocateAlignedByteBuffer(16 * 4096, 4096);

//	public int timePutByte(final int reps) throws InterruptedException {
//		for (long i = 0; i < reps; i++) {
//			prepareBuffy();
//			while (buffy.remaining() > 1) {
//				buffy.put((byte) i);
//			}
//			prepareBuffy();
//			while (buffy.remaining() > 1) {
//				if (buffy.get() != (byte) i)
//					throw new RuntimeException();
//			}
//		}
//		return buffy.remaining();
//	}
//
//	public int timePutChar(final int reps) throws InterruptedException {
//		for (long i = 0; i < reps; i++) {
//			prepareBuffy();
//			while (buffy.remaining() > 2) {
//				buffy.putChar((char) i);
//			}
//			prepareBuffy();
//			while (buffy.remaining() > 2) {
//				if (buffy.getChar() != (char) i)
//					throw new RuntimeException();
//			}
//		}
//		return buffy.remaining();
//	}
//
//	public int timePutShort(final int reps) throws InterruptedException {
//		for (long i = 0; i < reps; i++) {
//			prepareBuffy();
//			while (buffy.remaining() > 2) {
//				buffy.putShort((short) i);
//			}
//			prepareBuffy();
//			while (buffy.remaining() > 2) {
//				if (buffy.getShort() != (short) i)
//					throw new RuntimeException();
//			}
//		}
//		return buffy.remaining();
//	}
//
//	public int timePutInt(final int reps) throws InterruptedException {
//
//		for (long i = 0; i < reps; i++) {
//			prepareBuffy();
//			while (buffy.remaining() > 4) {
//				buffy.putInt((int) i);
//			}
//			prepareBuffy();
//			while (buffy.remaining() > 4) {
//				if (buffy.getInt() != (int) i)
//					throw new RuntimeException();
//			}
//		}
//		return buffy.remaining();
//	}

	public int timePutLong(final int reps) throws InterruptedException {

		for (long i = 0; i < reps; i++) {
			prepareBuffy();
			while (buffy.remaining() > 8) {
				buffy.putLong(i);
			}
			prepareBuffy();
			while (buffy.remaining() > 8) {
				if (buffy.getLong() != i)
					throw new RuntimeException();
			}
		}
		return buffy.remaining();
	}

	private void prepareBuffy() {
		buffy.clear();
		for (int i = 0; i < offset; i++) {
			buffy.put((byte) 0);
		}
	}

}
