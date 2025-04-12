package Main;

public class AVLTree<T extends Comparable<T>> implements BaseOperations<T> {

    /**
     * Represents a node in the AVL tree.
     */
    protected class AVLNode {
        /** The data held by the node. */
        public T data;

        /** The height of the node in the AVL tree. */
        int height;

        /** The left child of this node. */
        public AVLNode left;

        /** The right child of this node. */
        public AVLNode right;

        /**
         * Constructs a new AVLNode with the specified data.
         *
         * @param data The data to store in the node.
         */
        public AVLNode(T data) {
            this.data = data;
            this.height = 1; // New nodes are initially leaf nodes
        }
    }

    /** The root node of the AVL tree. */
    private AVLNode root;

    /**
     * Returns the height of a given node.
     *
     * @param node The node whose height is to be calculated.
     * @return The height of the node, or 0 if the node is null.
     */
    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    /**
     * Calculates the balance factor of a node.
     *
     * @param node The node to calculate the balance factor for.
     * @return The balance factor (left height - right height).
     */
    private int balanceFactor(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    /**
     * Updates the height of a given node based on its children's heights.
     *
     * @param node The node whose height is to be updated.
     */
    private void updateHeight(AVLNode node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    /**
     * Performs a right rotation on the subtree rooted at the given parent node.
     *
     * @param parent The root of the subtree to rotate.
     * @return The new root of the rotated subtree.
     */
    private AVLNode rightRotate(AVLNode parent) {
        AVLNode pivot = parent.left;
        AVLNode rightChildOfPivot = pivot.right;

        pivot.right = parent;
        parent.left = rightChildOfPivot;

        updateHeight(parent);
        updateHeight(pivot);

        return pivot;
    }

    /**
     * Performs a left rotation on the subtree rooted at the given parent node.
     *
     * @param parent The root of the subtree to rotate.
     * @return The new root of the rotated subtree.
     */
    private AVLNode leftRotate(AVLNode parent) {
        AVLNode pivot = parent.right;
        AVLNode leftChildOfPivot = pivot.left;

        pivot.left = parent;
        parent.right = leftChildOfPivot;

        updateHeight(parent);
        updateHeight(pivot);

        return pivot;
    }

    /**
     * Balances the subtree rooted at the given node if it has become unbalanced.
     *
     * @param node The root of the subtree to balance.
     * @return The new root of the balanced subtree.
     */
    private AVLNode balance(AVLNode node) {
        int balanceFactor = balanceFactor(node); // Calculate the balance factor of the node

        if (balanceFactor > 1) { // The node is left-heavy
            // if the left child is right-heavy, perform left rotation on it
            if (balanceFactor(node.left) < 0)
                node.left = leftRotate(node.left);

            return rightRotate(node); // Perform right rotation on the node
        } else if (balanceFactor < -1) { // The node is right-heavy
            // if the right child is left-heavy, perform right rotation on it
            if (balanceFactor(node.right) > 0)
                node.right = rightRotate(node.right);

            return leftRotate(node); // Perform left rotation on the node
        }

        // Node is already balanced
        return node;
    }

    /**
     * Inserts a key into the AVL tree and ensures the tree remains balanced.
     *
     * @param currentNode The current node in the recursion.
     * @param key The key to insert.
     * @return The root node of the (sub)tree after insertion.
     */
    private AVLNode insertNode(AVLNode currentNode, T key) {
        // Found the correct position, create and return a new node
        if (currentNode == null) return new AVLNode(key);


        int compareResult = key.compareTo(currentNode.data);

        // If the key already exists, do not insert duplicates
        if (compareResult == 0) return currentNode;

        // Recursively insert into the left or right subtree
        if (compareResult < 0)
            currentNode.left = insertNode(currentNode.left, key);
        else
            currentNode.right = insertNode(currentNode.right, key);

        // Update height after insertion
        updateHeight(currentNode);

        // Rebalance the subtree if needed
        return balance(currentNode);
    }

    /**
     * Retrieves the node with the minimum value in the subtree.
     *
     * @param node The root of the subtree.
     * @return The node with the smallest value in the subtree.
     */
    private AVLNode getMinChild(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }


    /**
     * Recursively deletes a node with the given key from the AVL tree rooted at currentNode.
     * Ensures the AVL tree remains balanced after deletion.
     *
     * @param currentNode The root of the current subtree.
     * @param key The key to delete.
     * @return The new root of the subtree after deletion and balancing.
     */
    private AVLNode deleteNode(AVLNode currentNode, T key) {
        // Base case: If the current node is null, nothing to delete
        if (currentNode == null) return currentNode;

        // recurse on the left or right subtree
        if (key.compareTo(currentNode.data) < 0)
            currentNode.left = deleteNode(currentNode.left, key);
        else if (key.compareTo(currentNode.data) > 0)
            currentNode.right = deleteNode(currentNode.right, key);
        else { // found the node to delete
            // Case 1: Node has at most one child
            if ((currentNode.left == null) || (currentNode.right == null)) {
                AVLNode temp = (currentNode.left != null) ? currentNode.left : currentNode.right;

                // No child case
                if (temp == null) {
                    temp = currentNode;
                    currentNode = null;
                }
                // One child case
                else {
                    currentNode = temp; // Replace node with its child
                }
            }
            // Case 2: Node has two children
            else {
                // Find the inorder successor (smallest value in right subtree)
                AVLNode temp = getMinChild(currentNode.right);

                // Replace current node's data with successor's data
                currentNode.data = temp.data;

                // Delete the inorder successor
                currentNode.right = deleteNode(currentNode.right, temp.data);
            }
        }

        // If the tree had only one node, return
        if (currentNode == null) {
            return currentNode;
        }

        // Update the height of the current node
        updateHeight(currentNode);

        // Rebalance the node if necessary and return it
        return balance(currentNode);
    }

    /**
     * Performs an inorder traversal of the AVL tree, printing each node's data.
     * Inorder traversal visits nodes in ascending order.
     *
     * @param node The current node in the traversal.
     */
    private void inorderTraversal(AVLNode node) {
        if (node != null) {
            inorderTraversal(node.left);
            System.out.print(node.data + " ");
            inorderTraversal(node.right);
        }
    }

    /**
     * Recursively searches for a node with the specified key in the AVL tree.
     *
     * @param node The current node in the search.
     * @param key The key to search for.
     * @return The node containing the key, or null if not found.
     */
    private AVLNode searchNode(AVLNode node, T key) {
        if (node == null || node.data.equals(key)) {
            return node;
        }

        if (key.compareTo(node.data) < 0) {
            return searchNode(node.left, key);
        }

        return searchNode(node.right, key);
    }

    /**
     * Inserts the specified element into the AVL tree.
     *
     * @param element The element to insert.
     * @return True if the element was inserted successfully.
     */
    @Override
    public boolean insert(T element) {
        root = insertNode(root, element);
        return root != null;
    }

    /**
     * Deletes the specified element from the AVL tree.
     *
     * @param element The element to delete.
     * @return The deleted element.
     */
    @Override
    public T delete(T element) {
        root = deleteNode(root, element);
        return element;
    }

    /**
     * Searches for the specified element in the AVL tree.
     *
     * @param element The element to search for.
     * @return The element if found, or null if not found.
     */
    @Override
    public T search(T element) {
        AVLNode node = searchNode(root, element);
        return (node != null) ? node.data : null;
    }

    /**
     * Prints the elements of the AVL tree in inorder (ascending) order.
     */
    public void printTree() {
        inorderTraversal(root);
    }

}
