package Main;

import java.util.function.Consumer;

/**
 * A benchmarking utility for measuring the time and memory usage
 * of operations implemented in a {@link BaseOperations} instance.
 *
 * @param <T> The type of elements to be benchmarked.
 */
public class Benchmarker<T> {

    private BaseOperations<T> benchmarkee;

    /**
     * Constructs a new Benchmarker with the provided {@link BaseOperations} instance.
     *
     * @param benchmarkee The object whose operations will be benchmarked.
     */
    public Benchmarker(BaseOperations<T> benchmarkee) {
        this.benchmarkee = benchmarkee;
    }

    /**
     * Benchmarks the execution time of an operation over multiple datasets, averaging some number of trials for each dataset
     *
     * @param datasets The input datasets.
     * @param numOfIterations Number of iterations to average results.
     * @param fn The operation to benchmark (insert, delete, or search).
     * @return An array of average execution times in nanoseconds.
     */
    private long[] benchmarkOperationTime(T[][] datasets, int numOfIterations, Consumer<T> fn) {
        long[] times = new long[datasets.length];
        for (int i = 0; i < datasets.length; i++) {
            times[i] = benchmarkOperationTime(datasets[i], numOfIterations, fn);
        }
        return times;
    }

    /**
     * Benchmarks the execution time of an operation over a single dataset.
     *
     * @param dataset The input dataset.
     * @param numOfIterations Number of iterations to average results.
     * @param fn The operation to benchmark.
     * @return The average execution time in nanoseconds.
     */
    private long benchmarkOperationTime(T[] dataset, int numOfIterations, Consumer<T> fn) {
        long sum = 0;
        for (int i = 0; i < numOfIterations; i++) {
            sum += benchmarkOperationTime(dataset, fn);
        }
        return sum / numOfIterations;
    }

    /**
     * Benchmarks the total execution time for a batch of operations on a dataset.
     *
     * @param dataset The input dataset.
     * @param fn The operation to benchmark.
     * @return The total execution time in nanoseconds.
     */
    private long benchmarkOperationTime(T[] dataset, Consumer<T> fn) {
        long startTime = System.nanoTime();
        for (T value : dataset) {
            fn.accept(value);
        }
        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    /**
     * Benchmarks memory usage of an operation across multiple datasets, averaging some number of trials for each dataset
     *
     * @param datasets The input datasets.
     * @param numberOfIterationsPerDataset Number of iterations to average memory usage.
     * @param fn The operation to benchmark.
     * @return An array of average memory usage in bytes.
     */
    private long[] benchmarkOperationMemory(T[][] datasets, int numberOfIterationsPerDataset, Consumer<T> fn) {
        long[] memoryUsages = new long[datasets.length];
        for (int i = 0; i < datasets.length; i++) {
            memoryUsages[i] = benchmarkOperationMemory(datasets[i], numberOfIterationsPerDataset, fn);
        }
        return memoryUsages;
    }

    /**
     * Benchmarks memory usage for a single dataset.
     *
     * @param datasets The dataset to operate on.
     * @param numberOfIterations Number of iterations to average results.
     * @param fn The operation to benchmark.
     * @return The average memory usage in bytes.
     */
    private long benchmarkOperationMemory(T[] datasets, int numberOfIterations, Consumer<T> fn) {
        long sum = 0;
        for (int i = 0; i < numberOfIterations; i++) {
            sum += benchmarkOperationMemory(datasets, fn);
        }
        return sum / numberOfIterations;
    }

    /**
     * Invokes the garbage collector and allows a brief pause for memory cleanup.
     */
    private void garbageCollect() {
        System.gc();
        try {
            //for some reason this requires a try catch even though the sleep amount is a literal
            Thread.sleep(50);
        } catch (InterruptedException e) {
            // Safe to ignore in this context
        }
    }

    /**
     * Benchmarks memory usage for a single operation execution over a dataset.
     *
     * @param dataset The dataset to benchmark.
     * @param fn The operation to apply.
     * @return The difference in memory usage in bytes.
     */
    private long benchmarkOperationMemory(T[] dataset, Consumer<T> fn) {
        garbageCollect();
        Runtime runtime = Runtime.getRuntime();
        long startMemory = runtime.totalMemory() - runtime.freeMemory();

        for (T value : dataset) {
            fn.accept(value);
        }

        long endMemory = runtime.totalMemory() - runtime.freeMemory();
        return endMemory - startMemory;
    }

    /**
     * Benchmarks the time it takes to perform insert operations on multiple datasets, averaging some number of trials for each dataset
     *
     * @param datasets A 2D array where each inner array is a dataset to be inserted.
     * @param numberOfIterationsPerDataset The number of times to repeat each benchmark for averaging.
     * @return An array of average insertion times in nanoseconds, one per dataset.
     */
    public long[] benchmarkInsertTime(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationTime(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.insert(t));
    }

    /**
     * Benchmarks the time it takes to perform delete operations on multiple datasets, averaging some number of trials for each dataset
     *
     * @param datasets A 2D array where each inner array is a dataset of elements to be deleted.
     * @param numberOfIterationsPerDataset The number of times to repeat each benchmark for averaging.
     * @return An array of average deletion times in nanoseconds, one per dataset.
     */
    public long[] benchmarkDeleteTime(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationTime(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.delete(t));
    }

    /**
     * Benchmarks the time it takes to search for elements in multiple datasets, averaging some number of trials for each dataset
     *
     * @param datasets A 2D array where each inner array is a dataset of elements to search for.
     * @param numberOfIterationsPerDataset The number of times to repeat each benchmark for averaging.
     * @return An array of average search times in nanoseconds, one per dataset.
     */
    public long[] benchmarkSearchTime(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationTime(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.search(t));
    }

    /**
     * Benchmarks the approximate memory usage of insert operations on multiple datasets, averaging some number of trials for each dataset
     *
     * @param datasets A 2D array of datasets to be inserted.
     * @param numberOfIterationsPerDataset The number of times to repeat each benchmark for averaging.
     * @return An array of average memory usage in bytes for each dataset.
     */
    public long[] benchmarkInsertMemory(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationMemory(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.insert(t));
    }

    /**
     * Benchmarks the approximate memory usage of delete operations on multiple datasets, averaging some number of trials for each dataset
     *
     * @param datasets A 2D array of datasets whose elements will be deleted.
     * @param numberOfIterationsPerDataset The number of times to repeat each benchmark for averaging.
     * @return An array of average memory usage in bytes for each dataset.
     */
    public long[] benchmarkDeleteMemory(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationMemory(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.delete(t));
    }

    /**
     * Benchmarks the approximate memory usage of search operations on multiple datasets, averaging some number of trials for each dataset
     *
     * @param datasets A 2D array of datasets containing elements to search for.
     * @param numberOfIterationsPerDataset The number of times to repeat each benchmark for averaging.
     * @return An array of average memory usage in bytes for each dataset.
     */
    public long[] benchmarkSearchMemory(T[][] datasets, int numberOfIterationsPerDataset) {
        return benchmarkOperationMemory(datasets, numberOfIterationsPerDataset, (T t) -> benchmarkee.search(t));
    }
}
