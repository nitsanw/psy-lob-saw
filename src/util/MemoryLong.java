package util;

public interface MemoryLong {

    long volatileGet();

    long directGet();

    void directSet(long value);

    void lazySet(long value);

    void volatileSet(long value);

    boolean compareAndSet(long expectedValue, long newValue);

}