package Main;

public class BinaryTree<T extends Comparable<T>> {
    protected class RotatableNode {
        public T data;
        public RotatableNode left, right;

        public RotatableNode(T data) {
            this.data = data;
        }

        public RotatableNode rotateRight() {
            // pivot is the left child of this node — it will become the new root of the subtree
            RotatableNode pivot = this.left;

            // store the right subtree of the pivot
            RotatableNode rightChildOfPivot = pivot.right;

            // Perform rotation
            pivot.right = this;               // parent becomes the right child of pivot
            this.left = rightChildOfPivot;    // the right child of pivot becomes the left child of parent

            // Return new root
            return pivot;
        }
    }
}
