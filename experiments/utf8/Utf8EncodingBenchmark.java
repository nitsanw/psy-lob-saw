package utf8;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
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
    private char[] chars;
    private CharBuffer charBuffer;
    private CharsetEncoder encoder;
    private CustomUtf8Encoder customEncoder;

    @Override
    protected void setUp() throws IOException {
	// @Param values are guaranteed to have been injected by now
	BufferedReader reader = null;
	try {
	    reader = new BufferedReader(new InputStreamReader(
		    new FileInputStream(stringsFile), "UTF-8"));
	    String line;
	    while ((line = reader.readLine()) != null) {
		strings.add(line);
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	finally{
	    if(reader != null)
		reader.close();
	}
	if (direct) {
	    dest = ByteBuffer.allocateDirect(4096);
	} else {
	    dest = ByteBuffer.allocate(4096);
	}
	chars = new char[4096];
	charBuffer = CharBuffer.wrap(chars);
	encoder = Charset.forName("UTF-8").newEncoder();
	customEncoder = new CustomUtf8Encoder();

    }

    public int timeCustomEncoder(int reps) {
	int countBytes = 0;
	for (int i = 0; i < reps; i++) {
	    for (int stringIndex = 0; stringIndex < strings.size(); stringIndex++) {
		customEncoder.encodeString(strings.get(stringIndex), dest);
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

    public int timeCharsetEncoder(int reps) throws UnsupportedEncodingException {
	int countBytes = 0;
	for (int i = 0; i < reps; i++) {
	    for (int stringIndex = 0; stringIndex < strings.size(); stringIndex++) {
		String source = strings.get(stringIndex);
		source.getChars(0, source.length(), chars, 0);
		charBuffer.clear();
		encoder.reset();
		charBuffer.limit(source.length());
		encoder.encode(charBuffer, dest, true);
		countBytes += dest.position();
		dest.clear();
	    }
	}
	return countBytes;
    }

}
