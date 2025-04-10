package Main;

import Main.HashTable.CollisionBehavior;

public class Main<T> {
    public static void main(String[] args) {
        Benchmarker<Integer> avlTreeBenchmarker = new Benchmarker<Integer>(new AVLTree<Integer>());
        Benchmarker<Integer> splayTreeBenchmarker = new Benchmarker<Integer>(new SplayTree<Integer>());
        Benchmarker<Integer> chainingHashTableBenchmarker = new Benchmarker<Integer>(new HashTable<Integer, Integer>(CollisionBehavior.Chaining, x -> x));
        Benchmarker<Integer> probingHashTableBenchmarker = new Benchmarker<Integer>(new HashTable<Integer, Integer>(CollisionBehavior.QuadraticProbing, x -> x));
    }
}