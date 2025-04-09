package Main;

public class AVLTree<T extends Comparable<T>> implements BaseOperations<T> {

    protected class AVLNode {
        public T data;
        int height;
        public AVLNode left;
        public AVLNode right;

        public AVLNode(T data) {
            this.data = data;
            this.height = 1;
        }
    }

    private AVLNode root;

    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(AVLNode node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private AVLNode rightRotate(AVLNode parent) {
        // pivot is the left child of parent — it will become the new root of the subtree
        AVLNode pivot = parent.left;

        // store the right subtree of the pivot
        AVLNode rightChildOfPivot = pivot.right;

        // Perform rotation
        pivot.right = parent;               // parent becomes the right child of pivot
        parent.left = rightChildOfPivot;    // the right child of pivot becomes the left child of parent

        //update the heights of the nodes
        updateHeight(parent);
        updateHeight(pivot);

        // Return new root
        return pivot;
    }

    private AVLNode leftRotate(AVLNode parent) {
        // pivot is the right child of parent — it will become the new root of the subtree
        AVLNode pivot = parent.right;

        // store the left subtree of the pivot
        AVLNode leftChildOfPivot = pivot.left;

        // Perform rotation
        pivot.left = parent;                // parent becomes the left child of pivot
        parent.right = leftChildOfPivot;    // the left child of pivot becomes the right child of parent

        //update the heights of the nodes
        updateHeight(parent);
        updateHeight(pivot);

        // Return new root
        return pivot;
    }

    private AVLNode balance(AVLNode node) {
        int balanceFactor = balanceFactor(node);

        if (balanceFactor > 1) { // The given node is left-heavy
            // left-rotate the left child if it is also left-heavy
            if(balanceFactor(node.left) < 0)
                node.left = leftRotate(node.left);

            // right rotate the given node
            return rightRotate(node);

        } else if (balanceFactor < -1) { // The given node is right-heavy
            // right-rotate the right child if it is also right-heavy
            if(balanceFactor(node.right) > 0)
                node.right = rightRotate(node.right);

            // right rotate the given node
            return leftRotate(node);
        }

        return node; // Return the balanced node
    }

//    private AVLNode balance(AVLNode node) {
//        int balanceFactor = balanceFactor(node);
//
//        // Left Heavy (Right Rotation)
//        if (balanceFactor > 1 && balanceFactor(node.left) >= 0) {
//            return rightRotate(node);
//        }
//
//        // Left-Right Case (Left Rotate Left Child, then Right Rotate)
//        if (balanceFactor > 1 && balanceFactor(node.left) < 0) {
//            node.left = leftRotate(node.left);
//            return rightRotate(node);
//        }
//
//        // Right Heavy (Left Rotation)
//        if (balanceFactor < -1 && balanceFactor(node.right) <= 0) {
//            return leftRotate(node);
//        }
//
//        // Right-Left Case (Right Rotate Right Child, then Left Rotate)
//        if (balanceFactor < -1 && balanceFactor(node.right) > 0) {
//            node.right = rightRotate(node.right);
//            return leftRotate(node);
//        }
//
//        return node; // Return the balanced node
//    }


    private AVLNode insertNode(AVLNode currentNode, T key) {
        if (currentNode == null) {
            return new AVLNode(key);
        }

        int compareResult = key.compareTo(currentNode.data);

        //no duplicates allowed, return if given key is a duplicate
        if(compareResult == 0) return currentNode;

        //recursively call insertNode on the child of the current node
        if (compareResult < 0)
            currentNode.left = insertNode(currentNode.left, key);
        else
            currentNode.right = insertNode(currentNode.right, key);

        // adjust the height of the current node and check its new balance factor
        updateHeight(currentNode);//.height = 1 + Math.max(height( currentNode.left), height( currentNode.right));

        return balance(currentNode);
//        int balance = balanceFactor(currentNode);
//
//        if (balance > 1 && key.compareTo(currentNode.left.data) < 0) {
//            return rightRotate(currentNode);
//        }
//        if (balance < -1 && key.compareTo(currentNode.right.data) > 0) {
//            return leftRotate(currentNode);
//        }
//        if (balance > 1 && key.compareTo(currentNode.left.data) > 0) {
//            currentNode.left = leftRotate( currentNode.left);
//            return rightRotate(currentNode);
//        }
//        if (balance < -1 && key.compareTo(currentNode.right.data) < 0) {
//            currentNode.right = rightRotate( currentNode.right);
//            return leftRotate(currentNode);
//        }
//
//        return currentNode;
    }

    private AVLNode getMinChild(AVLNode node) {
        AVLNode current = node;
        while (current.left != null) {
            current =  current.left;
        }
        return current;
    }

    private AVLNode deleteNode(AVLNode currentNode, T key) {
        if (currentNode == null) {
            return currentNode;
        }

        if (key.compareTo(currentNode.data) < 0) {
            currentNode.left = deleteNode( currentNode.left, key);
        } else if (key.compareTo(currentNode.data) > 0) {
            currentNode.right = deleteNode( currentNode.right, key);
        } else {
            if ((currentNode.left == null) || (currentNode.right == null)) {
                AVLNode temp = (currentNode.left != null) ?  currentNode.left :  currentNode.right;

                if (temp == null) {
                    temp = currentNode;
                    currentNode = null;
                } else {
                    currentNode = temp;
                }
            } else {
                AVLNode temp = getMinChild( currentNode.right);
                currentNode.data = temp.data;
                currentNode.right = deleteNode( currentNode.right, temp.data);
            }
        }

        if (currentNode == null) {
            return currentNode;
        }

        updateHeight(currentNode);//.height = 1 + Math.max(height(currentNode.left), height(currentNode.right));

        return balance(currentNode);
//        int balance = balanceFactor(root);
//
//        if (balance > 1 && balanceFactor(root.left) >= 0) {
//            return rightRotate(root);
//        }
//        if (balance > 1 && balanceFactor(root.left) < 0) {
//            root.left = leftRotate(root.left);
//            return rightRotate(root);
//        }
//        if (balance < -1 && balanceFactor(root.right) <= 0) {
//            return leftRotate(root);
//        }
//        if (balance < -1 && balanceFactor(root.right) > 0) {
//            root.right = rightRotate(root.right);
//            return leftRotate(root);
//        }
//
//        return root;
    }

    private void inorderTraversal(AVLNode node) {
        if (node != null) {
            inorderTraversal( node.left);
            System.out.print(node.data + " ");
            inorderTraversal( node.right);
        }
    }

    private AVLNode searchNode(AVLNode node, T key) {
        if (node == null || node.data.equals(key)) {
            return node;
        }

        if (key.compareTo(node.data) < 0) {
            return searchNode( node.left, key);
        }

        return searchNode( node.right, key);
    }

    @Override
    public boolean insert(T element) {
        root = insertNode(root, element);
        return root != null;
    }

    @Override
    public T delete(T element) {
        root = deleteNode(root, element);
        return element;
    }

    @Override
    public T search(T element) {
        AVLNode node = searchNode(root, element);
        return (node != null) ? node.data : null;
    }

    public void printTree() {
        inorderTraversal(root);
//        System.out.println();
    }
}
