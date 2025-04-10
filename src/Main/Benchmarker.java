package Main;

import java.util.function.Consumer;
import java.util.function.Function;

public class Benchmarker<T> {
    public static final int SMALL_N  = 1_000;
    public static final int MEDIUM_N = 10_000;
    public static final int LARGE_N  = 100_000;


    BaseOperations<T> benchmarkee;

    public Benchmarker(BaseOperations<T> benchmarkee) {
        this.benchmarkee = benchmarkee;
    }

    private long benchmarkFunction(T[] values, Consumer<T> fn) {
        long endTime, startTime = System.nanoTime();
        for (T value : values) {
            fn.accept(value);
        }
        endTime = System.nanoTime();

        return endTime - startTime;
    }

    private long[] benchmarkFunction(T[][] valueArrays, Consumer<T> fn) {
        long[] times = new long[valueArrays.length];
        for (T[] valueArray : valueArrays) {
            benchmarkFunction(valueArray, fn);
        }
        return times;
    }


    public long[] benchmarkInsert(T[][] values) {
        return benchmarkFunction(values, (T t) -> benchmarkee.insert(t));
    }

    public long[] benchmarkDelete(T[][] values) {
        return benchmarkFunction(values, (T t) -> benchmarkee.delete(t));
    }

    public long[] benchmarkSearch(T[][] values) {
        return benchmarkFunction(values, (T t) -> benchmarkee.search(t));
    }
}
