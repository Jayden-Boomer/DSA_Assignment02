package Main;

import java.util.function.Consumer;
import java.util.function.Function;

public class Benchmarker<T> {
    public static final int SMALL_N  = 1_000;
    public static final int MEDIUM_N = 10_000;
    public static final int LARGE_N  = 100_000;


    private BaseOperations<T> benchmarkee;

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

    private Long[] benchmarkFunction(T[][] valueArrays, Consumer<T> fn) {
        Long[] times = new Long[valueArrays.length];
        for (int i = 0; i < valueArrays.length; i++) {
            times[i] = benchmarkFunction(valueArrays[i], fn);
        }

        return times;
    }


    public Long[] benchmarkInsert(T[][] values) {
        return benchmarkFunction(values, (T t) -> benchmarkee.insert(t));
    }

    public Long[] benchmarkDelete(T[][] values) {
        return benchmarkFunction(values, (T t) -> benchmarkee.delete(t));
    }

    public Long[] benchmarkSearch(T[][] values) {
        return benchmarkFunction(values, (T t) -> benchmarkee.search(t));
    }
}
