package Main;

import java.util.function.Consumer;

public class Benchmarker<T> {
    public static final int SMALL_N  = 1_000;
    public static final int MEDIUM_N = 10_000;
    public static final int LARGE_N  = 100_000;


    private BaseOperations<T> benchmarkee;

    public Benchmarker(BaseOperations<T> benchmarkee) {
        this.benchmarkee = benchmarkee;
    }

    private long benchmarkOperation(T[] values, Consumer<T> fn) {
        long endTime, startTime = System.nanoTime();
        for (T value : values) {
            fn.accept(value);
        }
        endTime = System.nanoTime();

        return endTime - startTime;
    }

    private long[] benchmarkOperation(T[][] valueArrays, Consumer<T> fn) {
        long[] times = new long[valueArrays.length];
        for (int i = 0; i < valueArrays.length; i++) {
            times[i] = benchmarkOperation(valueArrays[i], fn);
        }

        return times;
    }


    public long[] benchmarkInsert(T[][] valueLists) {
        return benchmarkOperation(valueLists, (T t) -> benchmarkee.insert(t));
    }

    public long[] benchmarkDelete(T[][] valueLists) {
        return benchmarkOperation(valueLists, (T t) -> benchmarkee.delete(t));
    }

    public long[] benchmarkSearch(T[][] valueLists) {
        return benchmarkOperation(valueLists, (T t) -> benchmarkee.search(t));
    }

    public static long measureMemoryUsed(Runnable operation) {
        System.gc(); // Request garbage collection (not guaranteed to run immediately)
        Runtime runtime = Runtime.getRuntime();
        long before = runtime.totalMemory() - runtime.freeMemory();

        operation.run(); // Run the actual operation

        System.gc(); // request GC again
        long after = runtime.totalMemory() - runtime.freeMemory();

        return after - before; // Memory used in bytes
    }

    private long benchmarkMemoryUsage(T[] values, Consumer<T> operation) {
        System.gc(); // Request garbage collection (not guaranteed to run immediately)
        Runtime runtime = Runtime.getRuntime();
        long before = runtime.totalMemory() - runtime.freeMemory();

        for (T value : values) {
            operation.accept(value);
        }

        System.gc(); // request GC again
        long after = runtime.totalMemory() - runtime.freeMemory();

        return after - before; // Memory used in bytes
    }

    public long[] benchmarkMemoryUsage(T[][] valueLists) {
        return benchmarkMemoryUsage(valueLists, (T t) -> {});
    }
}
