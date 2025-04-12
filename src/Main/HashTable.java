package Main;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A generic hash table implementation supporting chaining, quadratic probing,
 * and abort-on-collision strategies.
 *
 * @param <K> The type of the key.
 * @param <V> The type of the value.
 */
public class HashTable<K, V> implements BaseOperations<V> {

    /**
     * Entry represents a key-value pair in the hash table.
     */
    private static class Entry<K, V> {
        K key;
        V value;
        boolean isDeleted;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.isDeleted = false;
        }

        /**
         * Marks the entry as deleted (for lazy deletion).
         */
        public void setDeleted() {
            this.value = null;
            this.isDeleted = true;
        }
    }

    /**
     * Bucket holds a list of entries and provides utility functions for entry management.
     */
    private static class Bucket<K, V> {
        LinkedList<Entry<K, V>> entries;

        Bucket() {
            entries = new LinkedList<>();
        }

        public int indexOf(K key) {
            if (key == null) return -1;
            int index = 0;
            for (Entry<K, V> e : entries) {
                if (key.equals(e.key)) return index;
                index++;
            }
            return -1;
        }

        public int indexOf(Entry<K, V> entry) {
            return entries.indexOf(entry);
        }

        public boolean contains(K key) {
            if (key == null) return false;
            for (Entry<K, V> e : entries) {
                if (e != null && e.key.equals(key)) return true;
            }
            return false;
        }

        public boolean contains(Entry<K, V> entry) {
            return entries.contains(entry);
        }

        public Entry<K, V> remove(K key) {
            int index = indexOf(key);
            if (index == -1) return new Entry<>(null, null);
            Entry<K, V> toRemove = entries.get(index);
            toRemove.setDeleted();
            return toRemove;
        }

        public Entry<K, V> getFirst() {
            try {
                return entries.getFirst();
            } catch (NoSuchElementException e) {
                return null;
            }
        }

        public Entry<K, V> get(int index) {
            try {
                return entries.get(index);
            } catch (IndexOutOfBoundsException e) {
                return new Entry<>(null, null);
            }
        }

        public void add(Entry<K, V> entry) {
            entries.add(entry);
        }

        public boolean isEmpty() {
            return entries.isEmpty();
        }

        public void updateEntry(K key, V newVal) {
            entries.get(indexOf(key)).value = newVal;
        }
    }

    /**
     * Specifies the collision handling behavior.
     */
    public enum CollisionBehavior {
        Chaining, QuadraticProbing, Abort
    }

    private Bucket<K, V>[] table;
    private int size;
    private final Function<V, K> valueToKeyConverter;
    private final CollisionBehavior collisionBehavior;
    private final int c1, c2;

    private static final double MAX_LOAD_FACTOR = 0.5;
    private static final int INITIAL_CAPACITY = 20;

    /**
     * Constructor for full customization.
     *
     * @param collisionBehavior Type of collision resolution.
     * @param valueToKeyConverter Function to extract keys from values.
     * @param c1 First quadratic probing coefficient.
     * @param c2 Second quadratic probing coefficient.
     */
    public HashTable(CollisionBehavior collisionBehavior, Function<V, K> valueToKeyConverter, int c1, int c2) {
        this.collisionBehavior = collisionBehavior;
        this.valueToKeyConverter = valueToKeyConverter;
        this.table = new Bucket[INITIAL_CAPACITY];
        this.size = 0;
        this.c1 = c1;
        this.c2 = c2;
        for (int i = 0; i < table.length; i++) {
            table[i] = new Bucket<>();
        }
    }

    /**
     * Constructor with default probing coefficients (c1 = 0, c2 = 1).
     *
     * @param collisionBehavior Type of collision resolution.
     * @param valueToKeyConverter Function to extract keys from values.
     */
    public HashTable(CollisionBehavior collisionBehavior, Function<V, K> valueToKeyConverter) {
        this(collisionBehavior, valueToKeyConverter, 0, 1);
    }

    /**
     * Adds a value to the table with a given key.
     *
     * @param key Key to associate with the value.
     * @param value Value to insert.
     * @return Result code (enum-backed int) indicating collision handling behavior used.
     */
    public int put(K key, V value) {
        double loadFactor = (double) size / table.length;
        if (loadFactor >= MAX_LOAD_FACTOR) resize();

        int index = hash(key);
        Bucket<K, V> bucket = table[index];
        Entry<K, V> toAdd = new Entry<>(key, value);

        if (bucket.isEmpty()) return RetVals.NO_COLLISIONS.andDo(simpleAdd(bucket, toAdd));

        switch (collisionBehavior) {
            case Chaining:
                if (!bucket.contains(key)) return RetVals.CHAINED.andDo(simpleAdd(bucket, toAdd));
                break;

            case QuadraticProbing:
                index = quadraticProbe(key, true);
                if (index == -1) throw new RuntimeException("HashTable is full");

                bucket = table[index];
                Entry<K, V> firstEntry = bucket.getFirst();
                if (firstEntry == null || firstEntry.isDeleted) return RetVals.NO_COLLISIONS.andDo(simpleAdd(bucket, toAdd));
                break;

            case Abort:
            default:
                return RetVals.NO_COLLISIONS.val;
        }

        Bucket<K, V> finalBucket = bucket;
        return RetVals.UPDATED_A_VALUE.andDo(() -> finalBucket.updateEntry(key, value));
    }

    /**
     * Removes an entry by its key.
     *
     * @param key Key to remove.
     * @return The removed value, or null if not found.
     */
    public V remove(K key) {
        int index = hash(key);
        Bucket<K, V> bucket = table[index];
        Entry<K, V> firstEntry;

        switch (collisionBehavior) {
            case Chaining:
                return simpleRemove(bucket, key);

            case QuadraticProbing:
                index = quadraticProbe(key, false);
                if (index == -1) return null;

                bucket = table[index];
                firstEntry = bucket.getFirst();
                if (firstEntry == null || firstEntry.isDeleted) return null;

                return simpleRemove(bucket, key);

            case Abort:
            default:
                return null;
        }
    }

    /**
     * Finds a value by key.
     *
     * @param key The key to look up.
     * @return The value associated with the key, or null if not found.
     */
    public V find(K key) {
        int index = hash(key);
        Bucket<K, V> bucket = table[index];
        Entry<K, V> firstEntry;

        switch (collisionBehavior) {
            case Chaining:
                return bucket.get(bucket.indexOf(key)).value;

            case QuadraticProbing:
                index = quadraticProbe(key, false);
                if (index == -1) return null;

                bucket = table[index];
                firstEntry = bucket.getFirst();
                if (firstEntry == null || firstEntry.isDeleted) return null;

                return firstEntry.value;

            case Abort:
            default:
                return null;
        }
    }

    /**
     * Prints the current state of the hash table.
     */
    public void printTable() {
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> entry = table[i].getFirst();
            System.out.print(i + ": ");
            if (entry == null)
                System.out.println("null");
            else if (entry.isDeleted)
                System.out.println("deleted");
            else
                System.out.println(entry.key + ", " + entry.value);
        }
    }

    // === Interface Implementations ===

    @Override
    public boolean insert(V element) {
        if (element == null) return false;
        K key = valueToKeyConverter.apply(element);
        return put(key, element) >= 0;
    }

    @Override
    public V delete(V element) {
        K key = valueToKeyConverter.apply(element);
        return remove(key);
    }

    @Override
    public V search(V value) {
        K key = valueToKeyConverter.apply(value);
        return find(key);
    }

    // === Helpers ===

    private int hash(K key) {
        return Math.abs(key.hashCode()) % table.length;
    }

    private int quadraticProbe(K key, boolean forInsert) {
        int index = hash(key);
        int i = 0, firstDeletedSlot = -1, newIndex;
        Entry<K, V> entry;

        while (i < table.length) {
            newIndex = (index + c1 * i + c2 * i * i) % table.length;
            entry = table[newIndex].getFirst();
            if (entry == null) return (forInsert && firstDeletedSlot != -1) ? firstDeletedSlot : newIndex;
            if (entry.isDeleted && forInsert && firstDeletedSlot == -1) firstDeletedSlot = newIndex;
            if (entry.key.equals(key)) return newIndex;
            i++;
        }

        return -1;
    }

    private void resize() {
        Bucket<K, V>[] oldTable = table;
        table = new Bucket[nextPrime(table.length * 2)];
        size = 0;
        for (int i = 0; i < table.length; i++) table[i] = new Bucket<>();
        for (Bucket<K, V> oldBucket : oldTable) {
            if (oldBucket.isEmpty()) continue;
            for (Entry<K, V> entry : oldBucket.entries) insert(entry.value);
        }
    }

    private int nextPrime(int n) {
        while (!isPrime(n)) n++;
        return n;
    }

    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        for (int i = 5; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    private int simpleAdd(Bucket<K, V> bucket, Entry<K, V> entry) {
        bucket.add(entry);
        size++;
        return 0;
    }

    private V simpleRemove(Bucket<K, V> bucket, K keyToRemove) {
        size--;
        V removedValue = bucket.get(bucket.indexOf(keyToRemove)).value;
        bucket.remove(keyToRemove);
        return removedValue;
    }

    /**
     * Internal enum for returning semantic status codes from put().
     */
    enum RetVals {
        NO_COLLISIONS,
        CHAINED,
        UPDATED_A_VALUE;

        public final int val;

        RetVals() { this.val = this.ordinal(); }

        public int andDo(Supplier<Integer> something) { return val; }
        public int andDo(int something) { return val; }
        public int andDo(Runnable something) { something.run(); return val; }
    }
}
