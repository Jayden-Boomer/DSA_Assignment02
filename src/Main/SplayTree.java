package Main;

/**
 * A Splay Tree implementation (self-balancing BST).
 * Every time a node is accessed, it is moved to the root via rotations.
 * This provides good average performance for a sequence of operations, with the complexity
 * of a single operation being O(log n) on average, though it can degrade to O(n) in the worst case.
 *
 * @param <T> The type of elements stored in the tree. Must be Comparable.
 */
public class SplayTree<T extends Comparable<T>> implements BaseOperations<T> {

    /**
     * Represents a node in the Splay Tree.
     */
    private class SplayNode {
        public T data;       // The data stored in the node
        public SplayNode left, right;  // Left and right child nodes

        /**
         * Constructs a new node with the given data.
         *
         * @param data The data to store in the node.
         */
        public SplayNode(T data) {
            this.data = data;
        }
    }

    private SplayNode root; // The root node of the tree

    /**
     * Splays the tree by bringing the node with the specified key to the root.
     *
     * @param root The root of the subtree to splay.
     * @param key The key of the node to splay.
     * @return The new root of the subtree after splaying.
     */
    private SplayNode splay(SplayNode root, T key) {
        if (root == null || root.data.equals(key)) return root; // the key was found or the tree is empty

        // If key lies in the left subtree
        if (key.compareTo(root.data) < 0) {
            if (root.left == null) return root; // the key is not in the tree

            // Perform zig-zig or zig-zag if needed
            if (key.compareTo(root.left.data) < 0) { // Zig-Zig (Left Left)
                root.left.left = splay(root.left.left, key);
                root = rotateRight(root);
            } else if (key.compareTo(root.left.data) > 0) { // Zig-Zag (Left Right)
                root.left.right = splay(root.left.right, key);
                if (root.left.right != null)
                    root.left = rotateLeft(root.left);
            }

            // no left child exists or key is left child
            return (root.left == null) ? root : rotateRight(root);
        } else { // If key lies in the right subtree
            if (root.right == null) return root; // the key is not in the tree

            // Perform zag-zig or zag-zag if needed
            if (key.compareTo(root.right.data) < 0) { // Zag-Zig (Right Left)
                root.right.left = splay(root.right.left, key);
                if (root.right.left != null)
                    root.right = rotateRight(root.right);
            } else if (key.compareTo(root.right.data) > 0) { // Zag-Zag (Right Right)
                root.right.right = splay(root.right.right, key);
                root = rotateLeft(root);
            }

            // no right child exists or key is right child
            return (root.right == null) ? root : rotateLeft(root);
        }
    }

    /**
     * Performs a right rotation on the given subtree.
     * The left child of the parent becomes the new root of the subtree.
     *
     * @param parent The root of the subtree to rotate.
     * @return The new root of the subtree after rotation.
     */
    private SplayNode rotateRight(SplayNode parent) {
        SplayNode pivot = parent.left;  // pivot is the left child of parent
        SplayNode rightChildOfPivot = pivot.right;  // store the right child of pivot

        // Perform rotation
        pivot.right = parent;  // parent becomes the right child of pivot
        parent.left = rightChildOfPivot;  // the right child of pivot becomes the left child of parent

        return pivot;  // Return the new root (pivot)
    }

    /**
     * Performs a left rotation on the given subtree.
     * The right child of the parent becomes the new root of the subtree.
     *
     * @param parent The root of the subtree to rotate.
     * @return The new root of the subtree after rotation.
     */
    private SplayNode rotateLeft(SplayNode parent) {
        SplayNode pivot = parent.right;  // pivot is the right child of parent
        SplayNode leftChildOfPivot = pivot.left;  // store the left child of pivot

        // Perform rotation
        pivot.left = parent;  // parent becomes the left child of pivot
        parent.right = leftChildOfPivot;  // the left child of pivot becomes the right child of parent

        return pivot;  // Return the new root (pivot)
    }

    /**
     * Inserts a key into the splay tree.
     *
     * @param key The key to insert.
     * @return true if the key was inserted, false if the key is a duplicate.
     */
    @Override
    public boolean insert(T key) {
        if (root == null) {
            root = new SplayNode(key);
            return true;
        }

        root = splay(root, key);

        int comparisonValue = key.compareTo(root.data);
        if (comparisonValue == 0) return false; // No duplicates

        // Create new node and insert it into the tree
        SplayNode newNode = new SplayNode(key);

        if (comparisonValue < 0) {
            newNode.right = root;
            newNode.left = root.left;
            root.left = null;
        } else {
            newNode.left = root;
            newNode.right = root.right;
            root.right = null;
        }

        root = newNode;
        return true;
    }

    /**
     * Deletes a key from the splay tree.
     *
     * @param key The key to delete.
     * @return The deleted key, or null if the key was not found.
     */
    @Override
    public T delete(T key) {
        if (root == null) return null;

        root = splay(root, key);

        if (!root.data.equals(key)) return null; // Key not found

        T deleted = root.data;

        // Remove the node and update the tree structure
        if (root.left == null) {
            root = root.right;
        } else {
            SplayNode temp = root.right;
            root = root.left;
            root = splay(root, key);  // Splay max of left subtree to root
            root.right = temp;
        }

        return deleted;
    }

    /**
     * Searches for a key in the splay tree.
     *
     * @param key The key to search for.
     * @return The key if found, or null if the key is not in the tree.
     */
    @Override
    public T search(T key) {
        root = splay(root, key);
        return (root != null && root.data.equals(key)) ? root.data : null;
    }

    /**
     * Prints the elements of the tree in in-order traversal.
     */
    public void printTree() {
        printInOrder(root);
    }

    /**
     * Recursively prints the elements of the tree in in-order traversal.
     *
     * @param node The current node to print.
     */
    private void printInOrder(SplayNode node) {
        if (node == null) return;
        printInOrder(node.left);
        System.out.print(node.data + " ");
        printInOrder(node.right);
    }
}
