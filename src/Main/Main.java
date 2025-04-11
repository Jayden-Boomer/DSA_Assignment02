package Main;

import Main.HashTable.CollisionBehavior;

import java.text.DecimalFormat;

public class Main {
    public static final int SMALL_N  = 1_000;
    public static final int MEDIUM_N = 10_000;
    public static final int LARGE_N  = 100_000;

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
        DataTable insertionTable = new DataTable("Insertion Performance Comparison (Time in milliseconds)", colHeaders, rowHeaders);
        insertionTable.AddRow(avlTreeBenchmarker.benchmarkInsert(datasets));
        insertionTable.AddRow(splayTreeBenchmarker.benchmarkInsert(datasets));
        insertionTable.AddRow(chainingHashTableBenchmarker.benchmarkInsert(datasets));
        insertionTable.AddRow(probingHashTableBenchmarker.benchmarkInsert(datasets));


        // create the table for search performance
        DataTable searchTable = new DataTable("Search Performance Comparison (Time in milliseconds)", colHeaders, rowHeaders);
        searchTable.AddRow(avlTreeBenchmarker.benchmarkSearch(datasets));
        searchTable.AddRow(splayTreeBenchmarker.benchmarkSearch(datasets));
        searchTable.AddRow(chainingHashTableBenchmarker.benchmarkSearch(datasets));
        searchTable.AddRow(probingHashTableBenchmarker.benchmarkSearch(datasets));

        // create the table for search performance
        DataTable deletionTable = new DataTable("Deletion Performance Comparison (Time in milliseconds)", colHeaders, rowHeaders);
        deletionTable.AddRow(avlTreeBenchmarker.benchmarkDelete(datasets));
        deletionTable.AddRow(splayTreeBenchmarker.benchmarkDelete(datasets));
        deletionTable.AddRow(chainingHashTableBenchmarker.benchmarkDelete(datasets));
        deletionTable.AddRow(probingHashTableBenchmarker.benchmarkDelete(datasets));


        // print the tables
        insertionTable.print();
        System.out.println("\n");
        searchTable.print();
        System.out.println("\n");
        deletionTable.print();

        System.out.println("\n\n");

        // print the tables in csv format
        insertionTable.printCSV();
        System.out.println("\n");
        searchTable.printCSV();
        System.out.println("\n");
        deletionTable.printCSV();
    }
}