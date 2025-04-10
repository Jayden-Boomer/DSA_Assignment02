package Main;

import Main.HashTable.CollisionBehavior;

public class Main {
    public static final int SMALL_N  = 1_000;
    public static final int MEDIUM_N = 10_000;
    public static final int LARGE_N  = 100_000;

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
        Benchmarker<Integer> probingHashTableBenchmarker = new Benchmarker<Integer>(new HashTable<Integer, Integer>(CollisionBehavior.QuadraticProbing, x -> x, 1, 3));

        // create the table for insertion performance
        DataTable<Long> table = new DataTable<Long>(
                "Insertion Performance Comparison (Time in milliseconds)",
                new String[] { "Data Structure", (SMALL_N + " Elements"), (MEDIUM_N + " Elements"), (LARGE_N + " Elements") },
                new String[] { "AVL Tree", "Splay Tree", "Hash Table (Chaining)", "Hash Table (Quadratic Probing)" }
        );
        Long[] times = avlTreeBenchmarker.benchmarkInsert(datasets);
        for (Long time : times) {
            System.out.println(time);
        }
        table.AddRow(avlTreeBenchmarker.benchmarkInsert(datasets));
        table.AddRow(splayTreeBenchmarker.benchmarkInsert(datasets));
        table.AddRow(chainingHashTableBenchmarker.benchmarkInsert(datasets));
        table.AddRow(probingHashTableBenchmarker.benchmarkInsert(datasets));
        table.print();


    }
}