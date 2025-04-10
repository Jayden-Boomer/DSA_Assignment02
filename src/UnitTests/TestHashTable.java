package UnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import Main.HashTable;

public class TestHashTable {

    HashTable<Integer, Integer> chainingTable;
    HashTable<Integer, Integer> probingTable;

    @BeforeEach
    public void setup() {
        chainingTable = new HashTable<Integer, Integer>(HashTable.CollisionBehavior.Chaining, x -> x);
        probingTable = new HashTable<Integer, Integer>(HashTable.CollisionBehavior.QuadraticProbing, x -> x, 1, 3);
    }

    @Test
    public void testInsertAndSearch_Chaining() {
        assertTrue(chainingTable.insert(42));
        assertEquals(42, chainingTable.search(42));
    }

    @Test
    public void testInsertAndSearch_QuadraticProbing() {
        assertTrue(probingTable.insert(42));
        assertEquals(42, probingTable.search(42));
    }

    @Test
    public void testDelete_Chaining() {
        chainingTable.insert(5);
        chainingTable.printTable();
        assertEquals(5, chainingTable.delete(5));
        chainingTable.printTable();

        assertNull(chainingTable.search(5));
    }

    @Test
    public void testDelete_QuadraticProbing() {
        probingTable.insert(5);
        assertEquals(5, probingTable.delete(5));
        assertNull(probingTable.search(5));
    }

    @Test
    public void testUpdateValue_Chaining() {
        chainingTable.insert(10);
        chainingTable.put(10, 20);

        assertEquals(20, chainingTable.search(10));
    }

    @Test
    public void testUpdateValue_Probing() {
        probingTable.insert(10);
        probingTable.put(10, 20);
        assertEquals(20, probingTable.search(10));
    }

    @Test
    public void testCollisions_Chaining() {
        // These all hash to the same bucket in a small table
        chainingTable.put(0, 0);
        chainingTable.put(20, 20);
        chainingTable.put(40, 40);

        assertEquals(0, chainingTable.search(0));
        assertEquals(20, chainingTable.search(20));
        assertEquals(40, chainingTable.search(40));
    }

    @Test
    public void testCollisions_QuadraticProbing() {
        probingTable.insert(0);
        probingTable.insert(20); // collides with 0
        probingTable.insert(40); // likely collides again
        
        assertEquals(0, probingTable.search(0));
        assertEquals(20, probingTable.search(20));
        assertEquals(40, probingTable.search(40));
    }

    @Test
    public void testDeletedSlotsStillAllowSearch_Probing() {
        probingTable.insert(10);
        probingTable.insert(17); // same hash bucket, causes probe
        probingTable.delete(10); // deletes first in chain

        assertEquals(17, probingTable.search(17)); // should still be found
    }

    @Test
    public void testSearchForNonExistentKeyReturnsNull() {
        chainingTable.insert(1);
        assertNull(chainingTable.search(999));
    }

    @Test
    public void testInsertReturnsTrue() {
        assertTrue(probingTable.insert(99));
        assertTrue(chainingTable.insert(100));
    }

    @Test
    public void testRemoveNonExistentReturnsNull() {
        assertNull(probingTable.delete(12345));
    }
}
