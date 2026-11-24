package com.stockmaster.dsa;

import java.util.List;
import java.util.function.Function;

/**
 * ============================================================================
 * CUSTOM BINARY SEARCH IMPLEMENTATION (ITERATIVE)
 * ============================================================================
 * Academic Topic: Data Structures & Algorithms - Searching Algorithms
 *
 * CONCEPT & WORKING:
 * Binary Search operates on an already SORTED list or array using the
 * Divide-and-Conquer paradigm.
 *
 * Algorithm Steps:
 * 1. Set low = 0, high = size - 1.
 * 2. Calculate mid = low + (high - low) / 2 (avoids integer overflow).
 * 3. Compare target key with element at mid:
 *    - If target == mid element: MATCH FOUND! Return item and index.
 *    - If target < mid element: Target lies in left half -> high = mid - 1.
 *    - If target > mid element: Target lies in right half -> low = mid + 1.
 * 4. Repeat until low > high (element does not exist -> return -1).
 *
 * Search Space Reduction:
 * At each comparison, half of the remaining elements are eliminated:
 * N -> N/2 -> N/4 -> ... -> 1 => log2(N) steps.
 *
 * COMPLEXITY:
 * - Best Case Time: O(1) (target element found at first mid calculation)
 * - Average Case Time: O(log N)
 * - Worst Case Time: O(log N) (target at leaf or absent)
 * - Space Complexity: O(1) auxiliary (iterative implementation uses zero recursion stack)
 *
 * PREREQUISITE:
 * The input dataset MUST be sorted beforehand (e.g. via MergeSort or QuickSort).
 *
 * INVENTORY INTEGRATION:
 * Used in StockMaster to search products by Product Code in sorted collections.
 */
public class BinarySearch {

    /**
     * Data Transfer Object holding the search outcome, comparisons made, and timing.
     */
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
     * Executes manual iterative binary search.
     *
     * @param sortedList  A pre-sorted list of elements
     * @param targetKey   The key to locate (e.g. Product Code)
     * @param keyExtractor Function extracting comparable key from item
     * @return SearchResult with match details and performance metrics
     */
    public static <T, K extends Comparable<K>> SearchResult<T> search(
            List<T> sortedList,
            K targetKey,
            Function<T, K> keyExtractor) {

        long startTime = System.nanoTime();

        if (sortedList == null || sortedList.isEmpty() || targetKey == null) {
            long duration = System.nanoTime() - startTime;
            return new SearchResult<>(null, -1, 0, duration);
        }

        int low = 0;
        int high = sortedList.size() - 1;
        int comparisons = 0;

        while (low <= high) {
            // Unsigned safe mid calculation to prevent 32-bit integer overflow
            int mid = low + ((high - low) >>> 1);
            T midItem = sortedList.get(mid);
            K midKey = keyExtractor.apply(midItem);

            comparisons++;
            int cmp = targetKey.compareTo(midKey);

            if (cmp == 0) {
                long duration = System.nanoTime() - startTime;
                return new SearchResult<>(midItem, mid, comparisons, duration);
            } else if (cmp < 0) {
                // Target is in the left half
                high = mid - 1;
            } else {
                // Target is in the right half
                low = mid + 1;
            }
        }

        // Element not found
        long duration = System.nanoTime() - startTime;
        return new SearchResult<>(null, -1, comparisons, duration);
    }
}
