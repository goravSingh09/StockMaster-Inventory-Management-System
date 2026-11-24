package com.stockmaster.dsa;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * CUSTOM AVL TREE IMPLEMENTATION (SELF-BALANCING BINARY SEARCH TREE)
 * ============================================================================
 * Academic Topic: Data Structures & Algorithms - Self-Balancing BST
 * Inventors: Georgy Adelson-Velsky and Evgenii Landis (1962)
 *
 * CONCEPT & WORKING:
 * An AVL Tree is a Binary Search Tree (BST) where the difference between
 * heights of left and right subtrees for ANY node (Balance Factor) cannot exceed 1.
 *
 * Balance Factor = Height(Left Subtree) - Height(Right Subtree)
 * Valid balance factors for an AVL tree: -1, 0, +1.
 *
 * When an insertion or deletion causes Balance Factor to become > +1 or < -1,
 * tree rotations restore balance immediately:
 *
 * 1. Left-Left (LL) Heavy: Fixed by a Single RIGHT ROTATION.
 * 2. Right-Right (RR) Heavy: Fixed by a Single LEFT ROTATION.
 * 3. Left-Right (LR) Heavy: Fixed by a Double Rotation (LEFT rotation on child, then RIGHT rotation on parent).
 * 4. Right-Left (RL) Heavy: Fixed by a Double Rotation (RIGHT rotation on child, then LEFT rotation on parent).
 *
 * INORDER TRAVERSAL:
 * Traverses Left Subtree -> Root -> Right Subtree. In a BST, inorder traversal
 * produces items in strictly SORTED order in O(N) time without additional sorting!
 *
 * COMPLEXITY:
 * - Search: O(log N) [Guaranteed worst-case because height is strictly bounded by 1.44 * log2(N)]
 * - Insertion: O(log N)
 * - Deletion: O(log N)
 * - Space Complexity: O(N)
 *
 * INVENTORY INTEGRATION:
 * Used in StockMaster for ordered product retrieval, range queries, and log(N)
 * searching by Product Code and Name.
 */
public class AVLTree<K extends Comparable<K>, V> {

    /**
     * Node representing an element in the AVL Tree.
     */
    public static class AVLNode<K extends Comparable<K>, V> {
        private K key;
        private V value;
        private int height;
        private AVLNode<K, V> left;
        private AVLNode<K, V> right;

        public AVLNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 1; // New leaf node starts with height 1
            this.left = null;
            this.right = null;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public int getHeight() {
            return height;
        }

        public AVLNode<K, V> getLeft() {
            return left;
        }

        public AVLNode<K, V> getRight() {
            return right;
        }
    }

    private AVLNode<K, V> root;
    private int size;
    private int rotationCount;

    public AVLTree() {
        this.root = null;
        this.size = 0;
        this.rotationCount = 0;
    }

    /**
     * Returns the height of a given node (0 if null).
     */
    public int height(AVLNode<K, V> node) {
        return (node == null) ? 0 : node.height;
    }

    /**
     * Calculates the balance factor of a node:
     * Balance Factor = Height(Left) - Height(Right).
     */
    public int getBalanceFactor(AVLNode<K, V> node) {
        if (node == null) {
            return 0;
        }
        return height(node.left) - height(node.right);
    }

    /**
     * Updates the height of a node based on its children's heights.
     */
    private void updateHeight(AVLNode<K, V> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    /**
     * RIGHT ROTATION (LL Case):
     *
     *        y                               x
     *       / \     Right Rotation          / \
     *      x   T3   -------------->        T1   y
     *     / \                                  / \
     *    T1  T2                               T2  T3
     */
    private AVLNode<K, V> rightRotate(AVLNode<K, V> y) {
        AVLNode<K, V> x = y.left;
        AVLNode<K, V> T2 = x.right;

        // Perform rotation
        x.right = y;
        y.left = T2;

        // Update heights (order matters: children first)
        updateHeight(y);
        updateHeight(x);

        rotationCount++;
        return x; // New root of rotated subtree
    }

    /**
     * LEFT ROTATION (RR Case):
     *
     *      x                                 y
     *     / \       Left Rotation           / \
     *    T1  y      ------------->         x   T3
     *       / \                           / \
     *      T2  T3                        T1  T2
     */
    private AVLNode<K, V> leftRotate(AVLNode<K, V> x) {
        AVLNode<K, V> y = x.right;
        AVLNode<K, V> T2 = y.left;

        // Perform rotation
        y.left = x;
        x.right = T2;

        // Update heights
        updateHeight(x);
        updateHeight(y);

        rotationCount++;
        return y; // New root of rotated subtree
    }

    /**
     * Public method to insert a key-value pair into the AVL tree.
     */
    public void insert(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys are not supported in AVLTree");
        }
        root = insertRecursive(root, key, value);
    }

    /**
     * Recursive helper for insertion and rebalancing.
     */
    private AVLNode<K, V> insertRecursive(AVLNode<K, V> node, K key, V value) {
        // Step 1: Standard BST insertion
        if (node == null) {
            size++;
            return new AVLNode<>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insertRecursive(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertRecursive(node.right, key, value);
        } else {
            // Duplicate key: update value in place
            node.value = value;
            return node;
        }

        // Step 2: Update height of ancestor node
        updateHeight(node);

        // Step 3: Check balance factor to determine if unbalanced
        int balance = getBalanceFactor(node);

        // Case 1: Left Left (LL) Heavy -> Right Rotate
        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            return rightRotate(node);
        }

        // Case 2: Right Right (RR) Heavy -> Left Rotate
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            return leftRotate(node);
        }

        // Case 3: Left Right (LR) Heavy -> Left Rotate left child, then Right Rotate node
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Case 4: Right Left (RL) Heavy -> Right Rotate right child, then Left Rotate node
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        // Return the (possibly balanced) node pointer
        return node;
    }

    /**
     * Searches for a value associated with key.
     * Returns value if found, null otherwise.
     */
    public V search(K key) {
        if (key == null) {
            return null;
        }
        AVLNode<K, V> node = searchRecursive(root, key);
        return (node != null) ? node.value : null;
    }

    private AVLNode<K, V> searchRecursive(AVLNode<K, V> node, K key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return searchRecursive(node.left, key);
        } else {
            return searchRecursive(node.right, key);
        }
    }

    /**
     * Checks if key exists in the AVL Tree.
     */
    public boolean contains(K key) {
        return search(key) != null;
    }

    /**
     * Deletes a key-value pair from the AVL tree and rebalances.
     */
    public void delete(K key) {
        if (key == null) {
            return;
        }
        root = deleteRecursive(root, key);
    }

    private AVLNode<K, V> deleteRecursive(AVLNode<K, V> node, K key) {
        if (node == null) {
            return null; // Key not found
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = deleteRecursive(node.left, key);
        } else if (cmp > 0) {
            node.right = deleteRecursive(node.right, key);
        } else {
            // Found node to delete
            size--;

            // Case 1 & 2: Node with only one child or no child
            if (node.left == null || node.right == null) {
                AVLNode<K, V> temp = (node.left != null) ? node.left : node.right;
                if (temp == null) {
                    // No child case
                    node = null;
                } else {
                    // One child case
                    node = temp;
                }
            } else {
                // Case 3: Node with two children
                // Get inorder successor (smallest in the right subtree)
                AVLNode<K, V> successor = minValueNode(node.right);
                node.key = successor.key;
                node.value = successor.value;
                // Delete successor from right subtree
                node.right = deleteRecursive(node.right, successor.key);
                // Balance size counter decrement compensation
                size++;
            }
        }

        // If tree had only one node and it was deleted
        if (node == null) {
            return null;
        }

        // Update height
        updateHeight(node);

        // Check balance factor
        int balance = getBalanceFactor(node);

        // Case 1: Left Left (LL)
        if (balance > 1 && getBalanceFactor(node.left) >= 0) {
            return rightRotate(node);
        }

        // Case 2: Left Right (LR)
        if (balance > 1 && getBalanceFactor(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        // Case 3: Right Right (RR)
        if (balance < -1 && getBalanceFactor(node.right) <= 0) {
            return leftRotate(node);
        }

        // Case 4: Right Left (RL)
        if (balance < -1 && getBalanceFactor(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    /**
     * Finds node with minimum key value in a subtree.
     */
    private AVLNode<K, V> minValueNode(AVLNode<K, V> node) {
        AVLNode<K, V> current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    /**
     * Performs an Inorder Traversal (Left -> Root -> Right)
     * Returns all values in strictly ASCENDING sorted order of their keys.
     */
    public List<V> inorderTraversal() {
        List<V> result = new ArrayList<>(size);
        inorderHelper(root, result);
        return result;
    }

    private void inorderHelper(AVLNode<K, V> node, List<V> result) {
        if (node != null) {
            inorderHelper(node.left, result);
            result.add(node.value);
            inorderHelper(node.right, result);
        }
    }

    /**
     * Clears the tree.
     */
    public void clear() {
        this.root = null;
        this.size = 0;
        this.rotationCount = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getRootHeight() {
        return height(root);
    }

    public int getRotationCount() {
        return rotationCount;
    }
}
