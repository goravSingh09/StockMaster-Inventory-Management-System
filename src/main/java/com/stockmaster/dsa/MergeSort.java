package com.stockmaster.dsa;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * ============================================================================
 * CUSTOM MERGE SORT IMPLEMENTATION (DIVIDE AND CONQUER)
 * ============================================================================
 * Academic Topic: Data Structures & Algorithms - Sorting Algorithms
 * Inventor: John von Neumann (1945)
 *
 * CONCEPT & WORKING:
 * Merge Sort is a classic recursive Divide-and-Conquer sorting algorithm.
 *
 * Algorithm Steps:
 * 1. Divide: Split the unsorted list in half at the midpoint until sublists
 *    contain 1 element (base case).
 * 2. Conquer: Recursively sort each of the two halves.
 * 3. Combine (Merge): Merge the two sorted halves into one sorted list by
 *    repeatedly comparing the smallest elements of each sublist.
 *
 * STABILITY:
 * Merge Sort is a STABLE sort: duplicate keys maintain their original relative
 * order (crucial when sorting products by multiple secondary attributes).
 *
 * RECURRENCE RELATION:
 * T(N) = 2T(N/2) + O(N)
 * By Master Theorem Case 2: T(N) = O(N log2 N).
 *
 * COMPLEXITY:
 * - Best Case Time:    O(N log N)
 * - Average Case Time: O(N log N)
 * - Worst Case Time:   O(N log N) [Guaranteed under all data distributions]
 * - Space Complexity:  O(N) auxiliary space for the temporary merge buffer
 *
 * INVENTORY INTEGRATION:
 * Used in StockMaster for sorting products by Selling Price, Available Quantity,
 * and Name with guaranteed O(N log N) performance and strict stability.
 */
public class MergeSort {

    /**
     * Sorts the provided list in-place using a custom comparator.
     */
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        if (list == null || list.size() <= 1 || comparator == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        T[] aux = (T[]) new Object[list.size()];
        mergeSort(list, aux, 0, list.size() - 1, comparator);
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

    private static <T> void mergeSort(List<T> list, T[] aux, int left, int right, Comparator<T> comparator) {
        if (left >= right) {
            return; // Base case: single element
        }

        int mid = left + ((right - left) >>> 1);

        // Recursively sort left and right halves
        mergeSort(list, aux, left, mid, comparator);
        mergeSort(list, aux, mid + 1, right, comparator);

        // Optimization: if already sorted, skip merge step
        if (comparator.compare(list.get(mid), list.get(mid + 1)) <= 0) {
            return;
        }

        // Merge the two sorted halves
        merge(list, aux, left, mid, right, comparator);
    }

    private static <T> void merge(List<T> list, T[] aux, int left, int mid, int right, Comparator<T> comparator) {
        // Copy elements into auxiliary array
        for (int k = left; k <= right; k++) {
            aux[k] = list.get(k);
        }

        int i = left;      // Pointer for left sublist [left..mid]
        int j = mid + 1;   // Pointer for right sublist [mid+1..right]
        int k = left;      // Destination pointer in original list

        while (i <= mid && j <= right) {
            // Note: <= ensures stability
            if (comparator.compare(aux[i], aux[j]) <= 0) {
                list.set(k++, aux[i++]);
            } else {
                list.set(k++, aux[j++]);
            }
        }

        // Copy remaining elements of left sublist (if any)
        while (i <= mid) {
            list.set(k++, aux[i++]);
        }

        // Note: remaining elements of right sublist are already in their correct positions
    }
}
