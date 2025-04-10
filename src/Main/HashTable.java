package Main;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

public class HashTable<K, V> implements BaseOperations<V> {

    // A simple entry class that stores key-value pairs
    private static class Entry<K, V> {
        K key;
        V value;
        boolean isDeleted; // A flag to mark the entry as deleted, for lazy deletion

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.isDeleted = false;
        }


        public void setDeleted() {
            this.value = null;
            this.isDeleted = true;
        }
    }

    private static class Bucket<K, V> {//extends LinkedList<Entry<K, V>> {
        LinkedList<Entry<K, V>> entries;

        Bucket() {
            entries = new LinkedList<>();
        }

        public int indexOf(K key) {
            if (key == null) return -1;

            int index = 0;
            for (Entry<K, V> nthEntry : entries) {
                if (key.equals(nthEntry.key))
                    return index;
                index++;
            }

            return -1;
        }

        public int indexOf(Entry<K, V> entry) { return entries.indexOf(entry); }


        public boolean contains(K key) {
            if (key == null) return false;

            for (Entry<K, V> entry : entries) {
                if(entry == null) continue;
                if (entry.key.equals(key)) return true;
            }
            return false;
        }

        public boolean contains(Entry<K, V> entry) { return entries.contains(entry); }

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

        public void add(Entry<K, V> entry) { entries.add(entry); }
        public boolean isEmpty() { return entries.isEmpty(); }
        public void updateEntry(K key, V newVal) { entries.get(indexOf(key)).value = newVal; }
    }

    public enum CollisionBehavior {
        Chaining,
        QuadraticProbing,
        Abort
    }


    private Bucket<K, V>[] table; // Array to hold entries of the hash table
    private int size; // The current number of entries in the hash table
    private final Function<V, K> valueToKeyConverter; // Lambda function to extract key from value
    private final CollisionBehavior collisionBehavior;

    private static final double MAX_LOAD_FACTOR = 0.5; // Load factor threshold for resizing the table
    private static final int INITIAL_CAPACITY = 20; // Initial capacity of the table
    private final int c1; // Coefficient for quadratic probing (1st term)
    private final int c2; // Coefficient for quadratic probing (2nd term)

    // Constructor that takes a lambda function to convert a value to a key
    public HashTable(CollisionBehavior collisionBehavior, Function<V, K> valueToKeyConverter, int c1, int c2) {
        this.collisionBehavior = collisionBehavior;
        this.valueToKeyConverter = valueToKeyConverter;
        this.table = new Bucket[INITIAL_CAPACITY]; // Initialize the table with a default capacity
        this.size = 0; // Start with an empty table
        this.c1 = c1;
        this.c2 = c2;

        for (int i = 0; i < table.length ; i++) {
            table[i] = new Bucket<>();
        }
    }

    public HashTable(CollisionBehavior collisionBehavior, Function<V, K> valueToKeyConverter) {
        this(collisionBehavior, valueToKeyConverter, -1, -1);
        if(collisionBehavior == CollisionBehavior.QuadraticProbing) {
            throw new Error("You are attempting to use quadratic probing without initializing the quadratic coefficients. Use the other constructor");
        }
    }

    // Hash function to calculate the index from the key
    private int hash(K key) {
        return Math.abs(key.hashCode()) % table.length; // Modulo to ensure it's within table bounds
    }

    // Probing function to find an available index using quadratic probing
    private int quadraticProbe(K key, boolean forInsert) {
        int index = hash(key); // Calculate initial index using hash function
        int i = 0; // Initialize the quadraticProbe attempt counter
        int firstDeletedSlot = -1; // Keep track of the first deleted slot (for reuse)

        int newIndex;
        Entry<K, V> entry;
        while (i < table.length) { // Loop over the table to find a spot
            // probe to a new index based on the formula and store the entry at that spot
            newIndex = (index + c1 * i + c2 * i * i) % table.length; // Quadratic probing formula
            entry = table[newIndex].getFirst();

            // If spot is empty, return it
            if (entry == null) return (forInsert && firstDeletedSlot != -1) ? firstDeletedSlot : newIndex;


            // If we find a deleted slot and we're inserting, save it for later reuse
            if (entry.isDeleted && forInsert) {
                if (firstDeletedSlot == -1) firstDeletedSlot = newIndex;
            }

            // If the key is found, return the index
            if (entry.key.equals(key)) return newIndex;

            i++; // Increment the quadraticProbe counter and try the next slot
        }

        return -1; // Return -1 if no available slot was found (table is full)
    }

    // enum for naming the return values
    enum RetVals {
        NO_COLLISIONS,
        CHAINED,
        UPDATED_A_VALUE;

        public final int val;
        RetVals() { this.val = this.ordinal(); }
        RetVals(int val) { this.val = val; }

        /**
         * @return the value associated with the name used
         * also allows for a function that returns an int to be called in the same line
         * */
        public int andDo(Supplier<Integer> something) { return val; }
        public int andDo(int something) { return val; }
        public int andDo(Runnable something) { something.run(); return val; }

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

    public int put(K key, V value) {

        // Resize the table if it exceeds the load factor threshold
        double loadFactor = (double)size / table.length;
        if ( loadFactor >= MAX_LOAD_FACTOR) {
            resize();
        }

        int indexInTable = hash(key);
        Bucket<K, V> bucket = table[indexInTable];
        Entry<K, V> toAdd = new Entry<>(key, value);
        Entry<K, V> firstEntry;

        if(bucket.isEmpty()) return RetVals.NO_COLLISIONS.andDo(simpleAdd(bucket, toAdd));


        switch (collisionBehavior) {
            case Chaining:
                // no entry with the given key exists in the bucket
                if (!bucket.contains(key)) return RetVals.CHAINED.andDo(simpleAdd(bucket, toAdd));

                break;

            case QuadraticProbing:
                indexInTable = quadraticProbe(key, true); // Try to find an indexInTable using quadratic probing

                if (indexInTable == -1) throw new RuntimeException("HashTable is full");
                bucket = table[indexInTable];
                firstEntry = bucket.getFirst();


                // If the index is empty or marked as deleted, insert the new element
                if (firstEntry == null || firstEntry.isDeleted) return RetVals.NO_COLLISIONS.andDo(simpleAdd(bucket, toAdd));

//                return RetVals.UPDATED_A_VALUE.andDo(() -> bucket.updateEntry(firstEntry.key, value)); // Update the value if the key already exists

                break;

            case Abort: default: return RetVals.NO_COLLISIONS.val;
        }


        // The entry with the given key exists in the bucket, set its corresponding value to the new value
        Bucket<K, V> finalBucket = bucket; //"Variable used in lambda expression should be final or effectively final" so there's this temp variable

        return RetVals.UPDATED_A_VALUE.andDo( () -> finalBucket.updateEntry(key, value) );
    }

    public V remove(K keyToRemove) {
        int index = hash(keyToRemove);
        Bucket<K, V> bucket = table[index];
        Entry<K, V> firstEntry;
        switch (collisionBehavior) {
            case Chaining:
                return simpleRemove(bucket, keyToRemove);

            case QuadraticProbing:
                index = quadraticProbe(keyToRemove, false);
                if (index == -1) return null;

                bucket = table[index];
                firstEntry = bucket.getFirst();

                if (firstEntry == null || firstEntry.isDeleted) return null;

                return simpleRemove(bucket, keyToRemove);

            case Abort: default: return null;
        }
    }

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



    @Override
    public boolean insert(V element) {
        K key = valueToKeyConverter.apply(element); // Extract the key from the element
        return put(key, element) >= 0;
    }

    @Override
    public V delete(V element) {
        K key = valueToKeyConverter.apply(element); // Extract the key from the element
        return remove(key);
    }

    @Override
    public V search(V value) {
        K key = valueToKeyConverter.apply(value); // Extract the key from the element
        return find(key);
    }

    // Resize the table when the load factor exceeds the threshold
    private void resize() {
        Bucket<K, V>[] oldTable = table; // Store the old table
        table = new Bucket[nextPrime(table.length * 2)]; // Create a new table with double the capacity
        size = 0; // Reset the size as we will reinsert the elements

        // Reinsert all elements from the old table into the new table
        for (Bucket<K, V> oldBucket : oldTable) {
            if (oldBucket.isEmpty()) continue;

            for (Entry<K, V> entry : oldBucket.entries) {
                insert(entry.value);
            }
        }
    }

    // Find the next prime number greater than or equal to a given number
    private int nextPrime(int n) {
        while (true) {
            if (isPrime(n)) return n; // Return the prime number if found
            n++; // Increment and check the next number
        }
    }

    // Check if a number is prime
    private boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        for (int i = 5; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    // Optional method to print the contents of the hash table
    public void printTable() {
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> entry = table[i].getFirst();
            System.out.print(i + ": ");
            if (entry == null) {
                System.out.println("null");
            } else if (entry.isDeleted) {
                System.out.println("deleted");
            } else {
                System.out.println(entry.key + " = " + entry.value);
            }
        }
    }
}

