package pingpong;

import java.util.concurrent.atomic.AtomicLong;

public final class AtomicPingPong
{
    static final class AtomicPingPongCounter implements PingPongCounter {
	    private final AtomicLong val = new AtomicLong(-1);
	    @Override
	    public void set(long l) {
		val.set(l);
	    }
	    
	    @Override
	    public long get() {
		return val.get();
	    }
	    @Override
	    public String toString() {
		return val.toString();
	    }
	}

    public static void main(final String[] args) throws Exception {
	System.out.print("atomic,\tset,\t");
	PingPongCounter pingValue = new AtomicPingPongCounter();
	PingPongCounter pongValue = new AtomicPingPongCounter();
	new PingPong(pingValue, pongValue);
    }
}
