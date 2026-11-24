package com.stockmaster.dsa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ============================================================================
 * CUSTOM QUICK SORT IMPLEMENTATION (MEDIAN-OF-THREE PIVOT PARTITIONING)
 * ============================================================================
 * Academic Topic: Data Structures & Algorithms - Sorting Algorithms
 * Inventor: Sir Tony Hoare (1959)
 *
 * CONCEPT & WORKING:
 * Quick Sort is a highly efficient, in-place Divide-and-Conquer sorting algorithm.
 *
 * Algorithm Steps:
 * 1. Pivot Selection: Choose a pivot element from the subarray.
 *    In our implementation, we use "Median-of-Three" (first, middle, last elements)
 *    to prevent the catastrophic O(N^2) degradation on sorted or reverse-sorted inputs.
 * 2. Partition: Rearrange the subarray such that all elements smaller than pivot
 *    are placed to its left, and all elements larger are placed to its right.
 *    The pivot reaches its final sorted position.
 * 3. Recursion: Recursively apply Quick Sort to the sub-arrays left and right of pivot.
 *
 * COMPLEXITY:
 * - Best Case Time:    O(N log N) (pivot consistently divides subarray in half)
 * - Average Case Time: O(N log N) [With low constant factor, often faster than MergeSort]
 * - Worst Case Time:   O(N^2) (prevented by median-of-three pivot selection)
 * - Space Complexity:  O(log N) auxiliary recursion call stack (in-place data sorting)
 *
 * INVENTORY INTEGRATION:
 * Used in StockMaster for in-place high-speed sorting and benchmarking comparisons
 * against Merge Sort across diverse dataset sizes and distributions.
 */
public class QuickSort {

    /**
     * Sorts the provided list in-place using a custom comparator.
     */
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1 || comparator == null) {
            return;
        }
        quickSort(list, 0, list.size() - 1, comparator);
    }

    /**
     * Returns a new sorted copy of the list without modifying the original.
     */
    public static <T> List<T> sortedCopy(List<T> list, Comparator<T> comparator) {
        if (list == null) {
            return new ArrayList<>();
        }
        List<T> copy = new ArrayList<>(list);
        sort(copy, comparator);
        return copy;
    }

    private static <T> void quickSort(List<T> list, int low, int high, Comparator<T> comparator) {
        if (low < high) {
            int pivotIndex = partition(list, low, high, comparator);
            quickSort(list, low, pivotIndex - 1, comparator);
            quickSort(list, pivotIndex + 1, high, comparator);
        }
    }

    /**
     * Partitioning using Median-of-Three pivot selection and Hoare-style partitioning.
     */
    private static <T> int partition(List<T> list, int low, int high, Comparator<T> comparator) {
        // Median-of-Three pivot selection
        int mid = low + ((high - low) >>> 1);

        // Sort low, mid, high elements
        if (comparator.compare(list.get(low), list.get(mid)) > 0) {
            swap(list, low, mid);
        }
        if (comparator.compare(list.get(low), list.get(high)) > 0) {
            swap(list, low, high);
        }
        if (comparator.compare(list.get(mid), list.get(high)) > 0) {
            swap(list, mid, high);
        }

        // Place median pivot at high - 1 position
        swap(list, mid, high);
        T pivot = list.get(high);

        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (comparator.compare(list.get(j), pivot) <= 0) {
                i++;
                swap(list, i, j);
            }
        }

        // Place pivot at its final sorted partition position
        swap(list, i + 1, high);
        return i + 1;
    }

    private static <T> void swap(List<T> list, int i, int j) {
        T temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
