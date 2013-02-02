package util;

public abstract class Counters {
    private Counters() {
    }

    public static final class SinglePlainCounter implements Counter {
	private final MemoryLong val = new VolatileLong(-1);

	@Override
	public void set(long l) {
	    val.directSet(l);
	}

	@Override
	public long get() {
	    return val.volatileGet();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }

    public static final class SingleOrderedCounter implements Counter {
	private final MemoryLong val = new VolatileLong(-1);

	@Override
	public void set(long l) {
	    val.lazySet(l);
	}

	@Override
	public long get() {
	    return val.volatileGet();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }

    public static final class SingleVolatileCounter implements Counter {
	private final MemoryLong val = new VolatileLong(-1);

	@Override
	public void set(long l) {
	    val.volatileSet(l);
	}

	@Override
	public long get() {
	    return val.volatileGet();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }

    public static final class SingleDirectCounter implements Counter {
	private final OffHeapLong val = new OffHeapLong(-1);

	@Override
	public void set(long l) {
	    val.directSet(l);
	}

	@Override
	public long get() {
	    return val.volatileGet();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }

    public static final class PaddedPlainCounter implements Counter {
	private final PaddedVolatileLong val = new PaddedVolatileLong(-1);

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
	private final PaddedVolatileLong val = new PaddedVolatileLong(-1);

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
	private final PaddedVolatileLong val = new PaddedVolatileLong(-1);

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

    public static final class DirectSinglePlainCounter implements Counter {
	private final OffHeapLong val = new OffHeapLong(-1);

	@Override
	public void set(long l) {
	    val.directSet(l);
	}

	@Override
	public long get() {
	    return val.volatileGet();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }

    public static final class DirectSingleOrderedCounter implements Counter {
	private final OffHeapLong val = new OffHeapLong(-1);

	@Override
	public void set(long l) {
	    val.lazySet(l);
	}

	@Override
	public long get() {
	    return val.volatileGet();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }

    public static final class DirectSingleVolatileCounter implements Counter {
	private final OffHeapLong val = new OffHeapLong(-1);

	@Override
	public void set(long l) {
	    val.volatileSet(l);
	}

	@Override
	public long get() {
	    return val.volatileGet();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }

    public static final class DirectSingleLongCounter implements Counter {
	private final OffHeapLong val = new OffHeapLong(-1);

	@Override
	public void set(long l) {
	    val.directSet(l);
	}

	@Override
	public long get() {
	    return val.volatileGet();
	}

	@Override
	public String toString() {
	    return val.toString();
	}
    }

    public static final class DirectPaddedPlainCounter implements Counter {
	private final OffHeapPaddedLong val = new OffHeapPaddedLong(-1);

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

    public static final class DirectPaddedOrderedCounter implements Counter {
	private final OffHeapPaddedLong val = new OffHeapPaddedLong(-1);

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

    public static final class DirectPaddedVolatileCounter implements Counter {
	private final OffHeapPaddedLong val = new OffHeapPaddedLong(-1);

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

    public static final class VolatileCounter implements Counter {
	private volatile long val = -1L;

	@Override
	public void set(long l) {
	    val = l;
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

    public static Counter createCounter(String... args) {
	Counter type = null;
	if (args.length != 3) {
	    throw new RuntimeException(
		    "Require couter type in 3 strings: [plain | ordered | volatile] [single | padded] [direct | plain] ");
	}
	String access = args[0];
	String size = args[1];
	String method = args[2];
	if (access.equals("plain")) {
	    if (size.equals("single")) {
		if (method.equals("plain")) {
		    type = new Counters.SinglePlainCounter();
		} else if (method.equals("direct")) {
		    type = new Counters.DirectSinglePlainCounter();
		}
	    } else if (size.equals("padded")) {
		if (method.equals("plain")) {
		    type = new Counters.PaddedPlainCounter();
		} else if (method.equals("direct")) {
		    type = new Counters.DirectPaddedPlainCounter();
		}
	    }
	} else if (access.equals("ordered")) {
	    if (size.equals("single")) {
		if (method.equals("plain")) {
		    type = new Counters.SingleOrderedCounter();
		} else if (method.equals("direct")) {
		    type = new Counters.DirectSingleOrderedCounter();
		}
	    } else if (size.equals("padded")) {
		if (method.equals("plain")) {
		    type = new Counters.PaddedOrderedCounter();
		} else if (method.equals("direct")) {
		    type = new Counters.DirectPaddedOrderedCounter();
		}
	    }
	} else if (access.equals("volatile")) {
	    if (size.equals("single")) {
		if (method.equals("plain")) {
		    type = new Counters.SingleVolatileCounter();
		} else if (method.equals("direct")) {
		    type = new Counters.DirectSingleVolatileCounter();
		}
	    } else if (size.equals("padded")) {
		if (method.equals("plain")) {
		    type = new Counters.PaddedVolatileCounter();
		} else if (method.equals("direct")) {
		    type = new Counters.DirectPaddedVolatileCounter();
		}
	    }
	}
	if (type == null) {
	    throw new RuntimeException(
		    "Require couter type in 3 strings: [plain | ordered | volatile] [single | padded] [direct | plain] ");

	} else {
	    return type;
	}
    }
}
