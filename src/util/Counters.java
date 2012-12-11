package util;

public abstract class Counters {
    private Counters(){}
    public static final class SinglePlainCounter implements Counter {
	private final SingleLong val = new SingleLong(-1);

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
    public static final class SingleOrderedCounter implements Counter {
	private final SingleLong val = new SingleLong(-1);

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
    public static final class SingleVolatileCounter implements Counter {
	private final SingleLong val = new SingleLong(-1);

	@Override
	public void set(long l) {
	    val.setVolatile(l);
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
    public static final class SingleDirectCounter implements Counter {
	private final DirectLong val = new DirectLong(-1);

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
    public static final class PaddedPlainCounter implements Counter {
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
    public static final class PaddedOrderedCounter implements Counter {
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
    public static final class PaddedVolatileCounter implements Counter {
	private final PaddedLong val = new PaddedLong(-1);

	@Override
	public void set(long l) {
	    val.setVolatile(l);
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
    public static final class PaddedDirectCounter implements Counter {
	private final DirectPaddedLong val = new DirectPaddedLong(-1);

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
    public static final class VolatileCounter implements Counter {
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
    public static Counter createCounter(String...args){
	Counter type;
	if(args.length == 0 || args[0].equals("single")){
	    if(args.length < 2 || args[1].equals("plain")){
		type = new Counters.SinglePlainCounter();
	    } else if(args[1].equals("ordered")){
		type = new Counters.SingleOrderedCounter();
	    } else if(args[1].equals("volatile")){
		type = new Counters.SingleVolatileCounter();
	    } else {
		type = new Counters.SingleDirectCounter();
	    }
	}
	else if (args[0].equals("padded")){
	    if(args.length < 2 || args[1].equals("plain")){
		type = new Counters.PaddedPlainCounter();
	    } else if(args[1].equals("ordered")){
		type = new Counters.PaddedOrderedCounter();
	    } else if(args[1].equals("volatile")){
		type = new Counters.PaddedVolatileCounter();
	    } else {
		type = new Counters.PaddedDirectCounter();
	    }
	}
	else{
	    type = new Counters.VolatileCounter();
	}
	return type;
    }
}
