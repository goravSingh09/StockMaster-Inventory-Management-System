package com.stockmaster.dsa;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * CUSTOM HASH TABLE IMPLEMENTATION WITH SEPARATE CHAINING
 * ============================================================================
 * Academic Topic: Data Structures & Algorithms - Hash Tables & Collision Resolution
 *
 * CONCEPT & WORKING:
 * A Hash Table is a data structure that maps keys to values for efficient lookup.
 * A mathematical Hash Function transforms a key (e.g. Product Code "PRD-1001")
 * into an integer array index: index = abs(hash(key)) % bucketCapacity.
 *
 * COLLISION RESOLUTION (Separate Chaining):
 * When two different keys produce the identical bucket index (collision), both
 * entries are stored in a Singly Linked List (HashNode chain) at that bucket.
 *
 * DYNAMIC RESIZING & REHASHING:
 * When the Load Factor (size / capacity) exceeds the threshold (0.75), the internal
 * table capacity is doubled (multiplied by 2) and all existing nodes are rehashed
 * to preserve O(1) average lookup performance.
 *
 * COMPLEXITY:
 * - Average Time Complexity:
 *     * Insertion (put): O(1)
 *     * Lookup (get): O(1)
 *     * Deletion (remove): O(1)
 * - Worst-Case Time Complexity (all keys collide into single bucket): O(N)
 * - Space Complexity: O(N + M) where N = number of elements, M = table capacity
 *
 * INVENTORY INTEGRATION:
 * Used in StockMaster for instant O(1) primary key lookup of products by Product Code.
 */
public class HashTable<K, V> {

    /**
     * Singly Linked List node representing a key-value pair inside a bucket chain.
     */
    public static class HashNode<K, V> {
        private final K key;
        private V value;
        private HashNode<K, V> next;

        public HashNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public HashNode<K, V> getNext() {
            return next;
        }

        public void setNext(HashNode<K, V> next) {
            this.next = next;
        }
    }

    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR_THRESHOLD = 0.75f;

    private HashNode<K, V>[] buckets;
    private int capacity;
    private int size;
    private int totalCollisions;

    @SuppressWarnings("unchecked")
    public HashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = initialCapacity;
        this.buckets = (HashNode<K, V>[]) new HashNode[capacity];
        this.size = 0;
        this.totalCollisions = 0;
    }

    public HashTable() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Hash function that distributes keys evenly across buckets.
     * Uses bit manipulation to ensure non-negative bucket indices.
     */
    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        // High-bit mixing to protect against poor hashCode implementations
        h = h ^ (h >>> 16);
        return Math.abs(h % capacity);
    }

    /**
     * Inserts or updates a key-value mapping.
     * If key already exists, updates the value and returns old value.
     * If key is new, prepends node to chain and increments size.
     */
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys are not supported in this HashTable");
        }

        // Check if rehashing is required
        if ((float) size / capacity >= LOAD_FACTOR_THRESHOLD) {
            rehash();
        }

        int index = hash(key);
        HashNode<K, V> head = buckets[index];

        // Traverse existing chain to check for duplicate key (update case)
        HashNode<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                V oldValue = current.value;
                current.value = value; // Update existing value
                return oldValue;
            }
            current = current.next;
        }

        // Key does not exist: insert new node at the head of the bucket chain
        if (head != null) {
            totalCollisions++; // A collision occurred because bucket already contains elements
        }

        HashNode<K, V> newNode = new HashNode<>(key, value);
        newNode.next = head;
        buckets[index] = newNode;
        size++;

        return null;
    }

    /**
     * Searches and retrieves value associated with key.
     * Returns null if key is not found.
     */
    public V get(K key) {
        if (key == null) {
            return null;
        }

        int index = hash(key);
        HashNode<K, V> current = buckets[index];

        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }

        return null;
    }

    /**
     * Checks if key exists in hash table.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Removes key-value pair from hash table.
     * Returns deleted value, or null if key was not found.
     */
    public V remove(K key) {
        if (key == null) {
            return null;
        }

        int index = hash(key);
        HashNode<K, V> current = buckets[index];
        HashNode<K, V> prev = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (prev == null) {
                    // Removing head of bucket
                    buckets[index] = current.next;
                } else {
                    // Bypass the current node
                    prev.next = current.next;
                }
                size--;
                return current.value;
            }
            prev = current;
            current = current.next;
        }

        return null;
    }

    /**
     * Rehashes all entries when table exceeds load factor threshold.
     * Doubles capacity to O(2 * capacity) and re-inserts all existing nodes.
     */
    @SuppressWarnings("unchecked")
    private void rehash() {
        int oldCapacity = capacity;
        HashNode<K, V>[] oldBuckets = buckets;

        this.capacity = oldCapacity * 2;
        this.buckets = (HashNode<K, V>[]) new HashNode[capacity];
        this.size = 0;
        this.totalCollisions = 0;

        for (int i = 0; i < oldCapacity; i++) {
            HashNode<K, V> current = oldBuckets[i];
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }

    /**
     * Returns a list of all values in the hash table.
     */
    public List<V> values() {
        List<V> list = new ArrayList<>(size);
        for (int i = 0; i < capacity; i++) {
            HashNode<K, V> current = buckets[i];
            while (current != null) {
                list.add(current.value);
                current = current.next;
            }
        }
        return list;
    }

    /**
     * Returns a list of all keys in the hash table.
     */
    public List<K> keys() {
        List<K> list = new ArrayList<>(size);
        for (int i = 0; i < capacity; i++) {
            HashNode<K, V> current = buckets[i];
            while (current != null) {
                list.add(current.key);
                current = current.next;
            }
        }
        return list;
    }

    /**
     * Clears all entries from the hash table.
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        this.capacity = DEFAULT_INITIAL_CAPACITY;
        this.buckets = (HashNode<K, V>[]) new HashNode[capacity];
        this.size = 0;
        this.totalCollisions = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getTotalCollisions() {
        return totalCollisions;
    }

    public float getCurrentLoadFactor() {
        return (float) size / capacity;
    }
}
