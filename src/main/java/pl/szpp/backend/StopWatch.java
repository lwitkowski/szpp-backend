package pl.szpp.backend;

public class StopWatch {
    private long currentTime = System.currentTimeMillis();

    public long measure() {
        long t = System.currentTimeMillis() - currentTime;
        reset();
        return t;
    }

    public void reset() {
        currentTime = System.currentTimeMillis();
    }
}
