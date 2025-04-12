package Main;

import Main.HashTable.CollisionBehavior;
import Main.DataTable.Format;

import java.text.DecimalFormat;

public class Main {
    public static final int SMALL_N  = 1_000;
    public static final int MEDIUM_N = 10_000;
    public static final int LARGE_N  = 100_000;
    public static final int ITERATIONS_PER_DATASET = 1; //for some reason, running more trials makes the averages decrease (I suspect the dataset is being cached or something)
    public static final long CSV_TIME_FACTOR = 1_000_000;
    public static final long CSV_BYTE_FACTOR = 1;//1_000;

    public static DecimalFormat df = new DecimalFormat("#,###");
    public static final String[] colHeaders = new String[] {
            "Data Structure", (df.format(SMALL_N) + " Elements"), (df.format(MEDIUM_N) + " Elements"), (df.format(LARGE_N) + " Elements")
    };
    public static final String[] rowHeaders = new String[] {
            "AVL Tree", "Splay Tree", "Hash Table (Chaining)", "Hash Table (Quadratic Probing)"
    };

    public static void main(String[] args) {
        // generate the datasets
        Integer[][] datasets = new Integer[3][];
        datasets[0] = new Integer[SMALL_N];
        datasets[1] = new Integer[MEDIUM_N];
        datasets[2] = new Integer[LARGE_N];
        for (int i = 0; i < datasets.length; i++) {
            for (int j = 0; j < datasets[i].length; j++) {
                datasets[i][j] = (int)(Math.random() * (datasets[i].length/2.0)) + 1;;
            }
        }


        // create the benchmarkers
        Benchmarker<Integer> avlTreeBenchmarker = new Benchmarker<Integer>(new AVLTree<Integer>());
        Benchmarker<Integer> splayTreeBenchmarker = new Benchmarker<Integer>(new SplayTree<Integer>());
        Benchmarker<Integer> chainingHashTableBenchmarker = new Benchmarker<Integer>(new HashTable<Integer, Integer>(CollisionBehavior.Chaining, x -> x));
        Benchmarker<Integer> probingHashTableBenchmarker = new Benchmarker<Integer>(new HashTable<Integer, Integer>(CollisionBehavior.QuadraticProbing, x -> x));


        // create the table for insertion performance
        DataTable insertionTimesTable = new DataTable("Insertion Performance Comparison (Time in milliseconds)", colHeaders, rowHeaders);
        insertionTimesTable.AddRow(avlTreeBenchmarker.benchmarkInsertTime(datasets, ITERATIONS_PER_DATASET ));
        insertionTimesTable.AddRow(splayTreeBenchmarker.benchmarkInsertTime(datasets, ITERATIONS_PER_DATASET));
        insertionTimesTable.AddRow(chainingHashTableBenchmarker.benchmarkInsertTime(datasets, ITERATIONS_PER_DATASET));
        insertionTimesTable.AddRow(probingHashTableBenchmarker.benchmarkInsertTime(datasets, ITERATIONS_PER_DATASET));


        // create the table for search performance
        DataTable searchTimesTable = new DataTable("Search Performance Comparison (Time in milliseconds)", colHeaders, rowHeaders);
        searchTimesTable.AddRow(avlTreeBenchmarker.benchmarkSearchTime(datasets, ITERATIONS_PER_DATASET));
        searchTimesTable.AddRow(splayTreeBenchmarker.benchmarkSearchTime(datasets, ITERATIONS_PER_DATASET));
        searchTimesTable.AddRow(chainingHashTableBenchmarker.benchmarkSearchTime(datasets, ITERATIONS_PER_DATASET));
        searchTimesTable.AddRow(probingHashTableBenchmarker.benchmarkSearchTime(datasets, ITERATIONS_PER_DATASET));

        // create the table for search performance
        DataTable deletionTimesTable = new DataTable("Deletion Performance Comparison (Time in milliseconds)", colHeaders, rowHeaders);
        deletionTimesTable.AddRow(avlTreeBenchmarker.benchmarkDeleteTime(datasets, ITERATIONS_PER_DATASET));
        deletionTimesTable.AddRow(splayTreeBenchmarker.benchmarkDeleteTime(datasets, ITERATIONS_PER_DATASET));
        deletionTimesTable.AddRow(chainingHashTableBenchmarker.benchmarkDeleteTime(datasets, ITERATIONS_PER_DATASET));
        deletionTimesTable.AddRow(probingHashTableBenchmarker.benchmarkDeleteTime(datasets, ITERATIONS_PER_DATASET));


        // print the tables
        insertionTimesTable.print(Format.TIME);
        System.out.println("\n");
        searchTimesTable.print(Format.TIME);
        System.out.println("\n");
        deletionTimesTable.print(Format.TIME);
        System.out.println("\n\n");

        // create the table for insertion performance
        DataTable insertionMemTable = new DataTable("Insertion Performance Comparison (Memory in Kilobytes)", colHeaders, rowHeaders);
        insertionMemTable.AddRow(avlTreeBenchmarker.benchmarkInsertMemory(datasets, ITERATIONS_PER_DATASET));
        insertionMemTable.AddRow(splayTreeBenchmarker.benchmarkInsertMemory(datasets, ITERATIONS_PER_DATASET));
        insertionMemTable.AddRow(chainingHashTableBenchmarker.benchmarkInsertMemory(datasets, ITERATIONS_PER_DATASET));
        insertionMemTable.AddRow(probingHashTableBenchmarker.benchmarkInsertMemory(datasets, ITERATIONS_PER_DATASET));


        // create the table for search performance
        DataTable searchMemTable = new DataTable("Search Performance Comparison (Memory in Kilobytes)", colHeaders, rowHeaders);
        searchMemTable.AddRow(avlTreeBenchmarker.benchmarkSearchMemory(datasets, ITERATIONS_PER_DATASET));
        searchMemTable.AddRow(splayTreeBenchmarker.benchmarkSearchMemory(datasets, ITERATIONS_PER_DATASET));
        searchMemTable.AddRow(chainingHashTableBenchmarker.benchmarkSearchMemory(datasets, ITERATIONS_PER_DATASET));
        searchMemTable.AddRow(probingHashTableBenchmarker.benchmarkSearchMemory(datasets, ITERATIONS_PER_DATASET));

        // create the table for search performance
        DataTable deletionMemTable = new DataTable("Deletion Performance Comparison (Memory in Kilobytes)", colHeaders, rowHeaders);
        deletionMemTable.AddRow(avlTreeBenchmarker.benchmarkDeleteMemory(datasets, ITERATIONS_PER_DATASET));
        deletionMemTable.AddRow(splayTreeBenchmarker.benchmarkDeleteMemory(datasets, ITERATIONS_PER_DATASET));
        deletionMemTable.AddRow(chainingHashTableBenchmarker.benchmarkDeleteMemory(datasets, ITERATIONS_PER_DATASET));
        deletionMemTable.AddRow(probingHashTableBenchmarker.benchmarkDeleteMemory(datasets, ITERATIONS_PER_DATASET));


        // print the tables
        insertionMemTable.print(Format.MEMORY);
        System.out.println("\n");
        searchMemTable.print(Format.MEMORY);
        System.out.println("\n");
        deletionMemTable.print(Format.MEMORY);
        System.out.println("\n\n");

        // print the tables in csv format
        insertionTimesTable.printCSV(CSV_TIME_FACTOR);
        System.out.println("\n");
        searchTimesTable.printCSV(CSV_TIME_FACTOR);
        System.out.println("\n");
        deletionTimesTable.printCSV(CSV_TIME_FACTOR);
        System.out.println("\n");

        // print the tables in csv format
        insertionMemTable.printCSV(CSV_BYTE_FACTOR);
        System.out.println("\n");
        searchMemTable.printCSV(CSV_BYTE_FACTOR);
        System.out.println("\n");
        deletionMemTable.printCSV(CSV_BYTE_FACTOR);
        System.out.println("\n\n");

    }
}