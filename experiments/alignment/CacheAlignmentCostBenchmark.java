package alignment;

import java.nio.ByteBuffer;

import util.UnsafeDirectByteBuffer;

import com.google.caliper.SimpleBenchmark;

public class CacheAlignmentCostBenchmark extends SimpleBenchmark {

    // buffy is a page aligned 4 page sized buffer, and a vampire slayer
    private ByteBuffer buffy = 
	    UnsafeDirectByteBuffer.allocateAlignedByteBuffer(16*4096, 4096);

    public int timeBrokenLong(final int reps) throws InterruptedException {
	
	for (long i = 0; i < reps; i++) {
	    buffy.clear();
	    buffy.position(60);
	    while(buffy.remaining() > 8 + UnsafeDirectByteBuffer.PAGE_SIZE){
		buffy.putLong(i);
		buffy.position(buffy.position() + 56);
	    }
	    buffy.clear();
	    buffy.position(60);
	    while(buffy.remaining() > 8 + UnsafeDirectByteBuffer.PAGE_SIZE){
		if(buffy.getLong()!=i)
		    throw new RuntimeException();
		buffy.position(buffy.position() + 56);
	    }
	}
	return buffy.remaining();
    }
    public int timeFirstLong(final int reps) throws InterruptedException {
	
	for (long i = 0; i < reps; i++) {
	    buffy.clear();
	    while(buffy.remaining() > UnsafeDirectByteBuffer.PAGE_SIZE){
		buffy.putLong(i);
		buffy.position(buffy.position() + 56);
	    }
	    buffy.clear();
	    while(buffy.remaining() > UnsafeDirectByteBuffer.PAGE_SIZE){
		if(buffy.getLong()!=i)
		    throw new RuntimeException();
		buffy.position(buffy.position() + 56);
	    }
	}
	return buffy.remaining();
    }
    public int timeLastLong(final int reps) throws InterruptedException {
	
	for (long i = 0; i < reps; i++) {
	    buffy.clear();
	    while(buffy.remaining() > UnsafeDirectByteBuffer.PAGE_SIZE){
		buffy.position(buffy.position() + 56);
		buffy.putLong(i);
	    }
	    buffy.clear();
	    while(buffy.remaining() > UnsafeDirectByteBuffer.PAGE_SIZE){
		buffy.position(buffy.position() + 56);
		if(buffy.getLong()!=i)
		    throw new RuntimeException();
	    }
	}
	return buffy.remaining();
    }
}
