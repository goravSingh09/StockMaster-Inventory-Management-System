package com.stockmaster.dsa;

import java.util.List;
import java.util.function.Function;

/**
 * ============================================================================
 * CUSTOM LINEAR SEARCH IMPLEMENTATION (BASELINE SEARCH ALGORITHM)
 * ============================================================================
 * Academic Topic: Data Structures & Algorithms - Searching Algorithms
 *
 * CONCEPT & WORKING:
 * Linear Search is the simplest sequential search algorithm. It examines each
 * element in a collection one-by-one starting from index 0 until:
 * 1. A matching element is found (SUCCESS), or
 * 2. The entire collection has been traversed without match (NOT FOUND).
 *
 * It requires NO pre-sorting or special data structures.
 *
 * COMPLEXITY:
 * - Best Case Time: O(1) (matching element is at index 0)
 * - Average Case Time: O(N / 2) = O(N)
 * - Worst Case Time: O(N) (matching element at last index or absent)
 * - Space Complexity: O(1) auxiliary space
 *
 * INVENTORY INTEGRATION:
 * Used in StockMaster as the academic baseline comparator to demonstrate the
 * exponential performance superiority of Hash Table O(1), AVL Tree O(log N),
 * and Binary Search O(log N) over sequential O(N) iteration.
 */
public class LinearSearch {

    public static class SearchResult<T> {
        private final T foundItem;
        private final int index;
        private final int comparisons;
        private final long executionTimeNs;
        private final boolean found;

        public SearchResult(T foundItem, int index, int comparisons, long executionTimeNs) {
            this.foundItem = foundItem;
            this.index = index;
            this.comparisons = comparisons;
            this.executionTimeNs = executionTimeNs;
            this.found = (index != -1);
        }

        public T getFoundItem() {
            return foundItem;
        }

        public int getIndex() {
            return index;
        }

        public int getComparisons() {
            return comparisons;
        }

        public long getExecutionTimeNs() {
            return executionTimeNs;
        }

        public boolean isFound() {
            return found;
        }
    }

    /**
     * Executes manual sequential linear search.
     */
    public static <T, K> SearchResult<T> search(
            List<T> list,
            K targetKey,
            Function<T, K> keyExtractor) {

        long startTime = System.nanoTime();

        if (list == null || list.isEmpty() || targetKey == null) {
            long duration = System.nanoTime() - startTime;
            return new SearchResult<>(null, -1, 0, duration);
        }

        int comparisons = 0;
        for (int i = 0; i < list.size(); i++) {
            comparisons++;
            T item = list.get(i);
            K key = keyExtractor.apply(item);

            if (targetKey.equals(key)) {
                long duration = System.nanoTime() - startTime;
                return new SearchResult<>(item, i, comparisons, duration);
            }
        }

        long duration = System.nanoTime() - startTime;
        return new SearchResult<>(null, -1, comparisons, duration);
    }
}
