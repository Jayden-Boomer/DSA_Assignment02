package Main;

public class Benchmarker<T> {
    public static final int SMALL_N  = 1_000;
    public static final int MEDIUM_N = 10_000;
    public static final int LARGE_N  = 100_000;

    BaseOperations<T> benchmarkee;

    public Benchmarker(BaseOperations<T> benchmarkee) {
        this.benchmarkee = benchmarkee;
    }

    private long benchmarkFunction(int N) {
        long time = 0;
        for (int i = 0; i < SMALL_N; i++) {

        }



        return time;
    }



    public long[] benchmarkInsert() {
        long[] times = new long[3];

        return times;
    }

    public long[] benchmarkDelete() {
        long[] times = new long[3];

        return times;
    }

    public long[] benchmarkSearch() {
        long[] times = new long[3];

        return times;
    }
}
