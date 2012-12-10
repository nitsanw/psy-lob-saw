package pingpong;

import util.DirectLong;
import util.DirectPaddedLong;

public final class DirectPingPong {
    public static void main(final String[] args) throws Exception {
	if (args.length == 0 || args[0].equalsIgnoreCase("single")) {
	    System.out.print("single,\tdirect,\t");
	    new PingPong(new DirectLong(-1L), new DirectLong(-1L));
	} else {
	    System.out.print("padded,\tdirect,\t");
	    new PingPong(new DirectPaddedLong(-1L), new DirectPaddedLong(-1L));
	}
    }
}
