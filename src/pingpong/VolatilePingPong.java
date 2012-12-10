package pingpong;


public final class VolatilePingPong {
    static final class VolatilePingPongCounter implements PingPongCounter {
	private volatile long val = -1L;

	@Override
	public void set(long l) {
	    val=l;
	}

	@Override
	public long get() {
	    return val;
	}

	@Override
	public String toString() {
	    return String.valueOf(val);
	}
    }

    public static void main(final String[] args) throws Exception {
	System.out.print("volatile,\tvolatile,\t");
	PingPongCounter pingValue = new VolatilePingPongCounter();
	PingPongCounter pongValue = new VolatilePingPongCounter();
	new PingPong(pingValue, pongValue);
    }
}
