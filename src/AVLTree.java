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
        /*
             parent
              /                    pivot
            pivot         -->     /     \
            /                  child    parent
          child
         */
        AVLNode pivot =  parent.left;
        AVLNode child =  pivot.right;

        pivot.right = parent;
        parent.left = child;

        updateHeight(parent);// = Math.max(height( parent.left), height( parent.right)) + 1;
        updateHeight(pivot);// = Math.max(height( pivot.left), height( pivot.right)) + 1;

        return pivot;
    }

    private AVLNode leftRotate(AVLNode parent) {
        AVLNode pivot =  parent.right;
        AVLNode child =  pivot.left;

        pivot.left = parent;
        parent.right = child;

        updateHeight(parent);// = Math.max(height( parent.left), height( parent.right)) + 1;
        updateHeight(pivot);// = Math.max(height( pivot.left), height( pivot.right)) + 1;

        return pivot;
    }

    private AVLNode balance(AVLNode node) {
        int balanceFactor = balanceFactor(node);

        // Left Heavy (Right Rotation)
        if (balanceFactor > 1 && balanceFactor(node.left) >= 0) {
            return rightRotate(node);
        }

        // Left-Right Case (Left Rotate Left Child, then Right Rotate)
        if (balanceFactor > 1 && balanceFactor(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Right Heavy (Left Rotation)
        if (balanceFactor < -1 && balanceFactor(node.right) <= 0) {
            return leftRotate(node);
        }

        // Right-Left Case (Right Rotate Right Child, then Left Rotate)
        if (balanceFactor < -1 && balanceFactor(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node; // Return the balanced node
    }


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

    private AVLNode minValueNode(AVLNode node) {
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
                AVLNode temp = minValueNode( currentNode.right);
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
    public void insert(T element) {
        root = insertNode(root, element);
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
        System.out.println();
    }
}
