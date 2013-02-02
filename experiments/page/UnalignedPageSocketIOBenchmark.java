package page;

import static util.UnsafeDirectByteBuffer.PAGE_SIZE;
import static util.UnsafeDirectByteBuffer.allocateAlignedByteBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

public class UnalignedPageSocketIOBenchmark extends SimpleBenchmark {
    @Param({ "48", "-16", "0", "-64", "64" })
    int offset;

    private ByteBuffer buffy;
    private SocketChannel client = null;
    private Exception failed;

    @Override
    protected void setUp() throws Exception {
	buffy = allocateAlignedByteBuffer(2 * PAGE_SIZE, PAGE_SIZE);
	buffy.position(PAGE_SIZE + offset);
	buffy.limit(buffy.position() + 32);
	buffy = buffy.slice().order(ByteOrder.nativeOrder());
	String host = "localhost";
	int port = 12345;
	final ServerSocketChannel serverSocket = ServerSocketChannel.open();
	serverSocket.socket().bind(new InetSocketAddress(host, port));
	final CountDownLatch waitForAccept = new CountDownLatch(1);
	Thread t = new Thread(new Runnable() {

	    public void run() {
		SocketChannel server = null;
		try {
		    serverSocket.socket().setReceiveBufferSize(PAGE_SIZE);
		    server = serverSocket.accept();
		    server.socket().setSendBufferSize(PAGE_SIZE);
		    server.socket().setTcpNoDelay(true);
		    server.configureBlocking(false);
		    serverSocket.close();
		    waitForAccept.countDown();
		    ByteBuffer spike = allocateAlignedByteBuffer(2 * PAGE_SIZE,
			    PAGE_SIZE);
		    spike.position(PAGE_SIZE + offset);
		    spike.limit(spike.position() + 32);
		    spike = spike.slice().order(ByteOrder.nativeOrder());
		    while (!Thread.interrupted()) {
			spike.clear();
			do {
			    if (server.read(spike) == -1)
				return;
			} while (spike.hasRemaining());
			spike.flip();
			do {
			    server.write(spike);
			} while (spike.hasRemaining());
		    }

		} catch (Exception e) {
		    // TODO Auto-generated catch block
		    failed = e;
		    waitForAccept.countDown();
		}
		finally{
		    if (server != null){
			try {
			    server.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		    }
		}
	    }
	});
	t.start();
	client = SocketChannel.open();
	client.socket().setTcpNoDelay(true);
	client.configureBlocking(false);
	client.socket().setReceiveBufferSize(PAGE_SIZE);
	client.connect(new InetSocketAddress(host, port));
	while(!client.finishConnect());
	waitForAccept.await();
	if (failed != null) {
	    throw failed;
	}

    }

    @Override
    protected void tearDown() throws Exception {
	if (client != null)
	    client.close();
	
    }

    public int timeOffsetPageIO(final int reps) throws IOException {
	for (int i = 0; i < reps; i++) {
	    ping(i);
	}
	return (int) buffy.getInt();
    }

    private void ping(int i) throws IOException {
	buffy.putInt(0, i);
	do{
	    client.write(buffy);
	} while (buffy.hasRemaining());
	buffy.clear();
	buffy.putInt(0, 0);
	do{
	    client.read(buffy);
	}while (buffy.hasRemaining());
	if (buffy.getInt(0) != i)
	    throw new RuntimeException();
	buffy.clear();
    }

    public static void main(String[] args) throws Exception {
	Runner.main(UnalignedPageSocketIOBenchmark.class, args);
    }
}
