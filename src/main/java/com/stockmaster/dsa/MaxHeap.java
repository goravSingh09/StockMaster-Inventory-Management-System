package com.stockmaster.dsa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ============================================================================
 * CUSTOM MAX HEAP IMPLEMENTATION (PRIORITY QUEUE)
 * ============================================================================
 * Academic Topic: Data Structures & Algorithms - Heaps & Priority Queues
 *
 * CONCEPT & WORKING:
 * A Max Heap is a Complete Binary Tree where every parent node has a key
 * GREATER THAN OR EQUAL TO its child nodes (Max-Heap Property):
 * A[parent(i)] >= A[i]
 *
 * ARRAY REPRESENTATION:
 * For any node at 0-based array index `i`:
 * - Parent index:       (i - 1) / 2
 * - Left child index:   2 * i + 1
 * - Right child index:  2 * i + 2
 *
 * OPERATIONS:
 * 1. Insert: Appends element to end and performs `heapifyUp()` (Bubble-up) in O(log N).
 * 2. ExtractMax: Removes the root element, moves the last leaf to the root,
 *    and restores max-heap property via `heapifyDown()` (Sink-down) in O(log N).
 * 3. BuildHeap: Converts an unordered list into a valid max heap in O(N) linear time
 *    using Floyd's bottom-up heap construction algorithm.
 * 4. Top-K Selection: Extracts the top K elements in O(K log N) time without sorting
 *    the entire dataset (much faster than O(N log N) full sorting when K << N).
 *
 * COMPLEXITY:
 * - Insert: O(log N)
 * - ExtractMax: O(log N)
 * - PeekMax: O(1)
 * - BuildHeap: O(N)
 * - Top-K Products: O(K log N)
 * - Space Complexity: O(N)
 *
 * INVENTORY INTEGRATION:
 * Used in StockMaster to continuously rank and retrieve the Best-Selling Products
 * (Top 5 / Top 10 by Units Sold & Revenue) without sorting the entire product catalog.
 */
public class MaxHeap<T> {

    private final ArrayList<T> heap;
    private final Comparator<T> comparator;

    /**
     * Initializes a Max Heap with a custom comparator.
     */
    public MaxHeap(Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }
        this.heap = new ArrayList<>();
        this.comparator = comparator;
    }

    /**
     * Private copy constructor for non-destructive operations.
     */
    private MaxHeap(ArrayList<T> existingList, Comparator<T> comparator) {
        this.heap = new ArrayList<>(existingList);
        this.comparator = comparator;
    }

    /**
     * Inserts an item into the max heap.
     * Appends to bottom and bubbles up to restore max-heap property.
     */
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Null items are not allowed in MaxHeap");
        }
        heap.add(item);
        heapifyUp(heap.size() - 1);
    }

    /**
     * Heapify Up (Bubble-up):
     * Restores heap property by swapping child with parent if child > parent.
     */
    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (comparator.compare(heap.get(index), heap.get(parentIndex)) > 0) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    /**
     * Extracts and removes the maximum element (the root of the heap).
     */
    public T extractMax() {
        if (isEmpty()) {
            throw new IllegalStateException("Cannot extract from an empty MaxHeap");
        }

        T maxItem = heap.get(0);
        int lastIndex = heap.size() - 1;
        T lastItem = heap.remove(lastIndex);

        if (!heap.isEmpty()) {
            heap.set(0, lastItem);
            heapifyDown(0);
        }

        return maxItem;
    }

    /**
     * Heapify Down (Sink-down):
     * Compares parent with left and right children, swaps with the largest child.
     */
    private void heapifyDown(int index) {
        int size = heap.size();
        while (index < size) {
            int largest = index;
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            if (leftChild < size && comparator.compare(heap.get(leftChild), heap.get(largest)) > 0) {
                largest = leftChild;
            }

            if (rightChild < size && comparator.compare(heap.get(rightChild), heap.get(largest)) > 0) {
                largest = rightChild;
            }

            if (largest != index) {
                swap(index, largest);
                index = largest;
            } else {
                break;
            }
        }
    }

    /**
     * Returns the maximum element without removing it.
     */
    public T peekMax() {
        if (isEmpty()) {
            return null;
        }
        return heap.get(0);
    }

    /**
     * Builds a max heap from an existing list in O(N) linear time
     * using Floyd's bottom-up construction algorithm.
     */
    public void buildHeap(List<T> items) {
        heap.clear();
        if (items == null || items.isEmpty()) {
            return;
        }
        heap.addAll(items);
        // Start heapifying from the last non-leaf node down to the root
        for (int i = (heap.size() / 2) - 1; i >= 0; i--) {
            heapifyDown(i);
        }
    }

    /**
     * Extracts the Top K elements in descending order without modifying the original heap.
     * Time Complexity: O(K log N) where N = total elements.
     */
    public List<T> getTopK(int k) {
        if (k <= 0) {
            return new ArrayList<>();
        }

        // Create a copy of the heap so original data structure remains intact
        MaxHeap<T> copyHeap = new MaxHeap<>(this.heap, this.comparator);
        List<T> topKList = new ArrayList<>();

        int count = Math.min(k, copyHeap.size());
        for (int i = 0; i < count; i++) {
            topKList.add(copyHeap.extractMax());
        }

        return topKList;
    }

    private void swap(int i, int j) {
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public int size() {
        return heap.size();
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public void clear() {
        heap.clear();
    }
}
