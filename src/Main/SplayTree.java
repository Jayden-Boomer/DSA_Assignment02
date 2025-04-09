package Main;

public class SplayTree<T extends Comparable<T>> implements BaseOperations<T> {
    
    private class SplayNode {
        public T data;
        public SplayNode left, right;
        public SplayNode(T data) {
            this.data = data;
        }
    }

    private SplayNode root;

    private SplayNode splay(SplayNode root, T key) {
        if (root == null || root.data.equals(key)) return root; // the key was found or the tree is empty


        if (key.compareTo(root.data) < 0) { // Key lies in left subtree
            if (root.left == null) return root; // the key is not in the tree

            //perform zig-zig or zig-zag if needed
            if (key.compareTo(root.left.data) < 0) { // key is left of the left child :  Zig-Zig (Left Left)
                root.left.left = splay(root.left.left, key);
                root = rotateRight(root);
            } else if (key.compareTo(root.left.data) > 0) { // key is right of the left child :  Zig-Zag (Left Right)
                root.left.right = splay(root.left.right, key);
                if (root.left.right != null)
                    root.left = rotateLeft(root.left);
            }

            // no left child exists or key is left child
            return (root.left == null) ? root : rotateRight(root);

        } else { // Key lies in right subtree
            if (root.right == null) return root; // the key is not in the tree

            //perform zag-zig or zag-zag if needed
            if (key.compareTo(root.right.data) < 0) { // key is left of the right child :  Zag-Zig (Right Left)
                root.right.left = splay(root.right.left, key);
                if (root.right.left != null)
                    root.right = rotateRight(root.right);
            } else if (key.compareTo(root.right.data) > 0) { // key is right of the right child :  Zag-Zag (Right Right)
                root.right.right = splay(root.right.right, key);
                root = rotateLeft(root);
            }

            // no right child exists or key is right child
            return (root.right == null) ? root : rotateLeft(root);
        }
    }

    private SplayNode rotateRight(SplayNode parent) {
        // pivot is the left child of parent — it will become the new root of the subtree
        SplayNode pivot = parent.left;

        // store the right subtree of the pivot
        SplayNode rightChildOfPivot = pivot.right;

        // Perform rotation
        pivot.right = parent;               // parent becomes the right child of pivot
        parent.left = rightChildOfPivot;    // the right child of pivot becomes the left child of parent

        // Return new root
        return pivot;
    }

    private SplayNode rotateLeft(SplayNode parent) {
        // pivot is the right child of parent — it will become the new root of the subtree
        SplayNode pivot = parent.right;

        // store the left subtree of the pivot
        SplayNode leftChildOfPivot = pivot.left;

        // Perform rotation
        pivot.left = parent;                // parent becomes the right child of pivot
        parent.right = leftChildOfPivot;    // the left child of pivot becomes the right child of parent

        // Return new root
        return pivot;
    }

    @Override
    public boolean insert(T key) {
        if (root == null) {
            root = new SplayNode(key);
            return true;
        }

        root = splay(root, key);

        int comparisonValue = key.compareTo(root.data);
        if (comparisonValue == 0) return false; // No duplicates

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

    @Override
    public T delete(T key) {
        if (root == null) return null;

        root = splay(root, key);

        if (!root.data.equals(key)) return null; // Key not found

        T deleted = root.data;

        if (root.left == null) {
            root = root.right;
        } else {
            SplayNode temp = root.right;
            root = root.left;
            root = splay(root, key); // Splay max of left subtree to root
            root.right = temp;
        }

        return deleted;
    }

    @Override
    public T search(T key) {
        root = splay(root, key);
        return (root != null && root.data.equals(key)) ? root.data : null;
    }

    // Optional: In-order traversal
    public void printTree() {
        printInOrder(root);
//        System.out.println();
    }

    private void printInOrder(SplayNode node) {
        if (node == null) return;
        printInOrder(node.left);
        System.out.print(node.data + " ");
        printInOrder(node.right);
    }
}

