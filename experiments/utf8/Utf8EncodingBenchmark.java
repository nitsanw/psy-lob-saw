package utf8;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;

public class Utf8EncodingBenchmark extends SimpleBenchmark {
    @Param(value = "utf8.txt")
    String stringsFile;
    @Param(value = "true")
    boolean direct;
    private ArrayList<String> strings = new ArrayList<String>();
    private ByteBuffer dest;

    @Override
    protected void setUp() {
	// @Param values are guaranteed to have been injected by now
	try {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(
		    new FileInputStream(stringsFile), "UTF-8"));
	    String line;
	    while ((line = reader.readLine()) != null) {
		strings.add(line);
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	if (direct) {
	    dest = ByteBuffer.allocateDirect(4096);
	} else {
	    dest = ByteBuffer.allocate(4096);
	}
    }

    public int timeCustomEncoder(int reps) {
	int countBytes = 0;
	CustomUtf8Encoder encoder = new CustomUtf8Encoder();
	for (int i = 0; i < reps; i++) {
	    for (int stringIndex = 0; stringIndex < strings.size(); stringIndex++) {
		encoder.encodeString(strings.get(stringIndex), dest);
		countBytes += dest.position();
		dest.clear();
	    }
	}
	return countBytes;
    }

    public int timeStringGetBytes(int reps) throws UnsupportedEncodingException {
	int countBytes = 0;
	for (int i = 0; i < reps; i++) {
	    for (int stringIndex = 0; stringIndex < strings.size(); stringIndex++) {
		dest.put(strings.get(stringIndex).getBytes("UTF-8"));
		countBytes += dest.position();
		dest.clear();
	    }
	}
	return countBytes;
    }

}
