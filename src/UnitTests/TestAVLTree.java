package UnitTests;

import Main.AVLTree;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestAVLTree {
//    public AVLTree<Integer> avlTree = new AVLTree<Integer>();

    @Test
    void testInsertionMaintainsBSTProperty() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(10);
        tree.insert(20);
        tree.insert(30);

        // In-order traversal of AVLTree should be sorted
        assertEquals("10 20 30 ", captureInOrder(tree));
    }

    @Test
    void testInsertionTriggersRightRotation() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(30);
        tree.insert(20);
        tree.insert(10); // Causes Left-Left (right rotation)

        assertEquals("10 20 30 ", captureInOrder(tree));
    }

    @Test
    void testInsertionTriggersLeftRotation() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(10);
        tree.insert(20);
        tree.insert(30); // Causes Right-Right (left rotation)

        assertEquals("10 20 30 ", captureInOrder(tree));
    }

    @Test
    void testInsertionTriggersLeftRightRotation() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(30);
        tree.insert(10);
        tree.insert(20); // Causes Left-Right (left then right)

        assertEquals("10 20 30 ", captureInOrder(tree));
    }

    @Test
    void testInsertionTriggersRightLeftRotation() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(10);
        tree.insert(30);
        tree.insert(20); // Causes Right-Left (right then left)

        assertEquals("10 20 30 ", captureInOrder(tree));
    }

    @Test
    void testSearchReturnsCorrectNode() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(15);

        assertEquals(15, tree.search(15));
        assertEquals(5, tree.search(5));
        assertNull(tree.search(100));
    }

    @Test
    void testDeleteLeafNode() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(15);

        tree.delete(5); // Deleting leaf

        assertEquals("10 15 ", captureInOrder(tree));
        assertNull(tree.search(5));
    }

    @Test
    void testDeleteNodeWithOneChild() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(10);
        tree.insert(5);
        tree.insert(2);

        tree.delete(5); // Node with one child (2)

        assertEquals("2 10 ", captureInOrder(tree));
        assertNull(tree.search(5));
    }

    @Test
    void testDeleteNodeWithTwoChildren() {
        AVLTree<Integer> tree = new AVLTree<Integer>();
        tree.insert(20);
        tree.insert(10);
        tree.insert(30);
        tree.insert(25);
        tree.insert(40);

        tree.delete(30); // Has two children: 25, 40

        assertEquals("10 20 25 40 ", captureInOrder(tree));
        assertNull(tree.search(30));
    }

    // Helper to capture in-order traversal output
    private String captureInOrder(AVLTree<Integer> tree) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));
        tree.printTree();
        return out.toString();
    }
}
