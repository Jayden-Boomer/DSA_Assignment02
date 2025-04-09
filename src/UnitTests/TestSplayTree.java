package UnitTests;

import Main.SplayTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestSplayTree {

    // Helper method to capture in-order output
    private String captureInOrder(SplayTree<Integer> tree) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        tree.printTree();
        return out.toString();
    }

    @Test
    void testInsertAndSplayToRoot() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(10);
        tree.insert(20);
        tree.insert(5);

        assertEquals(5, tree.search(5)); // Should splay 5 to root
        assertEquals("5 10 20 ", captureInOrder(tree));
    }

    @Test
    void testSearchMovesElementToRoot() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);

        assertEquals(40, tree.search(40)); // 40 should be at root after search
        assertEquals("20 30 40 50 70 ", captureInOrder(tree));
    }

    @Test
    void testSearchNonExistentValueReturnsNull() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(10);
        tree.insert(20);
        assertNull(tree.search(100));
    }

    @Test
    void testDeleteRootNode() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(20);

        assertEquals(20, tree.search(20)); // Splay 20 to root
        assertEquals(20, tree.delete(20)); // Delete root
        assertEquals("5 10 ", captureInOrder(tree));
    }

    @Test
    void testDeleteLeafNode() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(20);

        assertEquals(5, tree.delete(5)); // Deleting leaf
        assertEquals("10 20 ", captureInOrder(tree));
    }

    @Test
    void testDeleteNonExistentValue() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(10);
        tree.insert(5);
        assertNull(tree.delete(100));
        assertEquals("5 10 ", captureInOrder(tree));
    }

    @Test
    void testInsertDuplicatesAreIgnored() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(10);
        tree.insert(10);
        tree.insert(10);
        assertEquals("10 ", captureInOrder(tree));
    }

    @Test
    void testMultipleInsertionsAndStructure() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        int[] values = {50, 30, 70, 20, 40, 60, 80};
        for (int val : values) {
            tree.insert(val);
        }
        assertEquals("20 30 40 50 60 70 80 ", captureInOrder(tree));
    }

    @Test
    void testZigZigRotation() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(100);
        tree.insert(50);
        tree.insert(25); // This should trigger a zig-zig (left-left)

        assertEquals(25, tree.search(25)); // 25 should be splayed to root
        assertEquals("25 50 100 ", captureInOrder(tree));
    }

    @Test
    void testZigZagRotation() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(100);
        tree.insert(50);
        tree.insert(75); // Should trigger zig-zag (left-right)

        assertEquals(75, tree.search(75));
        assertEquals("50 75 100 ", captureInOrder(tree));
    }

    @Test
    void testZagZigRotation() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(10);
        tree.insert(30);
        tree.insert(20); // Should trigger zag-zig (right-left)

        assertEquals(20, tree.search(20));
        assertEquals("10 20 30 ", captureInOrder(tree));
    }

    @Test
    void testZagZagRotation() {
        SplayTree<Integer> tree = new SplayTree<Integer>();
        tree.insert(10);
        tree.insert(20);
        tree.insert(30); // Should trigger zag-zag (right-right)

        assertEquals(30, tree.search(30));
        assertEquals("10 20 30 ", captureInOrder(tree));
    }
}
