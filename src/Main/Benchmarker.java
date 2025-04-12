package Main;

import java.util.function.Consumer;

public class Benchmarker<T> {

    public static class BenchmarkResults {
        public long[] times;
        public long[] memoryUsages;

        BenchmarkResults(long[] times, long[] memUsages) {
            this.times = times;
            this.memoryUsages = memUsages;
        }
    }

    private static class BenchmarkResult {
        public long nanosecondsElapsed;
        public long bytesUsed;

        BenchmarkResult(long nanosecondsElapsed, long bytesUsed) {
            this.nanosecondsElapsed = nanosecondsElapsed;
            this.bytesUsed = bytesUsed;
        }
    }

    private BaseOperations<T> benchmarkee;

    public Benchmarker(BaseOperations<T> benchmarkee) {
        this.benchmarkee = benchmarkee;
    }

    private long[] benchmarkOperationTime(T[][] datasets, int numOfIterations, Consumer<T> fn) {
        long[] times = new long[datasets.length];
        for (int i = 0; i < datasets.length; i++) {
            times[i] = benchmarkOperationTime(datasets[i], numOfIterations, fn);
        }

        return times;
    }


    private long benchmarkOperationTime(T[] dataset, int numOfIterations, Consumer<T> fn) {

        long sum = 0;
        for (int i = 0; i < numOfIterations; i++) {
            sum += benchmarkOperationTime(dataset, fn);
        }
        return sum/numOfIterations;
    }

    private long benchmarkOperationTime(T[] dataset, Consumer<T> fn) {

        long endTime, startTime = System.nanoTime();
        for (T value : dataset) {
            fn.accept(value);
        }
        endTime = System.nanoTime();

        return endTime - startTime;
    }

    private long[] benchmarkOperationMemory(T[][] datasets, int numberOfIterationsPerDataset, Consumer<T> fn) {
        long[] memoryUsages = new long[datasets.length];
        for (int i = 0; i < datasets.length; i++) {
            memoryUsages[i] = benchmarkOperationMemory(datasets[i], numberOfIterationsPerDataset, fn);
        }

        return memoryUsages;
    }

    private long benchmarkOperationMemory(T[] datasets, int numberOfIterations, Consumer<T> fn) {
        long sum = 0;
        for (int i = 0; i < numberOfIterations; i++) {
            sum += benchmarkOperationMemory(datasets, fn);
        }

        return sum/numberOfIterations;
    }
    

    private long benchmarkOperationMemory(T[] dataset, Consumer<T> fn) {
        long startMemory, endMemory;

        System.gc(); // Request garbage collection
        Runtime runtime = Runtime.getRuntime();
        startMemory = runtime.totalMemory() - runtime.freeMemory();
        for (T value : dataset) {
            fn.accept(value);
        }
        System.gc(); // request GC again
        endMemory = runtime.totalMemory() - runtime.freeMemory();

        return endMemory - startMemory;
    }

    public long[] benchmarkInsertTime(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationTime(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.insert(t));
    }

    public long[] benchmarkDeleteTime(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationTime(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.delete(t));
    }

    public long[] benchmarkSearchTime(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationTime(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.search(t));
    }

    public long[] benchmarkInsertMemory(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationMemory(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.insert(t));
    }

    public long[] benchmarkDeleteMemory(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationMemory(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.delete(t));
    }

    public long[] benchmarkSearchMemory(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationMemory(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.search(t));
    }
}
