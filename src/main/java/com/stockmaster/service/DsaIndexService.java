package com.stockmaster.service;

import com.stockmaster.dsa.AVLTree;
import com.stockmaster.dsa.HashTable;
import com.stockmaster.dsa.MaxHeap;
import com.stockmaster.dto.ProductSalesDto;
import com.stockmaster.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ============================================================================
 * IN-MEMORY DSA INDEX SERVICE
 * ============================================================================
 * Serves as the central bridge between persistent database storage and the
 * custom Core Java Data Structures:
 *
 * 1. HashTable<String, Product>: Primary Key / Product Code lookup in O(1) time.
 * 2. AVLTree<String, Product>: Height-balanced BST for O(log N) lookup and sorted inorder traversal.
 * 3. MaxHeap<ProductSalesDto>: Real-time ranking of best-selling products in O(K log N) time.
 *
 * Synchronized with MySQL database on application boot and on every CRUD / Stock / Sale event.
 */
@Service
public class DsaIndexService {

    private static final Logger log = LoggerFactory.getLogger(DsaIndexService.class);

    private final HashTable<String, Product> productHashTable = new HashTable<>(64);
    private final AVLTree<String, Product> productAVLTree = new AVLTree<>();
    private final MaxHeap<ProductSalesDto> bestSellingMaxHeap =
            new MaxHeap<>(Comparator.comparingInt(ProductSalesDto::getUnitsSold));

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();

    /**
     * Fully rebuilds in-memory data structures from persistent database records.
     */
    public void rebuildAll(List<Product> products, List<ProductSalesDto> salesData) {
        rwLock.writeLock().lock();
        try {
            productHashTable.clear();
            productAVLTree.clear();
            bestSellingMaxHeap.clear();

            if (products != null) {
                for (Product product : products) {
                    productHashTable.put(product.getProductCode(), product);
                    productAVLTree.insert(product.getProductCode(), product);
                }
            }

            if (salesData != null && !salesData.isEmpty()) {
                bestSellingMaxHeap.buildHeap(salesData);
            }

            log.info("DSA In-Memory Indexes initialized: {} products in HashTable & AVLTree, {} sales in MaxHeap.",
                    productHashTable.size(), bestSellingMaxHeap.size());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Inserts or updates a product in both HashTable and AVLTree.
     */
    public void indexProduct(Product product) {
        if (product == null || product.getProductCode() == null) {
            return;
        }
        rwLock.writeLock().lock();
        try {
            productHashTable.put(product.getProductCode(), product);
            productAVLTree.insert(product.getProductCode(), product);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Removes a product from HashTable and AVLTree.
     */
    public void removeProduct(String productCode) {
        if (productCode == null) {
            return;
        }
        rwLock.writeLock().lock();
        try {
            productHashTable.remove(productCode);
            productAVLTree.delete(productCode);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * O(1) Hash Table Lookup by Product Code.
     */
    public Product searchByHashTable(String productCode) {
        rwLock.readLock().lock();
        try {
            return productHashTable.get(productCode);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * O(log N) AVL Tree Lookup by Product Code.
     */
    public Product searchByAVLTree(String productCode) {
        rwLock.readLock().lock();
        try {
            return productAVLTree.search(productCode);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Returns products in sorted Product Code order using AVL Inorder Traversal.
     */
    public List<Product> getSortedProductsFromAVL() {
        rwLock.readLock().lock();
        try {
            return productAVLTree.inorderTraversal();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Updates the sales ranking MaxHeap.
     */
    public void updateSalesHeap(List<ProductSalesDto> salesData) {
        rwLock.writeLock().lock();
        try {
            bestSellingMaxHeap.buildHeap(salesData);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Extracts Top-K Best Selling products from MaxHeap in O(K log N) time.
     */
    public List<ProductSalesDto> getTopSellingProducts(int k) {
        rwLock.readLock().lock();
        try {
            return bestSellingMaxHeap.getTopK(k);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public int getHashTableSize() {
        return productHashTable.size();
    }

    public int getAVLTreeHeight() {
        return productAVLTree.getRootHeight();
    }

    public int getHashTableCollisions() {
        return productHashTable.getTotalCollisions();
    }
}
