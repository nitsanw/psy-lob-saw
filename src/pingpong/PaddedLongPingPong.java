package pingpong;

import util.PaddedLong;

public final class PaddedLongPingPong {
    static final class PlainCounter implements PingPongCounter {
	private final PaddedLong val = new PaddedLong(-1);

	@Override
	public void set(long l) {
	    val.set(l);
	}

	@Override
	public long get() {
	    return val.getVolatile();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }
    static final class OrderedCounter implements PingPongCounter {
	private final PaddedLong val = new PaddedLong(-1);

	@Override
	public void set(long l) {
	    val.setOrdered(l);
	}

	@Override
	public long get() {
	    return val.getVolatile();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }
    static final class VolatileCounter implements PingPongCounter {
	private final PaddedLong val = new PaddedLong(-1);

	@Override
	public void set(long l) {
	    val.setOrdered(l);
	}

	@Override
	public long get() {
	    return val.getVolatile();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }

    public static void main(final String[] args) throws Exception {
	System.out.print("padded,\t");
	if(args.length == 0 || args[0].equalsIgnoreCase("plain")){
	    System.out.print("plain,\t");
	    new PingPong(new PlainCounter(), new PlainCounter());
	}
	else if (args[0].equalsIgnoreCase("ordered")){
	    System.out.print("ordered,\t");
	    new PingPong(new OrderedCounter(), new OrderedCounter());
	}
	else{
	    System.out.print("volatile,\t");
	    new PingPong(new VolatileCounter(), new VolatileCounter());
	}
    }
}
