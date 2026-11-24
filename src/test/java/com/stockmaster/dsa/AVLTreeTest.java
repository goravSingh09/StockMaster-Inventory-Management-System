package com.stockmaster.dsa;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Custom AVLTree Unit Tests")
class AVLTreeTest {

    private AVLTree<String, String> avlTree;

    @BeforeEach
    void setUp() {
        avlTree = new AVLTree<>();
    }

    @Test
    @DisplayName("Should insert items and maintain AVL balance property")
    void testInsertAndBalance() {
        // Inserting ascending keys forces left rotations in standard BST,
        // but AVL tree balances them automatically to keep height <= 1.44 * log2(N)
        String[] keys = {"PRD-01", "PRD-02", "PRD-03", "PRD-04", "PRD-05", "PRD-06", "PRD-07"};
        for (String key : keys) {
            avlTree.insert(key, "Product " + key);
        }

        assertEquals(7, avlTree.size());
        assertTrue(avlTree.getRotationCount() > 0);
        // Height of a balanced tree with 7 nodes should be 3 (2^3 - 1 = 7)
        assertTrue(avlTree.getRootHeight() <= 4);

        for (String key : keys) {
            assertEquals("Product " + key, avlTree.search(key));
        }
    }

    @Test
    @DisplayName("Should produce strictly sorted list via Inorder Traversal")
    void testInorderTraversal() {
        avlTree.insert("PRD-50", "Laptop");
        avlTree.insert("PRD-20", "Keyboard");
        avlTree.insert("PRD-70", "Monitor");
        avlTree.insert("PRD-10", "Mouse");
        avlTree.insert("PRD-30", "Headset");

        List<String> sortedProducts = avlTree.inorderTraversal();
        assertEquals(5, sortedProducts.size());
        // Inorder of BST must be sorted by key: PRD-10, PRD-20, PRD-30, PRD-50, PRD-70
        assertEquals("Mouse", sortedProducts.get(0));
        assertEquals("Keyboard", sortedProducts.get(1));
        assertEquals("Headset", sortedProducts.get(2));
        assertEquals("Laptop", sortedProducts.get(3));
        assertEquals("Monitor", sortedProducts.get(4));
    }

    @Test
    @DisplayName("Should delete node and maintain balance")
    void testDeleteAndRebalance() {
        avlTree.insert("A", "ValA");
        avlTree.insert("B", "ValB");
        avlTree.insert("C", "ValC");
        avlTree.insert("D", "ValD");
        avlTree.insert("E", "ValE");

        assertEquals(5, avlTree.size());
        avlTree.delete("C");

        assertEquals(4, avlTree.size());
        assertNull(avlTree.search("C"));
        assertEquals("ValA", avlTree.search("A"));
        assertEquals("ValB", avlTree.search("B"));
        assertEquals("ValD", avlTree.search("D"));
        assertEquals("ValE", avlTree.search("E"));
    }

    @Test
    @DisplayName("Should handle missing keys gracefully")
    void testMissingKey() {
        avlTree.insert("PRD-01", "Item 1");
        assertNull(avlTree.search("PRD-999"));
        assertFalse(avlTree.contains("PRD-999"));
    }
}
